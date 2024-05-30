package com.alexgreece45.marvelx.controllers

import android.os.Build
import android.os.Debug
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface SecurityController {
    /**
     * Checks whether the package signature is valid, and returns `true` if it, otherwise `false`.
     */
    fun isPackageSignatureValid(): Boolean

    /**
     * Checks whether the application allows debugging of its code , even when installed on a
     * non-development system. Comes from `android:debuggable` of the `<application>` tag in
     * `AndroidManifest`.
     *
     * @return Returns `true` if the application allows debugging of its code, otherwise `false`.
     */
    fun isAppDebuggable(): Boolean

    /**
     * Checks whether a debugger has been attached to the application, and returns `true` if it has,
     * otherwise `false`.
     */
    fun isDebuggerAttached(): Boolean

    /**
     * Checks whether the app is running on an emulator, and returns `true` if it is otherwise
     * `false`.
     *
     * ### Note: This condition doesn't guarantee that it can always be detected if the app is running on an emulator.
     */
    fun isAppRunningOnEmulator(): Boolean

    /**
     * Checks whether the device is rooted, and returns `true` if it is otherwise `false`.
     *
     * ### Note: This condition doesn't guarantee that a rooted device can always be detected.
     */
    fun isDeviceRooted(): Boolean

    /**
     * Checks whether the device has root cloaking apps installed, and returns `true` if it has
     * otherwise `false`.
     *
     * ### Note: This condition doesn't guarantee that installed root cloaking apps can always be detected.
     */
    fun areRootCloakingAppsInstalled(): Boolean

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
     * with.
     *
     * ### Note: This condition doesn't guarantee that the device is secure, or that the application hasn't been tampered with.
     */
    fun isApplicationSecure(): Flow<Boolean>
}

class SecurityControllerImpl(
    private val configSecurityLogic: ConfigSecurityLogic,
    private val rootController: RootController,
    private val applicationPackageController: ApplicationPackageController
) : SecurityController {

    override fun isPackageSignatureValid(): Boolean {
        if (configSecurityLogic.appBuildType !is AppBuildType.RELEASE) return true
        return applicationPackageController.getApplicationSignatures()
            .any { it == configSecurityLogic.packageSignature }
    }

    override fun isAppDebuggable(): Boolean =
        configSecurityLogic.appBuildType is AppBuildType.RELEASE
                && applicationPackageController.isAppDebuggable()

    override fun isDebuggerAttached(): Boolean =
        configSecurityLogic.appBuildType is AppBuildType.RELEASE
                && Debug.isDebuggerConnected()

    override fun isAppRunningOnEmulator(): Boolean =
        configSecurityLogic.appBuildType is AppBuildType.RELEASE
                && (
                // Android SDK emulator
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
                        // Bluestacks
                        || Build.BOARD.equals("QC_Reference_Phone") && !Build.MANUFACTURER.equals(
                    "Xiaomi",
                    ignoreCase = true
                )
                        || Build.MANUFACTURER.contains("Genymotion")
                        || Build.HOST.startsWith("Build")
                        // MSI App Player
                        || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                        || Build.PRODUCT == "google_sdk"
                )

    override fun isDeviceRooted(): Boolean =
        configSecurityLogic.appBuildType is AppBuildType.RELEASE
                && rootController.isRooted()

    override fun areRootCloakingAppsInstalled(): Boolean =
        configSecurityLogic.appBuildType is AppBuildType.RELEASE
                && rootController.areRootCloakingAppsInstalled()

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

    override fun isApplicationSecure(): Flow<Boolean> = flow {
        emit(
            isPackageSignatureValid()
                    && !isDebuggerAttached()
                    && !isAppDebuggable()
                    && !isAppRunningOnEmulator()
                    && !isDeviceRooted()
                    && !areRootCloakingAppsInstalled()
        )
    }.flowOn(Dispatchers.IO)
}