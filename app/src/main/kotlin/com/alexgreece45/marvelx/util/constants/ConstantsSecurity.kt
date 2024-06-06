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
    sealed class HeaderName(val name: String) {
        data object UserAgent : HeaderName(name = "User-Agent")
        data object Authorization : HeaderName(name = "Authorization")
        data object XAuthNonce : HeaderName(name = "X-Auth-Nonce")
        data object XAuthTimestamp : HeaderName(name = "X-Auth-Timestamp")
        data object XAuthSignature : HeaderName(name = "X-Auth-Signature")
    }

    /**
     * Header values.
     */
    object HeaderValue {
        const val APP_USER_AGENT = "mobile"
    }
}