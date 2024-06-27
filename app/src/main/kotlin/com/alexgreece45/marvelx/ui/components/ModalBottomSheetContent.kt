package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer

/**
 * This [Composable] is used to draw the content of a modal bottom sheet. By default the drag handle
 * is always shown and has a standard padding from the top of this bottom sheet of [SPACING_MEDIUM]
 * .[dp], and if no value is provided for the padding values, a default one is used as well.
 *
 * @param paddingValues     The padding values to apply. It has the default value of [SPACING_MEDIUM]
 * .dp for both horizontal and vertical edges.
 * @param content           The content.
 */
@Composable
fun ModalBottomSheetContent(
    paddingValues: PaddingValues? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                paddingValues = paddingValues ?: modalBottomSheetContentDefaultPaddingValues
            )
    ) {
        Box(
            modifier = Modifier.padding(
                start = if (paddingValues != null) SPACING_MEDIUM.dp else 0.dp,
                top = if (paddingValues != null) SPACING_SMALL.dp else 0.dp,
                end = if (paddingValues != null) SPACING_MEDIUM.dp else 0.dp
            )
        ) {
            ModalBottomSheetDragHandle()
        }
        VSpacer.Medium()
        content()
    }
}

/**
 * Draws the bottom sheet drag handle.
 */
@Composable
private fun ModalBottomSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(SPACING_TINY.dp)
                .background(
//                    color = MaterialTheme.colors.surface,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

private val modalBottomSheetContentDefaultPaddingValues: PaddingValues =
    PaddingValues(
        start = SPACING_MEDIUM.dp,
        top = SPACING_SMALL.dp,
        end = SPACING_MEDIUM.dp,
        bottom = SPACING_MEDIUM.dp
    )
