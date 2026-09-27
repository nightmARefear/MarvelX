package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL

/**
 * A Material Design input chip representing an actively applied filter.
 *
 * This chip displays a text label alongside a trailing close icon. The close icon
 * features an isolated, unbounded circular ripple to indicate its interactive state without
 * triggering a ripple across the entire chip body. Use this component to allow users to
 * easily remove specific filters (like categories) from an active search or comparison grid.
 *
 * @param label The text to display inside the chip.
 * @param onRemoveClick The callback invoked when the trailing close icon is clicked.
 * @param modifier The [Modifier] to be applied to the chip's root layout.
 * @param iconContentDescription The accessibility description for the close icon, used by screen readers.
 */
@Composable
fun ActiveFilterChip(
    label: String,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
    removeButtonTestTag: String? = null
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.onSurface
                    .copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface),
                shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
            )
            .clip(MaterialTheme.shapes.small.copy(CornerSize(percent = 50)))
            .defaultMinSize(minHeight = 32.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2
        )

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = iconContentDescription,
            modifier = Modifier
                .size(18.dp)
                .then(removeButtonTestTag?.let { Modifier.testTag(it) } ?: Modifier)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 12.dp),
                    onClick = onRemoveClick
                ),
            tint = MaterialTheme.customColors.white
        )
    }
}