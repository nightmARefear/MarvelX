package com.alexgreece45.marvelx.common_ui.components.statusBox

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A highly customizable status box component used to display informational messages, warnings, errors, or success states.
 *
 * This component provides a stylized container with a colored background, an optional outline, a leading icon, and a title label.
 * It also supports appending additional content below the title row, making it suitable for displaying complex status messages
 * (e.g., bulleted requirement lists or detailed error descriptions).
 *
 * @param title The primary text displayed in the status box.
 * @param titleColor The color applied to the [title].
 * @param backgroundColor The color used for the box's background.
 * @param modifier The [Modifier] to be applied to the outer container.
 * @param outlineColor An optional color for the box's border. If provided, a 2.dp border is drawn around the shape.
 * @param icon An optional composable slot for a leading icon, positioned horizontally before the [title].
 * @param shape The shape of the status box and its outline. Defaults to [StatusContainerDefaults.shape].
 * @param innerPadding The padding applied uniformly inside the status box container. Defaults to [StatusContainerDefaults.innerPadding].
 * @param horizontalArrangement The horizontal arrangement applied to the row containing the [icon] and [title]. Defaults to [StatusContainerDefaults.horizontalArrangement].
 * @param verticalAlignment The vertical alignment applied to the row containing the [icon] and [title]. Defaults to [StatusContainerDefaults.verticalAlignment].
 * @param extraContent An optional composable slot for displaying additional layout elements below the main title row.
 */
@Composable
fun StatusContainer(
    title: String,
    titleColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    outlineColor: Color? = null,
    icon: (@Composable () -> Unit)? = null,
    shape: Shape = StatusContainerDefaults.shape,
    innerPadding: Dp = StatusContainerDefaults.innerPadding,
    horizontalArrangement: Arrangement.Horizontal = StatusContainerDefaults.horizontalArrangement,
    verticalAlignment: Alignment.Vertical = StatusContainerDefaults.verticalAlignment,
    extraContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
            .let { currentModifier ->
                if (outlineColor != null) {
                    currentModifier.border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = outlineColor
                        ), shape = shape
                    )
                } else {
                    currentModifier
                }
            }
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            icon?.invoke()

            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.h6
            )
        }

        extraContent?.invoke()
    }
}

object StatusContainerDefaults {

    val shape: Shape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.medium

    val innerPadding: Dp = 16.dp

    val horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp)
    val verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
}