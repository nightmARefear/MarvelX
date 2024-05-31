package com.alexgreece45.marvelx.controllers.model.security

/**
 * Used for setting the overall application security. If all the security checks have been
 * successfully passed then [Secure] must be used, otherwise [Insecure] alongside the first
 * [SecurityViolation] that was detected.
 */
sealed interface ApplicationSecurity {
    /**
     * Defines that this application has passed all the security checks.
     */
    data object Secure : ApplicationSecurity

    /**
     * Defines that this application has failed a security check. Its related security violation is
     * defined by [securityViolation].
     */
    data class Insecure(val securityViolation: SecurityViolation) : ApplicationSecurity
}