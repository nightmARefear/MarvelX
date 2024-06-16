package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.throttledClickable

/**
 * Displays a single header for the Profile Screen and its nested screens.
 *
 * @param title             The title of the section.
 * @param leadingIcon       The optional leading icon to be displayed at the beginning of the header
 * container.
 * @param trailingIcon      The optional trailing icon to be displayed at the end of the header
 * container.
 * @param throttleClicks    Whether the clicks are executed with a delay in-between them.
 * @param showRipple        Whether to show the ripple effect when the header is clicked.
 * @param onClick           Call to be invoked when the header is clicked.
 */
@Composable
fun ProfileHeader(
    title: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    throttleClicks: Boolean = true,
    showRipple: Boolean = true,
    onClick: () -> Unit
) {
    val localView = LocalView.current
    val headerShape = MaterialTheme.shapes.medium

    /**
     * The modifier to apply to the header.
     */
    val modifier = remember {
        mutableStateOf(
            if (throttleClicks) {
                Modifier.throttledClickable(
                    view = localView,
                    role = Role.Button,
                    showRipple = showRipple,
                    rippleShape = headerShape,
                    onClick = onClick
                )
            } else {
                Modifier.clickableNoRipple(rippleShape = headerShape, onClick = onClick)
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier.value),
        shape = headerShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACING_MEDIUM.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = SPACING_SMALL.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.h6
            )

            trailingIcon?.invoke()
        }
    }
}