package com.alexgreece45.marvelx.util.constants

/**
 * This object contains constants for validation-related purposes for multiple input field types.
 *
 * Notable examples are [Email], and [Password].
 */
object ConstantsInputField {
    data object Email {
        const val minLength = 5
        const val maxLength = 100
    }

    data object Password {
        const val minLength = 10
        const val maxLength = 30
    }
}