package com.alexgreece45.marvelx.ui.helper

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

/**
 * Contains the validation rules of the input fields used throughout the app. This `interface`
 * provides a consistent way of validating fields.
 */
interface FieldValidationRules {
    fun getFirstNameValidationRules(): List<Rule>
    fun getLastNameValidationRules(): List<Rule>
    fun getGenderValidationRules(): List<Rule>
    fun getEmailValidationRules(): List<Rule>
    fun getPasswordValidationRules(): List<Rule>
}

class FieldValidationRulesImpl(
    private val resourceProvider: ResourceProvider
) : FieldValidationRules {
    override fun getFirstNameValidationRules(): List<Rule> {
        val minLength = 2
        val maxLength = 50
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_first_name)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            )
        )
    }

    override fun getLastNameValidationRules(): List<Rule> {
        val minLength = 2
        val maxLength = 50
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_last_name)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            )
        )
    }

    override fun getGenderValidationRules(): List<Rule> {
        return listOf(
            Rule.ValidateGender(
                errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_gender)
            )
        )
    }

    override fun getEmailValidationRules(): List<Rule> {
        val minLength = 5
        val maxLength = 100
        return listOf(
            Rule.ValidateNotBlank(
                errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_email)
            ),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            ),
            Rule.ValidateEmail(
                errorMessage = resourceProvider.getString(R.string.validation_rule_error_validate_email)
            )
        )
    }

    override fun getPasswordValidationRules(): List<Rule> {
        val minLength = 10
        val maxLength = 30
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_password)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            )
        )
    }
}
