package com.alexgreece45.marvelx.core.common.config

/**
 * Contains all the configuration options for security related operations.
 */
interface SecurityConfigLogic {

    /**
     * Defines whether network traffic should be logged.
     */
    val useNetworkLogger: Boolean

    /**
     * Contains all the header names, in lowercase, that are used in the session signature
     * generation.
     */
    val sessionSignatureHeaders: List<String>

    /**
     * Defines whether screen capturing is blocked.
     */
    val blockScreenCapture: Boolean get() = false

    /**
     * Defines whether overlay windows are hidden.
     */
    val hideOverlayWindows: Boolean get() = false

    /**
     * Defines whether strict mode is enabled.
     */
    val enableStrictMode: Boolean

    /**
     * Defines whether this application should have the ability to draw overlay windows over other
     * applications.
     */
    val applicationDrawOverlayWindows: Boolean get() = false

    /**
     * Defines whether this application should have the ability to be in multi window mode.
     */
    val applicationMultiWindowMode: Boolean get() = false

    /**
     * Defines whether auto logout is enabled.
     */
    val isAutoLogoutEnabled: Boolean

    /**
     * The package signature of this application.
     */
    val packageSignature: String
}