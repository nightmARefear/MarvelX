package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * A Wrapper [Composable] for the native [Switch] providing custom styling.
 *
 * @param checked           Whether this switch has been selected.
 * @param onCheckedToggle   The callback to be invoked when the switch is clicked. Through the
 * lambda, the current switch state as a [Boolean].
 * @param modifier          The modifier to be applied to the switch layout.
 * @param enabled           Whether this switch is enabled.
 * @param interactionSource The [MutableInteractionSource] representing the stream of [Interaction]s
 * for this Switch. You can create and pass in your own remembered [MutableInteractionSource] if you
 * want to observe [Interaction]s and customize the appearance / behavior of this Switch in
 * different [Interaction]s.
 * @param colors            The track and thumb colors of this switch. The default value is the
 * application's recommended way of handling the styling, but you can provide your own implementation
 * if needed.
 */
@Composable
fun WrapSwitch(
    checked: Boolean,
    onCheckedToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors(checkedTrackAlpha = 0.3f)
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedToggle,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = colors
    )
}