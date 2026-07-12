package com.alexgreece45.marvelx.common_ui.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

@Composable
fun MarvelXSnackbar(
    snackbarHostState: SnackbarHostState,
    snackbarStateData: SnackbarStateData?
) {
    if (snackbarStateData == null) return

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .padding(12.dp)
            .safeDrawingPadding()
            .fillMaxWidth(),
        snackbar = { MarvelXSnackbarContent(snackbarStateData = snackbarStateData) }
    )
}

@Composable
fun MarvelXSnackbarContent(snackbarStateData: SnackbarStateData) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = when (snackbarStateData.type) {
            SnackbarType.INFO -> MaterialTheme.customColors.snackbarBackgroundColors.info
            SnackbarType.WARNING -> MaterialTheme.customColors.snackbarBackgroundColors.warning
            SnackbarType.ERROR -> MaterialTheme.customColors.snackbarBackgroundColors.error
            SnackbarType.SUCCESS -> MaterialTheme.customColors.snackbarBackgroundColors.success
        },
        contentColor = MaterialTheme.customColors.white,
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        ) {
            if (snackbarStateData.snackbarActionData != null) {
                SnackbarSimpleAction(snackbarStateData)
            } else {
                SnackbarSimple(
                    message = snackbarStateData.message,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

/**
 * Function that composes a snackbar with a message and an action button.
 *
 * @see SnackbarSimple()
 */
@Composable
private fun SnackbarSimpleAction(snackbarStateData: SnackbarStateData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SnackbarSimple(
            message = snackbarStateData.message,
            modifier = Modifier.weight(1f)
        )

        snackbarStateData.snackbarActionData?.let { actionData ->
            TextButton(
                onClick = actionData.onClick,
            ) {
                Text(
                    text = actionData.label
                )
            }
        }
    }
}

/**
 * Function that composes a snackbar with a message.
 *
 * @see SnackbarSimpleAction
 */
@Composable
private fun SnackbarSimple(message: String, modifier: Modifier) {
    return Text(
        text = message,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.h6
    )
}