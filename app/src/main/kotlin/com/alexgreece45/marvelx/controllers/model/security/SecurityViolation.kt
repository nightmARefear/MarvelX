package com.alexgreece45.marvelx.controllers.model.security

/**
 * A single security violation that defines which security check was failed.
 *
 * @param errorCode The error code of this security violation. Used for identifying which violation
 * was first detected, and for bug reporting in case of falsely triggering a violation.
 */
sealed class SecurityViolation(val errorCode: Int) {
    data object PackageSignatureInvalid : SecurityViolation(errorCode = 1000)
    data object DebuggableApp : SecurityViolation(errorCode = 1001)
    data object DebuggerAttached : SecurityViolation(errorCode = 1002)
    data object RunningOnEmulator : SecurityViolation(errorCode = 1003)
    data object DeviceRooted : SecurityViolation(errorCode = 1004)
    data object RootCloakingAppsInstalled : SecurityViolation(errorCode = 1005)
    data object ExternalProcessAttached: SecurityViolation(errorCode = 1006)
    data object AppDexCrcInvalid : SecurityViolation(errorCode = 1007)
    data object ApplicationDrawOverlayWindows : SecurityViolation(errorCode = 1008)
    data object ApplicationInMultiWindowMode : SecurityViolation(errorCode = 1009)
}