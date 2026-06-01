package com.alexgreece45.marvelx.ui.model_generic

import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.util.Rule

sealed interface InputFieldUi {

    val isValid: Boolean

    sealed class InputField(
        val value: String = "",
        val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        val validationRules: List<Rule> = emptyList()
    ) : InputFieldUi

    data class Text(
        val textValue: String = "",
        val textValidationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        val textValidationRules: List<Rule> = emptyList()
    ) : InputField(
        value = textValue,
        validationResult = textValidationResult,
        validationRules = textValidationRules
    ) {
        override val isValid: Boolean
            get() = textValidationResult.isValid
    }

    data class Integer(
        val integerValue: String = "",
        val integerValidationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        val integerValidationRules: List<Rule> = emptyList()
    ) : InputField(
        value = integerValue,
        validationResult = integerValidationResult,
        validationRules = integerValidationRules
    ) {

        val parsedValue: Int? get() = integerValue.toIntOrNull()

        override val isValid: Boolean
            get() = parsedValue != null && !integerValidationResult.isValid
    }

    data class Enum(
        val value: EnumGeneric? = null,
        val isExpanded: Boolean = false
    ) : InputFieldUi {

        override val isValid: Boolean
            get() = value != null
    }
}
