package com.alexgreece45.marvelx.controllers

import android.os.Build
import android.os.Debug
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
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
    private val configLogic: ConfigLogic,
    private val rootController: RootController,
    private val applicationPackageController: ApplicationPackageController
) : SecurityController {

    override fun isPackageSignatureValid(): Boolean {
        if (configLogic.appBuildType != AppBuildType.RELEASE) return true
        return applicationPackageController.getSignature() == configLogic.packageSignature
    }

    override fun isAppDebuggable(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
                && applicationPackageController.isAppDebuggable()

    override fun isDebuggerAttached(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
                && Debug.isDebuggerConnected()

    override fun isAppRunningOnEmulator(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
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
        configLogic.appBuildType is AppBuildType.RELEASE
                && rootController.isRooted()

    override fun areRootCloakingAppsInstalled(): Boolean =
        configLogic.appBuildType is AppBuildType.RELEASE
                && rootController.areRootCloakingAppsInstalled()

    override fun isScreenCapturingBlocked(): Boolean =
        configLogic.blockScreenCapture
                && configLogic.appBuildType is AppBuildType.RELEASE

    override fun areOverlayWindowsHidden(): Boolean =
        configLogic.hideOverlayWindows
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && configLogic.appBuildType is AppBuildType.RELEASE

    override fun shouldApplicationDrawOverlayWindows(): Boolean = false
    override fun isAutoLogoutEnabled(): Boolean =
        configLogic.isAutoLogoutEnabled
                && configLogic.appBuildType is AppBuildType.RELEASE


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