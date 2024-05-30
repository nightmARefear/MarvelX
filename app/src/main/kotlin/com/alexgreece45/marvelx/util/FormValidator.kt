package com.alexgreece45.marvelx.util

import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.util.extensions.isEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FormValidator {
    fun validateForm(form: Form): Flow<FormValidationResult>
    fun validateForms(forms: List<Form>): Flow<FormsValidationResult>
}

class FormValidatorImpl : FormValidator {
    override fun validateForm(form: Form): Flow<FormValidationResult> = flow {
        form.inputs.forEach { (rules, value) ->
            rules.forEach { rule ->
                validateRule(rule, value)?.let {
                    emit(it)
                    return@flow
                }
            }
        }
        emit(FormValidationResult(isValid = true))
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
}