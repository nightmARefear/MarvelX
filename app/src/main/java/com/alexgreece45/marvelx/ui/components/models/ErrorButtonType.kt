package com.alexgreece45.marvelx.ui.components.models

import com.alexgreece45.marvelx.ui.components.AlertDialogError

/**
 * Defines the type of an error button used in [AlertDialogError].
 */
sealed class ErrorButtonType {
    /**
     * Text button.
     */
    data object Text : ErrorButtonType()

    /**
     * Outlined button.
     */
    data object Outlined : ErrorButtonType()

    /**
     * Contained button.
     */
    data object Contained : ErrorButtonType()

    companion object {
        /**
         * The default error button type; i.e., [Text].
         */
        val defaultType: ErrorButtonType = Text
    }
}