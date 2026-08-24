package com.alexgreece45.marvelx.common_ui.inputFieldValidator

import com.alexgreece45.marvelx.common_ui.extensions.isEmail
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.core.common.dispatchers.model.DefaultDispatcher
import com.alexgreece45.marvelx.core.common.util.withContextCatchable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.koin.core.annotation.Factory
import java.math.BigDecimal
import java.time.YearMonth
import kotlin.coroutines.CoroutineContext

interface InputFieldValidator {
    fun validateInputSerially(
        value: String,
        rules: List<InputFieldRule>
    ): InputFieldValidationResult

    suspend fun validateInputInParallel(
        value: String,
        rules: List<InputFieldRule>
    ): InputFieldValidationResult
}

@Factory
internal class InputFieldValidatorImpl(
    @DefaultDispatcher private val defaultDispatcher: CoroutineContext
) : InputFieldValidator {

    override fun validateInputSerially(
        value: String,
        rules: List<InputFieldRule>
    ): InputFieldValidationResult {
        return try {
            for (rule in rules) {
                val result = validateRule(value = value, inputFieldRule = rule)
                if (result is InputFieldValidationResult.Error) {
                    return result
                }
            }
            InputFieldValidationResult.Success
        } catch (_: Exception) {
            InputFieldValidationResult.EmptyError
        }
    }

    override suspend fun validateInputInParallel(
        value: String,
        rules: List<InputFieldRule>
    ): InputFieldValidationResult =
        withContextCatchable(
            context = defaultDispatcher,
            onError = { InputFieldValidationResult.EmptyError }
        ) {
            val deferredResults = rules.map { inputFieldRule ->
                async { validateRule(inputFieldRule = inputFieldRule, value = value) }
            }
            val results = deferredResults.awaitAll()

            results
                .firstOrNull()
                ?: InputFieldValidationResult.Uninitialized // In case the rule list is empty.
        }

    private fun validateRule(
        value: String,
        inputFieldRule: InputFieldRule
    ): InputFieldValidationResult {
        val isRuleValid = isRuleValid(
            value = value,
            inputFieldRule = inputFieldRule
        )
        return if (isRuleValid) InputFieldValidationResult.Success else InputFieldValidationResult.Error(
            errorMessage = inputFieldRule.errorMsg
        )
    }

    private fun isRuleValid(
        value: String,
        inputFieldRule: InputFieldRule
    ): Boolean {
        return when (inputFieldRule) {
            is InputFieldRule.ValidateEmail -> isEmailValid(value)

            is InputFieldRule.ValidateStringMaxLength -> isStringMaxLengthValid(
                value,
                inputFieldRule.length
            )

            is InputFieldRule.ValidateStringMinLength -> isStringMinLengthValid(
                value,
                inputFieldRule.length
            )

            is InputFieldRule.ValidateNotEmpty -> value.isNotEmpty()

            is InputFieldRule.ValidateNotBlank -> value.isNotBlank()

            is InputFieldRule.ValidateRegex -> isRegexMatching(value, inputFieldRule.regex)

            is InputFieldRule.ValidateStringLength -> isLength(value, inputFieldRule.length)

            is InputFieldRule.ValidateStringMatch -> isStringMatching(
                value,
                inputFieldRule.stringToMatch,
                inputFieldRule.isCaseSensitive
            )

            is InputFieldRule.ValidateStringNotMatch -> isStringNotMatching(
                value,
                inputFieldRule.stringToMatch,
                inputFieldRule.isCaseSensitive
            )

            is InputFieldRule.ValidateIntegerValue -> isIntegerValid(value)

            is InputFieldRule.ValidateOverMinIntegerValue -> isIntegerOverMinValue(
                value = value,
                minValue = inputFieldRule.minValue
            )

            is InputFieldRule.ValidateUnderMaxIntegerValue -> isIntegerBelowMaxValue(
                value = value,
                maxValue = inputFieldRule.maxValue
            )

            is InputFieldRule.ValidateBigDecimal -> isBigDecimalValid(value, inputFieldRule.scale)

            is InputFieldRule.ValidateOverMinimumBigDecimalValue -> isBigDecimalOverMinValue(
                value = value,
                scale = inputFieldRule.scale,
                minValue = inputFieldRule.minValue
            )

            is InputFieldRule.ValidateUnderMaximumBigDecimalValue -> isBigDecimalBelowMaxValue(
                value,
                scale = inputFieldRule.scale,
                maxValue = inputFieldRule.maxValue
            )

            is InputFieldRule.ValidateCardNumberValue -> isCardNumberValueValid(value)

            is InputFieldRule.ValidateCardNumberExpirationDate -> isCardNumberExpirationDateValid(
                value = value,
                currentYearMonth = inputFieldRule.currentYearMonth
            )
        }
    }

    private fun isEmailValid(value: String): Boolean =
        value.isNotBlank() && value.isEmail()

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
    ): Boolean = value.equals(
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

    private fun isIntegerValid(value: String): Boolean = value.toIntOrNull() != null

    private fun isIntegerOverMinValue(value: String, minValue: Int): Boolean =
        value.toIntOrNull()?.let { it >= minValue } ?: false

    private fun isIntegerBelowMaxValue(value: String, maxValue: Int): Boolean =
        value.toIntOrNull()?.let { it <= maxValue } ?: false

    private fun isBigDecimalValid(value: String, scale: Int): Boolean =
        value.toBigDecimalOrNull()?.takeIf { it.scale() <= scale } != null

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

    private fun isCardNumberValueValid(value: String): Boolean {
        if (value.length != 16 || value.any { !it.isDigit() }) return false

        var sum = 0
        value.forEachIndexed { index, character ->
            val digit = character.digitToIntOrNull() ?: return false

            sum += if (index % 2 == 0) {
                val tempDigit = digit * 2
                if (tempDigit >= 10) tempDigit - 9 else tempDigit
            } else {
                digit
            }
        }
        return sum % 10 == 0
    }

    private fun isCardNumberExpirationDateValid(
        value: String,
        currentYearMonth: YearMonth
    ): Boolean {
        try {
            if (value.length != 4) return false

            val month = value.take(2).toInt()
            val year = value.takeLast(2).toInt() + 2000

            val providedYearMonth = YearMonth.of(year, month)
            return providedYearMonth >= currentYearMonth
        } catch (_: Exception) {
            return false
        }
    }
}