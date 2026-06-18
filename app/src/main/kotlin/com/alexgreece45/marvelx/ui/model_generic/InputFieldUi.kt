package com.alexgreece45.marvelx.ui.model_generic

import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric

sealed interface InputFieldUi {

    val isValid: Boolean
    val isMandatory: Boolean

    sealed class InputField(
        open val value: String = "",
        open val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        open val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : InputFieldUi {

        override val isValid: Boolean
            get() = if (isMandatory) {
                validationResult is InputFieldValidationResult.Success
            } else {
                validationResult.isNotError
            }
    }

    data class Text(
        override val value: String = "",
        override val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        override val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : InputField(value, validationResult, validationRules, isMandatory)

    data class Integer(
        val integerValue: String = "",
        val integerValidationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        val integerValidationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : InputField(
        value = integerValue,
        validationResult = integerValidationResult,
        validationRules = integerValidationRules,
        isMandatory = isMandatory
    ) {

        val parsedValue: Int? get() = integerValue.toIntOrNull()

        override val isValid: Boolean
            get() {
                val isIntegerFormatValid = if (isMandatory) {
                    parsedValue != null
                } else {
                    value.isEmpty() || parsedValue != null
                }

                return isIntegerFormatValid && super.isValid
            }
    }

    data class Enum(
        val value: EnumGeneric? = null,
        val isExpanded: Boolean = false,
        override val isMandatory: Boolean
    ) : InputFieldUi {

        override val isValid: Boolean
            get() = if (isMandatory) value != null else true
    }
}