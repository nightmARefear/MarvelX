package com.alexgreece45.marvelx.util.constants

/**
 * All security related constants.
 */
object ConstantsSecurity {
    /**
     * OTP - related constants.
     */
    object OTP {
        const val LENGTH = 6
    }

    /**
     * Auto-logout functionality - related constants.
     */
    object AutoLogout {
        /**
         * The timeout period in minutes of the auto-logout functionality.
         */
        const val TIMEOUT_PERIOD = 10
    }

    /**
     * Header names.
     */
    sealed class HeaderNames(val name: String) {
        data object UserAgent : HeaderNames(name = "User-Agent")
        data object Authorization : HeaderNames(name = "Authorization")
        data object Signature : HeaderNames(name = "Signature")
    }

    /**
     * Header values.
     */
    object HeaderValues {
        const val APP_USER_AGENT = "mobile"
    }

    /**
     * OAuth - related constants.
     */
    object OAuth {
        const val TOKEN_CLIENT_ID = "mobile"
        private const val TOKEN_REDIRECT_SCHEME = "marvelxbackend"
        private const val TOKEN_REDIRECT_HOST = "oauth"
        const val TOKEN_REDIRECT_URI = "$TOKEN_REDIRECT_SCHEME//$TOKEN_REDIRECT_HOST"
    }
}