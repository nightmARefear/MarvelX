package com.alexgreece45.marvelx.core.security.controllers

import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.security.model.security.ApplicationSecurity
import com.alexgreece45.marvelx.core.security.model.security.SecurityCheck
import org.koin.core.annotation.Single

@Single
internal class SecurityControllerImpl(
    private val securityConfigLogic: SecurityConfigLogic
) : SecurityController {

    override fun isPackageSignatureValid(): SecurityCheck = SecurityCheck.Passed

    override fun isAppDebuggable(): SecurityCheck = SecurityCheck.Passed

    override fun isDebuggerAttached(): SecurityCheck = SecurityCheck.Passed

    override fun isAppRunningOnEmulator(): SecurityCheck = SecurityCheck.Passed

    override fun isDeviceRooted(): SecurityCheck = SecurityCheck.Passed

    override fun areRootCloakingAppsInstalled(): SecurityCheck = SecurityCheck.Passed

    override fun isExternalProcessAttached(): SecurityCheck = SecurityCheck.Passed

    override fun isApplicationDexCrcValid(): SecurityCheck = SecurityCheck.Passed

    override fun isScreenCapturingBlocked(): Boolean = false

    override fun areOverlayWindowsHidden(): Boolean = false

    override fun shouldApplicationDrawOverlayWindows(): Boolean = true

    override fun shouldApplicationBeInMultiWindowMode(): Boolean = true

    override fun isAutoLogoutEnabled(): Boolean = securityConfigLogic.isAutoLogoutEnabled

    override suspend fun isApplicationSecure(): ApplicationSecurity = ApplicationSecurity.Secure
}