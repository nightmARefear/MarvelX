package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.autofill.AutofillType

/**
 * Wrapper class that contains the [autofillTypes] of a given field and what happens
 * when its filled through [onFill].
 *
 * @param autofillTypes The autofill types to apply.
 * @param onFill        The action(s) to invoke when on-fill occurs.
 */
data class AutofillData @OptIn(ExperimentalComposeUiApi::class) constructor(
    val autofillTypes: List<AutofillType>,
    val onFill: (String) -> Unit
)
