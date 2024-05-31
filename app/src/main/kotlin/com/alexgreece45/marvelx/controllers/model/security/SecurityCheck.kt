package com.alexgreece45.marvelx.controllers.model.security

/**
 * Defines a single security check. If it has been passed then [Passed] must be used, otherwise
 * [Failed] along with the related [SecurityViolation].
 */
sealed interface SecurityCheck {
    /**
     * This security check has been passed.
     */
    data object Passed: SecurityCheck

    /**
     * This security has failed. Its related security violation is [securityViolation].
     */
    data class Failed(val securityViolation: SecurityViolation): SecurityCheck
}