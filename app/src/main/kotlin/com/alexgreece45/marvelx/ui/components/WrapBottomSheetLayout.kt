package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.SPACING_MEDIUM

/**
 * A wrapper composable for the native [ModalBottomSheetLayout] providing the app's custom styling.
 *
 * The sheet shape and scrim color are already set. If you wish to override them, you can do so,
 * but it's not recommended.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WrapModalBottomSheetLayout(
    sheetContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    sheetGesturesEnabled: Boolean = true,
    sheetShape: Shape = RoundedCornerShape(
        topStart = SPACING_MEDIUM.dp,
        topEnd = SPACING_MEDIUM.dp
    ),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = if (isSystemInDarkTheme()) MaterialTheme.colors.background.copy(alpha = 0.55f) else MaterialTheme.colors.onSurface.copy(
        alpha = 0.4f
    ),
    onDismissed: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    onDismissed?.let {
        LaunchedEffect(Unit) {
            snapshotFlow {
                sheetState.currentValue
            }.collect { newBottomSheetStateValue ->
                if (newBottomSheetStateValue == ModalBottomSheetValue.Hidden) {
                    onDismissed()
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = { Box(modifier = Modifier.navigationBarsPadding()) { sheetContent() } },
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = sheetGesturesEnabled,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor,
        content = content
    )
}