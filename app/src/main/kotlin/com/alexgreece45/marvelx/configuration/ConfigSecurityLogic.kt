package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.util.constants.ConstantsSecurity

/**
 * Contains all the configuration options for security related operations.
 */
interface ConfigSecurityLogic {
    /**
     * Defines whether network traffic should be logged.
     */
    val useNetworkLogger: Boolean

    /**
     * Contains a [List] of all the excluded request paths that don't require authorization of any
     * kind -- i.e., passing in the header the access / refresh token and generating a session
     * signature.
     *
     * The requests included in this [List], won't have any authorization headers.
     */
    val excludedRequestPathsFromAuthorization: List<String>

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
    override val useNetworkLogger: Boolean
        get() = appBuildType !is AppBuildType.RELEASE
    override val excludedRequestPathsFromAuthorization: List<String>
        get() = listOf(
            "/api/private/marvelx/user/signup",
            "/api/private/marvelx/user/login",
            "/api/private/marvelx/user/post_login",
            "/api/private/marvelx/user/logout",
            "/api/private/marvelx/user/authorize",
            "/api/private/marvelx/user/token",
            "/api/private/marvelx/user/validate_otp",
            "/api/private/marvelx/user/resend_otp",
            "/api/private/marvelx/user/forgot_password/send_otp",
            "/api/private/marvelx/user/forgot_password/validate_otp",
            "/api/private/marvelx/user/jwt_sensitive_data",
            "/api/private/marvelx/user/sensitive_data"
        )

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