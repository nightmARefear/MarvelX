package com.alexgreece45.marvelx.ui.model_generic

import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import java.math.BigDecimal
import java.math.RoundingMode

sealed interface InputFieldUi {

    val isValid: Boolean
    val isMandatory: Boolean

    sealed class TextField(
        open val value: String = "",
        open val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        open val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : InputFieldUi {

        override val isValid: Boolean
            get() = if (isMandatory) {
                validationResult is InputFieldValidationResult.Success
            } else {
                validationResult is InputFieldValidationResult.Uninitialized || validationResult is InputFieldValidationResult.Success
            }
    }

    data class Text(
        override val value: String = "",
        override val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        override val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : TextField(value, validationResult, validationRules, isMandatory)

    data class Integer(
        override val value: String = "",
        override val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        override val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean
    ) : TextField(
        value = value,
        validationResult = validationResult,
        validationRules = validationRules,
        isMandatory = isMandatory
    ) {

        val parsedValue: Int? get() = value.toIntOrNull()

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

    data class Price(
        override val value: String = "",
        override val validationResult: InputFieldValidationResult = InputFieldValidationResult.Uninitialized,
        override val validationRules: List<InputFieldRule> = emptyList(),
        override val isMandatory: Boolean,
        val priceScale: Int = 2,
        val roundingMode: RoundingMode = RoundingMode.HALF_UP
    ) : TextField(
        value = value,
        validationResult = validationResult,
        validationRules = validationRules,
        isMandatory = isMandatory
    ) {
        val parsedValue: BigDecimal?
            get() = value.toBigDecimalOrNull()?.setScale(
                priceScale,
                roundingMode
            )

        val formattedValue: String
            get() = if (parsedValue != null) (parsedValue as BigDecimal).formatPrice(
                scale = priceScale,
                roundingMode = roundingMode,
                withEuroSymbol = false
            ) else super.value

        override val isValid: Boolean
            get() {
                val isBigDecimalFormatValid = if (isMandatory) {
                    parsedValue != null
                } else {
                    value.isEmpty() || parsedValue != null
                }

                return isBigDecimalFormatValid && super.isValid
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