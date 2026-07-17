package com.alexgreece45.marvelx.core.security.controllers

import com.alexgreece45.marvelx.core.security.model.security.ApplicationSecurity
import com.alexgreece45.marvelx.core.security.model.security.SecurityCheck
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
import kotlinx.coroutines.flow.Flow

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
    suspend fun isApplicationSecure(): ApplicationSecurity
}