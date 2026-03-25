package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.Rule

sealed interface WizardInputFieldUi {

    sealed class InputField(
        val value: String = "",
        val validationResult: FormValidationResult = FormValidationResult(isValid = true),
        val validationRules: List<Rule> = emptyList()
    ) : WizardInputFieldUi

    data class Text(
        val textValue: String = "",
        val textValidationResult: FormValidationResult = FormValidationResult(isValid = true),
        val textValidationRules: List<Rule> = emptyList()
    ) : InputField(
        value = textValue,
        validationResult = textValidationResult,
        validationRules = textValidationRules
    ) {

        val isValid: Boolean
            get() = textValue.isNotBlank() && textValidationResult.isValid
    }

    data class Integer(
        val integerValue: String = "",
        val integerValidationResult: FormValidationResult = FormValidationResult(isValid = true),
        val integerValidationRules: List<Rule> = emptyList()
    ) : InputField(
        value = integerValue,
        validationResult = integerValidationResult,
        validationRules = integerValidationRules
    ) {

        val parsedValue: Int? get() = integerValue.toIntOrNull()

        val isValid: Boolean
            get() = parsedValue != null && integerValidationResult.isValid
    }

    data class Enum(
        val value: EnumGeneric? = null,
        val isExpanded: Boolean = false
    ) : WizardInputFieldUi {

        val isValid: Boolean
            get() = value != null
    }
}
