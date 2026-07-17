package com.alexgreece45.marvelx.ui.screens.marketplace.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

@Composable
fun WizardTextInputField(
    label: String,
    placeholder: String,
    @SuppressLint("ModifierParameter") modifier : Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    wizardInputField: WizardInputFieldUi.InputField,
    onValueChanged: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextFieldValidation(
        value = wizardInputField.value,
        onValueChange = onValueChanged,
        modifier = modifier,
        enabled = enabled,
        readOnly = !enabled,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        errorMessage = wizardInputField.validationResult.message,
        isError = !wizardInputField.validationResult.isValid,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = false,
        minLines = minLines,
        maxLines = maxLines
    )
}
