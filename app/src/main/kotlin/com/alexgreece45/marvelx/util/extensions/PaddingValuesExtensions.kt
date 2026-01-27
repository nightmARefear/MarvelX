package com.alexgreece45.marvelx.util.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING

/**
 * Applies padding to all the edges inside a box.
 */
@Composable
fun PaddingValues.applyPadding(padding: Dp = DEFAULT_SCREEN_PADDING): PaddingValues {
    return PaddingValues(
        start = padding,
        top = padding + this.calculateTopPadding(),
        end = padding,
        bottom = padding + this.calculateBottomPadding()
    )
}

/**
 * Applies **only** top and bottom paddings.
 */
@Composable
fun PaddingValues.applyTBPadding(padding: Dp = DEFAULT_SCREEN_PADDING): PaddingValues {
    return PaddingValues(
        start = 0.dp,
        top = padding + this.calculateTopPadding(),
        end = 0.dp,
        bottom = padding + this.calculateBottomPadding()
    )
}

/**
 * Returns a new [PaddingValues] object with the padding values of this instance added to the
 * padding values of the provided [paddingValues] object.
 *
 * The resulting padding for each edge (start, top, end, bottom) is calculated by adding the
 * corresponding padding values from both [PaddingValues] objects.
 *
 * #### Note: This instance has no padding for the `start` and `end` edges.
 *
 * @param paddingValues The [PaddingValues] object whose padding values will be added to this
 * instance.
 * @return              A new [PaddingValues] object with the combined padding values.
 */
@Composable
fun PaddingValues.applyPaddingValues(paddingValues: PaddingValues): PaddingValues {
    return PaddingValues(
        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
        top = paddingValues.calculateTopPadding() + this.calculateTopPadding(),
        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
        bottom = paddingValues.calculateBottomPadding() + this.calculateBottomPadding()
    )
}

/**
 * Applies `0.`[dp] padding to all the edges inside a box.
 */
@Composable
fun screenPaddingsNone(): PaddingValues =
    PaddingValues(all = 0.dp)
