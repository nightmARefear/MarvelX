package com.alexgreece45.marvelx.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R

/**
 * Displays the navigate icon.
 *
 * @param modifier  The modifier to apply.
 * @param tint      The tint to apply.
 */
@Composable
fun NavigateIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        imageVector = Icons.Filled.KeyboardArrowUp,
        contentDescription = stringResource(R.string.content_description_navigate),
        modifier = modifier.rotate(90f),
        tint = tint
    )
}