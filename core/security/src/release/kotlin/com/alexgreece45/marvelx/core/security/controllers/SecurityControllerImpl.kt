package com.alexgreece45.marvelx.core.security.controllers

import android.os.Build
import android.os.Debug
import androidx.annotation.ChecksSdkIntAtLeast
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.util.withContextCatchable
import com.alexgreece45.marvelx.core.security.model.security.ApplicationSecurity
import com.alexgreece45.marvelx.core.security.model.security.SecurityCheck
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class SecurityControllerImpl(
    private val securityConfigLogic: SecurityConfigLogic,
    private val rootController: RootController,
    private val applicationPackageController: ApplicationPackageController,
    private val antiReverseEngineeringController: AntiReverseEngineeringController,
    private val antiHookController: AntiHookController,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : SecurityController {

    private fun fastSecurityChecks(): List<() -> SecurityCheck> = listOf(
        ::isDebuggerAttached,
        ::isAppDebuggable,
        ::isAppRunningOnEmulator,
        ::isDeviceRooted,
        ::areRootCloakingAppsInstalled,
        ::isExternalProcessAttached
    )

    private fun heavySecurityChecks(): List<() -> SecurityCheck> = listOf(
        ::isApplicationDexCrcValid,
        ::isPackageSignatureValid,
    )

    override fun isPackageSignatureValid(): SecurityCheck =
        when (
            applicationPackageController.getApplicationSignatures()
                .any { it == securityConfigLogic.packageSignature }
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
        when (antiReverseEngineeringController.isApplicationDexCrcValid()) {
            true -> SecurityCheck.Passed
            else -> SecurityCheck.Failed(SecurityViolation.AppDexCrcInvalid)
        }

    override fun isScreenCapturingBlocked(): Boolean =
        securityConfigLogic.blockScreenCapture

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    override fun areOverlayWindowsHidden(): Boolean =
        securityConfigLogic.hideOverlayWindows
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    override fun shouldApplicationDrawOverlayWindows(): Boolean =
        securityConfigLogic.applicationDrawOverlayWindows


    override fun shouldApplicationBeInMultiWindowMode(): Boolean =
        securityConfigLogic.applicationMultiWindowMode

    override fun isAutoLogoutEnabled(): Boolean = securityConfigLogic.isAutoLogoutEnabled


    override suspend fun isApplicationSecure(): ApplicationSecurity {
        return withContextCatchable(
            context = ioDispatcher,
            onError = { ApplicationSecurity.Secure }
        ) {
            fastSecurityChecks().forEach { securityCheck ->
                val result = securityCheck.invoke()
                if (result is SecurityCheck.Failed) {
                    return@withContextCatchable ApplicationSecurity.Insecure(result.securityViolation)
                }
            }

            val securityCheck = channelFlow {
                heavySecurityChecks().forEach { securityCheck ->
                    launch { send(securityCheck.invoke()) }
                }
            }.firstOrNull {
                it is SecurityCheck.Failed
            }

            if (securityCheck != null) {
                ApplicationSecurity.Insecure((securityCheck as SecurityCheck.Failed).securityViolation)
            } else {
                ApplicationSecurity.Secure
            }
        }
    }
}