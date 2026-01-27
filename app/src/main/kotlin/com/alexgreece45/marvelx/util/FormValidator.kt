package com.alexgreece45.marvelx.util

import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.util.extensions.isEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal

interface FormValidator {

    fun validateForm(form: Form): FormValidationResult
    fun validateForms(forms: List<Form>): Flow<FormsValidationResult>
}

class FormValidatorImpl : FormValidator {

    override fun validateForm(form: Form): FormValidationResult {
        form.inputs.forEach { (rules, value) ->
            rules.forEach { rule ->
                validateRule(rule, value)?.let {
                    return it
                }
            }
        }
        return (FormValidationResult(isValid = true))
    }

    override fun validateForms(forms: List<Form>): Flow<FormsValidationResult> = flow {
        val errors = mutableListOf<String>()
        var isValid = true
        forms.forEach { form ->
            form.inputs.forEach { (rules, value) ->
                rules.forEach { rule ->
                    validateRule(rule, value)?.let {
                        isValid = false
                        errors.add(it.message)
                    }
                }
            }
        }
        emit(FormsValidationResult(isValid, errors))
    }

    private fun validateRule(rule: Rule, value: String): FormValidationResult? {
        return when (rule) {
            is Rule.ValidateEmail -> checkValidationResult(isEmailValid(value), rule.errorMessage)

            is Rule.ValidateStringMaxLength -> checkValidationResult(
                isStringMaxLengthValid(
                    value,
                    rule.length
                ), rule.errorMessage
            )

            is Rule.ValidateStringMinLength -> checkValidationResult(
                isStringMinLengthValid(value, rule.length),
                rule.errorMessage
            )

            is Rule.ValidateNotEmpty -> checkValidationResult(value.isNotEmpty(), rule.errorMessage)

            is Rule.ValidateNotBlank -> checkValidationResult(value.isNotBlank(), rule.errorMessage)

            is Rule.ValidateRegex -> checkValidationResult(
                isRegexMatching(value, rule.regex),
                rule.errorMessage
            )

            is Rule.ValidateStringLength -> checkValidationResult(
                isLength(value, rule.length),
                rule.errorMessage
            )

            is Rule.ValidateStringMatch -> checkValidationResult(
                isStringMatching(
                    value,
                    rule.stringToMatch,
                    rule.isCaseSensitive
                ), rule.errorMessage
            )

            is Rule.ValidateStringNotMatch -> checkValidationResult(
                isStringNotMatching(
                    value,
                    rule.stringToMatch,
                    rule.isCaseSensitive
                ), rule.errorMessage
            )

            is Rule.ValidateGender -> checkValidationResult(
                isGenderValid(
                    value
                ),
                rule.errorMessage
            )

            is Rule.ValidateIntegerValue -> checkValidationResult(
                isValid = isIntegerValid(value),
                errorMessage = rule.errorMessage
            )

            is Rule.ValidateOverMinIntegerValue -> checkValidationResult(
                isValid = isIntegerOverMinValue(
                    value = value,
                    minValue = rule.minValue
                ),
                errorMessage = rule.errorMessage
            )

            is Rule.ValidateUnderMaxIntegerValue -> checkValidationResult(
                isValid = isIntegerBelowMaxValue(
                    value = value,
                    maxValue = rule.maxValue
                ),
                errorMessage = rule.errorMessage
            )

            is Rule.ValidateBigDecimal -> checkValidationResult(
                isValid = isBigDecimalValid(value),
                errorMessage = rule.errorMessage
            )

            is Rule.ValidateOverMinimumBigDecimalValue -> checkValidationResult(
                isValid = isBigDecimalOverMinValue(
                    value = value,
                    scale = rule.scale,
                    minValue = rule.minValue
                ),
                errorMessage = rule.errorMessage
            )

            is Rule.ValidateUnderMaximumBigDecimalValue -> checkValidationResult(
                isValid = isBigDecimalBelowMaxValue(
                    value,
                    scale = rule.scale,
                    maxValue = rule.maxValue
                ),
                errorMessage = rule.errorMessage
            )
        }
    }

    private fun checkValidationResult(
        isValid: Boolean,
        errorMessage: String
    ): FormValidationResult? {
        return if (!isValid) {
            FormValidationResult(
                isValid,
                errorMessage
            )
        } else null
    }

    private fun isEmailValid(value: String): Boolean =
        value.isNotEmpty() && value.isEmail()

    private fun isStringMaxLengthValid(value: String, length: Int): Boolean =
        value.length <= length

    private fun isStringMinLengthValid(value: String, length: Int): Boolean =
        value.length >= length

    private fun isRegexMatching(value: String, regex: Regex): Boolean =
        value.matches(regex)

    private fun isLength(value: String, length: Int): Boolean = value.length == length

    private fun isStringMatching(
        value: String,
        stringToMatch: String,
        isCaseSensitive: Boolean = false
    ) = value.equals(
        stringToMatch,
        !isCaseSensitive
    )

    private fun isStringNotMatching(
        value: String,
        stringToMatch: String,
        isCaseSensitive: Boolean = false
    ) = !value.equals(
        stringToMatch,
        !isCaseSensitive
    )

    private fun isGenderValid(value: String): Boolean = Gender.isStringValidGender(value = value)

    private fun isIntegerValid(value: String): Boolean = value.toIntOrNull() != null

    private fun isIntegerOverMinValue(value: String, minValue: Int): Boolean =
        value.toIntOrNull()?.let { it >= minValue } ?: false

    private fun isIntegerBelowMaxValue(value: String, maxValue: Int): Boolean =
        value.toIntOrNull()?.let { it <= maxValue } ?: false

    private fun isBigDecimalValid(value: String): Boolean = value.toBigDecimalOrNull() != null

    private fun isBigDecimalOverMinValue(
        value: String,
        scale: Int,
        minValue: BigDecimal
    ): Boolean =
        value.toBigDecimalOrNull()?.takeIf { it.scale() <= scale }?.let { it >= minValue } ?: false

    private fun isBigDecimalBelowMaxValue(
        value: String,
        scale: Int,
        maxValue: BigDecimal
    ): Boolean =
        value.toBigDecimalOrNull()?.takeIf { it.scale() <= scale }?.let { it <= maxValue } ?: false
}

data class Form(val inputs: Map<List<Rule>, String>)
data class FormValidationResult(val isValid: Boolean, val message: String = "")
data class FormsValidationResult(val isValid: Boolean, val messages: List<String> = listOf())

sealed class Rule(val errorMsg: String) {
    data class ValidateNotEmpty(val errorMessage: String) : Rule(errorMessage)
    data class ValidateNotBlank(val errorMessage: String) : Rule(errorMessage)
    data class ValidateEmail(val errorMessage: String) : Rule(errorMessage)
    data class ValidateStringMaxLength(
        val length: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateStringMinLength(
        val length: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateRegex(val regex: Regex, val errorMessage: String) : Rule(errorMessage)

    data class ValidateStringLength(
        val length: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateStringMatch(
        val stringToMatch: String,
        val errorMessage: String,
        val isCaseSensitive: Boolean = false
    ) : Rule(errorMessage)

    data class ValidateStringNotMatch(
        val stringToMatch: String,
        val errorMessage: String,
        val isCaseSensitive: Boolean = false
    ) : Rule(errorMessage)

    data class ValidateGender(val errorMessage: String) : Rule(errorMessage)

    data class ValidateIntegerValue(
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateOverMinIntegerValue(
        val minValue: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateUnderMaxIntegerValue(
        val maxValue: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateBigDecimal(
        val scale: Int,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateOverMinimumBigDecimalValue(
        val scale: Int,
        val minValue: BigDecimal,
        val errorMessage: String
    ) : Rule(errorMessage)

    data class ValidateUnderMaximumBigDecimalValue(
        val scale: Int,
        val maxValue: BigDecimal,
        val errorMessage: String
    ) : Rule(errorMessage)
}
