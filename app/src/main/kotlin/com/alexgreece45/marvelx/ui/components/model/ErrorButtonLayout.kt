package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.foundation.layout.Column
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout.Double
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout.Single

/**
 * Defines the layout of the error buttons in [ContentErrorScreen]. Both layouts have the primary
 * button. The recommended layout for simple operations is [Single].
 *
 * - [Single] is most commonly used for `Re-trying` an operation.
 * - [Double] is most commonly used for `Re-trying` an operation and `Navigating` to another screen.
 *
 * @param primaryButtonLabel        The label of the primary button. It has a default value of
 * `blank` representing that no label is set. [ContentErrorScreen] internally detects this and sets
 * a pre-defined label.
 * @param onPrimaryButtonClicked    The action(s) to be invoked when the primary button is clicked.
 */
sealed class ErrorButtonLayout(
    open val primaryButtonLabel: String,
    open val onPrimaryButtonClicked: () -> Unit
) {
    /**
     * This layout represents a single button (primary) which of type [WrapButton]. This
     * represents an important action such as `Re-trying` an operation.
     *
     * @param primaryButtonLabel        The label of the primary button. It has a default value of
     * `blank` representing that no label is set. [ContentErrorScreen] internally detects this and
     * sets a pre-defined label.
     * @param onPrimaryButtonClicked    The action(s) to be invoked when the primary button is
     * clicked.
     */
    data class Single(
        override val primaryButtonLabel: String = "",
        override val onPrimaryButtonClicked: () -> Unit
    ) : ErrorButtonLayout(primaryButtonLabel, onPrimaryButtonClicked)

    /**
     * This layout represents two buttons in a [Column] layout. The first button (primary) is of
     * type [WrapButton] and represents the most important action. The second button (secondary)
     * is of type [WrapOutlinedButton] represents a less important action.
     *
     * @param primaryButtonLabel        The label of the primary button. It has a default value of
     * `blank` representing that no label is set. [ContentErrorScreen] internally detects this and
     * sets a pre-defined label.
     * @param onPrimaryButtonClicked    The action(s) to be invoked when the primary button is
     * clicked.
     * @param secondaryButtonLabel      The label of the secondary button. It is strongly advise
     * not to set it to `blank` as it will have the same label as the `primary` button.
     * @param onSecondaryButtonClicked  The action(s) to be invoked when the secondary button is
     * clicked.
     */
    data class Double(
        override val primaryButtonLabel: String = "",
        override val onPrimaryButtonClicked: () -> Unit,
        val secondaryButtonLabel: String,
        val onSecondaryButtonClicked: () -> Unit
    ) : ErrorButtonLayout(primaryButtonLabel, onPrimaryButtonClicked)
}