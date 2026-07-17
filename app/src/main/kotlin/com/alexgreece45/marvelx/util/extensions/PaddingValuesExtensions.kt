package com.alexgreece45.marvelx.util.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
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
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = padding + this.calculateStartPadding(layoutDirection),
        top = padding + this.calculateTopPadding(),
        end = padding + this.calculateEndPadding(layoutDirection),
        bottom = padding + this.calculateBottomPadding()
    )
}

@Composable
fun PaddingValues.applyContentScreenPaddings(
    paddingValues: PaddingValues,
    isToolbarVisible: Boolean,
    isBottomBarVisible: Boolean,
    isInnerScreen: Boolean
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val systemBarPaddings = WindowInsets.systemBars.asPaddingValues()
    val navigationBarPaddings = WindowInsets.navigationBars.asPaddingValues()

    val topPadding = if (isToolbarVisible) {
        this.calculateTopPadding()
    } else {
        if (!isInnerScreen) {
            systemBarPaddings.calculateTopPadding()
        } else {
            0.dp
        }
    }

    val bottomPadding = if (isBottomBarVisible) {
        this.calculateBottomPadding()
    } else {
        if (!isInnerScreen) {
            navigationBarPaddings.calculateBottomPadding()
        } else {
            0.dp
        }
    }

    return PaddingValues(
        start = paddingValues.calculateStartPadding(layoutDirection) + this.calculateStartPadding(
            layoutDirection
        ),
        top = paddingValues.calculateTopPadding() + topPadding,
        end = paddingValues.calculateEndPadding(layoutDirection) + this.calculateEndPadding(
            layoutDirection
        ),
        bottom = paddingValues.calculateBottomPadding() + bottomPadding
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
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = paddingValues.calculateStartPadding(layoutDirection) + this.calculateStartPadding(
            layoutDirection
        ),
        top = paddingValues.calculateTopPadding() + this.calculateTopPadding(),
        end = paddingValues.calculateEndPadding(layoutDirection) + this.calculateEndPadding(
            layoutDirection
        ),
        bottom = paddingValues.calculateBottomPadding() + this.calculateBottomPadding()
    )
}

/**
 * Applies `0.`[dp] padding to all the edges inside a box.
 */
@Composable
fun screenPaddingsNone(): PaddingValues =
    PaddingValues(all = 0.dp)
