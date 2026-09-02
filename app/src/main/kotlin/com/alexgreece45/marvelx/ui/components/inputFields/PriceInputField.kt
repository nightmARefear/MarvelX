package com.alexgreece45.marvelx.ui.components.inputFields

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationCurrencySuffix

@Composable
fun PriceInputField(
    inputField: InputFieldUi.Price,
    label: String,
    placeholder: String,
    helperText: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    onValueChanged: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextFieldValidation(
        value = inputField.value,
        onValueChange = onValueChanged,
        modifier = modifier.onFocusChanged { focusState ->
            val wasFocused = isFocused
            isFocused = focusState.isFocused

            if (wasFocused && !isFocused) {
                val formatted = inputField.formattedValue
                if (formatted != inputField.value) {
                    onValueChanged(formatted)
                }
            }
        },
        enabled = enabled,
        readOnly = !enabled,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        errorText = when (val validationResult = inputField.validationResult) {
            is InputFieldValidationResult.Error -> validationResult.errorMessage
            InputFieldValidationResult.Success,
            InputFieldValidationResult.Uninitialized -> ""
        },
        helperText = helperText,
        isError = inputField.validationResult.isError,
        visualTransformation = if (isFocused || inputField.value.isEmpty()) {
            VisualTransformation.None
        } else {
            VisualTransformationCurrencySuffix()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        ),
        minLines = minLines,
        maxLines = maxLines
    )
}