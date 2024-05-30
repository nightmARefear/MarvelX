package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.util.constants.ConstantsSecurity

/**
 * Contains all the configuration options for security related operations.
 */
interface ConfigSecurityLogic {
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
     * The app's build type (i.e., debug, mock, or release).
     */
    val appBuildType: AppBuildType

    /**
     * The package signature of this application.
     */
    val packageSignature: String
}

class ConfigSecurityLogicImpl : ConfigSecurityLogic {
    override val sessionSignatureHeaders: List<String>
        get() = listOf(ConstantsSecurity.HeaderName.UserAgent).map { it.name.lowercase() }
    override val enableStrictMode: Boolean
        get() = appBuildType is AppBuildType.RELEASE

    override val isAutoLogoutEnabled: Boolean
        get() = appBuildType is AppBuildType.RELEASE

    override val appBuildType: AppBuildType
        get() = AppBuildType.getType()

    override val packageSignature: String
        get() = "tPwQygNBC+Pl1e3T5qwNGCg/oy9wOM9p+c4oddwuGFE="
}