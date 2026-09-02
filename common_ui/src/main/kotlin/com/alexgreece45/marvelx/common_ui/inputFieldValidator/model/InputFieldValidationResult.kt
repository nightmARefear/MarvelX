package com.alexgreece45.marvelx.common_ui.inputFieldValidator.model


sealed interface InputFieldValidationResult {
    data object Uninitialized : InputFieldValidationResult
    data object Success : InputFieldValidationResult
    data class Error(val errorMessage: String) : InputFieldValidationResult

    val isError: Boolean get() = this is Error

    companion object {
        val EmptyError = Error("")
    }
}