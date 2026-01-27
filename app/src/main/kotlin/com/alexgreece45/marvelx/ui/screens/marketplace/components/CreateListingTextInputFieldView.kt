package com.alexgreece45.marvelx.ui.screens.marketplace.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi

@Composable
fun CreateListingTextInputFieldView(
    label: String,
    placeholder: String,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    listingFieldUi: CreateListingFieldUi.InputField,
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextFieldValidation(
        value = listingFieldUi.value,
        onValueChange = onValueChanged,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = !enabled,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        errorMessage = listingFieldUi.validationResult.message,
        isError = !listingFieldUi.validationResult.isValid,
        keyboardOptions = keyboardOptions,
        singleLine = false,
        minLines = minLines,
        maxLines = maxLines
    )
}
