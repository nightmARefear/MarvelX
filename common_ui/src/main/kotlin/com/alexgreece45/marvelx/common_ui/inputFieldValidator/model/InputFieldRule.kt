package com.alexgreece45.marvelx.common_ui.inputFieldValidator.model

import java.math.BigDecimal
import java.time.YearMonth

sealed class InputFieldRule(val errorMsg: String) {

    data class ValidateNotEmpty(val errorMessage: String) : InputFieldRule(errorMessage)
    data class ValidateNotBlank(val errorMessage: String) : InputFieldRule(errorMessage)
    data class ValidateEmail(val errorMessage: String) : InputFieldRule(errorMessage)
    data class ValidateStringMaxLength(
        val length: Int,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateStringMinLength(
        val length: Int,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateRegex(val regex: Regex, val errorMessage: String) :
        InputFieldRule(errorMessage)

    data class ValidateStringLength(
        val length: Int,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateStringMatch(
        val stringToMatch: String,
        val errorMessage: String,
        val isCaseSensitive: Boolean = false
    ) : InputFieldRule(errorMessage)

    data class ValidateStringNotMatch(
        val stringToMatch: String,
        val errorMessage: String,
        val isCaseSensitive: Boolean = false
    ) : InputFieldRule(errorMessage)

    data class ValidateIntegerValue(
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateOverMinIntegerValue(
        val minValue: Int,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateUnderMaxIntegerValue(
        val maxValue: Int,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateBigDecimal(
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateOverMinimumBigDecimalValue(
        val minValue: BigDecimal,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateUnderMaximumBigDecimalValue(
        val maxValue: BigDecimal,
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateCardNumberValue(
        val errorMessage: String
    ) : InputFieldRule(errorMessage)

    data class ValidateCardNumberExpirationDate(
        val errorMessage: String,
        val currentYearMonth: YearMonth
    ) : InputFieldRule(errorMessage)
}