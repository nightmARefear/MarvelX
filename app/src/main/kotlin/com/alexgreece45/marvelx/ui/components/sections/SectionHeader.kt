package com.alexgreece45.marvelx.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.throttledClickable

/**
 * Displays a single section header.
 *
 * @param title The title of this section.
 * @param titleMaxLines The maximum allowed lines of the title.
 * @param additionalTitleContent Additional title content to be displayed under the title.
 * `Null` by default.
 * @param leadingIcon The optional leading icon to be displayed at the beginning of the header
 * container.
 * @param trailingIcon The optional trailing icon to be displayed at the end of the header
 * container.
 * @param additionalContent Additional optional content displayed below the standard.
 * @param throttleClicks Whether the clicks are executed with a delay in-between them.
 * @param showRipple Whether to show the ripple effect when the header is clicked.
 * @param enabled Whether this section is clickable. To define an action, set [onClick].
 * @param modifier The modifier to be applied to the header container (e.g., a test tag).
 * @param onClick Call to be invoked when the header is clicked. If [enabled] is `false`, then this
 * will have no effect.
 */
@Composable
fun SectionHeader(
    title: String,
    titleMaxLines: Int = SectionHeaderDefaults.TITLE_MAX_LINES,
    additionalTitleContent: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    additionalContent: (@Composable () -> Unit)? = null,
    backgroundColor: Color = SectionHeaderDefaults.BACKGROUND_COLOR,
    throttleClicks: Boolean = true,
    showRipple: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val localView = LocalView.current
    val headerShape = MaterialTheme.shapes.medium

    /**
     * The click modifier to apply to the header.
     */
    val clickModifier = remember {
        mutableStateOf(
            if (throttleClicks) {
                Modifier.throttledClickable(
                    view = localView,
                    role = Role.Button,
                    enabled = enabled,
                    showRipple = showRipple,
                    rippleShape = headerShape,
                    onClick = onClick
                )
            } else {
                Modifier.clickableNoRipple(
                    enabled = enabled,
                    rippleShape = headerShape,
                    role = Role.Button,
                    onClick = onClick
                )
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .then(clickModifier.value),
        shape = headerShape,
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACING_MEDIUM.dp)
        ) {
            // Standard content.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                leadingIcon?.invoke()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (leadingIcon == null) 0.dp else SPACING_SMALL.dp)
                ) {
                    SectionHeaderTitle(
                        title = title,
                        titleMaxLines = titleMaxLines
                    )

                    additionalTitleContent?.let {
                        VSpacer.Small()
                        it()
                    }
                }

                trailingIcon?.invoke()
            }

            // Additional optional content.
            additionalContent?.invoke()
        }
    }
}

@Composable
fun SectionHeaderTitle(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    titleMaxLines: Int = SectionHeaderDefaults.TITLE_MAX_LINES
) {
    Text(
        text = title,
        modifier = modifier,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = titleMaxLines,
        style = MaterialTheme.typography.h6
    )
}

object SectionHeaderDefaults {

    const val TITLE_MAX_LINES = 1

    val BACKGROUND_COLOR: Color
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.colors.surface
}