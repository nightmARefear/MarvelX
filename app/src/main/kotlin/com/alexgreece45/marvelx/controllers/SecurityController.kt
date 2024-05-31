package com.alexgreece45.marvelx.controllers

import android.os.Build
import android.os.Debug
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.model.security.ApplicationSecurity
import com.alexgreece45.marvelx.controllers.model.security.SecurityCheck
import com.alexgreece45.marvelx.controllers.model.security.SecurityViolation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

interface SecurityController {
    /**
     * Checks whether the package signature is valid, and returns [SecurityCheck.Passed] if it is,
     * otherwise [SecurityCheck.Failed] with [SecurityViolation.PackageSignatureInvalid].
     */
    fun isPackageSignatureValid(): SecurityCheck

    /**
     * Checks whether the application allows debugging of its code, even when installed on a
     * non-development system. Comes from `android:debuggable` of the `<application>` tag in
     * `AndroidManifest`.
     *
     * @return Returns [SecurityCheck.Passed] if debugging is disabled, otherwise
     * [SecurityCheck.Failed] with [SecurityViolation.DebuggableApp].
     */
    fun isAppDebuggable(): SecurityCheck

    /**
     * Checks whether a debugger has been attached to the application, and returns
     * [SecurityCheck.Failed] with [SecurityViolation.DebuggerAttached], otherwise
     * [SecurityCheck.Passed].
     */
    fun isDebuggerAttached(): SecurityCheck

    /**
     * Checks whether the app is running on an emulator, and returns [SecurityCheck.Failed] with
     * [SecurityViolation.RunningOnEmulator], otherwise [SecurityCheck.Passed].
     *
     * ### Note: This condition doesn't guarantee that it can always be detected if the app is running on an emulator.
     */
    fun isAppRunningOnEmulator(): SecurityCheck

    /**
     * Checks whether the device is rooted, and returns [SecurityCheck.Failed] with
     * [SecurityViolation.DeviceRooted] if it is, otherwise [SecurityCheck.Passed].
     *
     * ### Note: This condition doesn't guarantee that a rooted device can always be detected.
     */
    fun isDeviceRooted(): SecurityCheck

    /**
     * Checks whether the device has root cloaking apps installed, and returns [SecurityCheck.Failed]
     * with [SecurityViolation.RootCloakingAppsInstalled] if it has, otherwise [SecurityCheck.Passed].
     *
     * ### Note: This condition doesn't guarantee that installed root cloaking apps can always be detected.
     */
    fun areRootCloakingAppsInstalled(): SecurityCheck

    /**
     * Checks whether an external process has been attached and returns [SecurityCheck.Failed] with
     * [SecurityViolation.ExternalProcessAttached] if it has, otherwise [SecurityCheck.Passed].
     */
    fun isExternalProcessAttached(): SecurityCheck

    /**
     * Defines whether the CRC signature of the generated DEX files of this application is valid.
     * If the signature is valid then [SecurityCheck.Passed] is returned, otherwise
     * [SecurityCheck.Failed] with [SecurityViolation.AppDexCrcInvalid].
     */
    fun isApplicationDexCrcValid(): SecurityCheck

    /**
     * Defines whether screen capturing is blocked in this application.
     */
    fun isScreenCapturingBlocked(): Boolean

    /**
     * Defines whether overlay windows are hidden to display over this application.
     */
    fun areOverlayWindowsHidden(): Boolean

    /**
     * Defines whether this application should have the ability to draw itself as an overlay window
     * over other applications.
     */
    fun shouldApplicationDrawOverlayWindows(): Boolean

    /**
     * Defines whether this application should have the ability to be in multi window mode.
     */
    fun shouldApplicationBeInMultiWindowMode(): Boolean

    /**
     * Defines whether the auto logout functionality is enabled.
     */
    fun isAutoLogoutEnabled(): Boolean

    /**
     * Checks whether this application is running on a secure device and/or if it has been tampered
     * with. It returns [ApplicationSecurity.Secure] if the application is secure, otherwise
     * [ApplicationSecurity.Insecure] with the first [SecurityViolation] that was detected.
     *
     * ### Note: This condition doesn't guarantee that the device is secure, or that the application hasn't been tampered with.
     */
    fun isApplicationSecure(): Flow<ApplicationSecurity>
}

class SecurityControllerImpl(
    private val configSecurityLogic: ConfigSecurityLogic,
    private val rootController: RootController,
    private val applicationPackageController: ApplicationPackageController,
    private val antiReverseEngineeringController: AntiReverseEngineeringController,
    private val antiHookController: AntiHookController
) : SecurityController {
    /**
     * All the security checks to perform.
     */
    private fun securityChecks() = listOf(
        isPackageSignatureValid(),
        isDebuggerAttached(),
        isAppDebuggable(),
        isAppRunningOnEmulator(),
        isDeviceRooted(),
        areRootCloakingAppsInstalled(),
        isExternalProcessAttached(),
        isApplicationDexCrcValid()
    )

    override fun isPackageSignatureValid(): SecurityCheck =
        when (
            applicationPackageController.getApplicationSignatures()
                .any { it == configSecurityLogic.packageSignature }
        ) {
            true -> SecurityCheck.Passed
            false -> SecurityCheck.Failed(SecurityViolation.PackageSignatureInvalid)
        }

    override fun isAppDebuggable(): SecurityCheck =
        when (applicationPackageController.isAppDebuggable()) {
            true -> SecurityCheck.Passed
            else -> SecurityCheck.Failed(SecurityViolation.DebuggableApp)
        }

    override fun isDebuggerAttached(): SecurityCheck =
        when (Debug.isDebuggerConnected()) {
            false -> SecurityCheck.Passed
            true -> SecurityCheck.Failed(SecurityViolation.DebuggableApp)
        }

    override fun isAppRunningOnEmulator(): SecurityCheck =
        if ( // Android SDK emulator
            (Build.MANUFACTURER == "Google" && Build.BRAND == "google"
                    && (Build.FINGERPRINT.startsWith("google/sdk_gphone")
                    && Build.PRODUCT.startsWith("sdk_gphone")
                    && Build.MODEL.startsWith("sdk_gphone")
                    )
                    )
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.startsWith("google_sdk")
            || Build.MODEL.startsWith("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            // BlueStacks
            || Build.BOARD.equals("QC_Reference_Phone") && !Build.MANUFACTURER.equals(
                "Xiaomi",
                ignoreCase = true
            )
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build")
            // MSI App Player
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.PRODUCT == "google_sdk"
        ) SecurityCheck.Failed(SecurityViolation.RunningOnEmulator) else SecurityCheck.Passed

    override fun isDeviceRooted(): SecurityCheck =
        when (rootController.isRooted()) {
            true -> SecurityCheck.Passed
            else -> SecurityCheck.Failed(SecurityViolation.DeviceRooted)
        }

    override fun areRootCloakingAppsInstalled(): SecurityCheck =
        when (rootController.areRootCloakingAppsInstalled()) {
            true -> SecurityCheck.Passed
            else -> SecurityCheck.Failed(SecurityViolation.RootCloakingAppsInstalled)
        }

    override fun isExternalProcessAttached(): SecurityCheck =
        when (antiHookController.isExternalProcessAttached()) {
            false -> SecurityCheck.Passed
            true -> SecurityCheck.Failed(SecurityViolation.ExternalProcessAttached)
        }

    override fun isApplicationDexCrcValid(): SecurityCheck =
        when (antiReverseEngineeringController.validateApplicationDexCRC()) {
            true -> SecurityCheck.Passed
            else -> SecurityCheck.Failed(SecurityViolation.AppDexCrcInvalid)
        }

    override fun isScreenCapturingBlocked(): Boolean =
        configSecurityLogic.blockScreenCapture
                && configSecurityLogic.appBuildType is AppBuildType.RELEASE

    override fun areOverlayWindowsHidden(): Boolean =
        configSecurityLogic.hideOverlayWindows
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && configSecurityLogic.appBuildType is AppBuildType.RELEASE

    override fun shouldApplicationDrawOverlayWindows(): Boolean =
        configSecurityLogic.applicationDrawOverlayWindows
                && configSecurityLogic.appBuildType is AppBuildType.RELEASE

    override fun shouldApplicationBeInMultiWindowMode(): Boolean =
        configSecurityLogic.applicationMultiWindowMode
                && configSecurityLogic.appBuildType is AppBuildType.RELEASE

    override fun isAutoLogoutEnabled(): Boolean =
        configSecurityLogic.isAutoLogoutEnabled
                && configSecurityLogic.appBuildType is AppBuildType.RELEASE

    override fun isApplicationSecure(): Flow<ApplicationSecurity> = flow {
        if (configSecurityLogic.appBuildType !is AppBuildType.RELEASE) {
            emit(ApplicationSecurity.Secure)
            return@flow
        }

        val failedSecurityCheck = securityChecks().firstOrNull { it is SecurityCheck.Failed }
        emit(
            when (failedSecurityCheck) {
                null -> ApplicationSecurity.Secure
                else -> ApplicationSecurity.Insecure((failedSecurityCheck as SecurityCheck.Failed).securityViolation)
            }
        )
    }.flowOn(Dispatchers.IO)
}