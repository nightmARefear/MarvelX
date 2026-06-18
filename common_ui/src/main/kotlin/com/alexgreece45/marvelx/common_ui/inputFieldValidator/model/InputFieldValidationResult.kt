package com.alexgreece45.marvelx.common_ui.inputFieldValidator.model


sealed interface InputFieldValidationResult {
    data object Uninitialized : InputFieldValidationResult
    data object Success : InputFieldValidationResult
    data class Error(val errorMessage: String) : InputFieldValidationResult

    /**
     * Calculates whether this result type is not erroneous.
     */
    val isNotError: Boolean get() = this is Uninitialized || this is Success

    companion object {
        val EmptyError = Error("")
    }
}
