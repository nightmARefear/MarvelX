package com.alexgreece45.marvelx.data.domains.otp

import com.alexgreece45.marvelx.data.models.otp.OtpErrorCause

data class OtpResponseErrorDomain(
    val errorCode: Int,
    val errorMessage: String?,
    val errorCause: OtpErrorCause
) {
    /**
     * This function checks if [errorMessage] is null and
     * if [errorCause] is not [OtpErrorCause.Invalid], and if they are,
     * true is returned, otherwise false.
     *
     * If false was returned, this indicates that the [OtpRequest] that was
     * sent was invalid either because one or more fields were missing, or
     * due to invalid input. Note that ***invalid input*** refers to sending
     * the data in the wrong format, and not due to user-input error.
     *
     * If true was returned, this indicates that the [OtpRequest] that was
     * sent was valid and that [OtpErrorCause.cause] should be shown in the screen.
     *
     * @return returns true if [errorMessage] is null and if [errorCause]
     * is not [OtpErrorCause.Invalid], otherwise false
     * */
    fun shouldDisplayErrorMessage(): Boolean =
        errorMessage == null && errorCause != OtpErrorCause.Invalid(errorCause.key)
}
