package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import kotlinx.coroutines.launch

@Composable
fun WrapHorizontalPager(
    initialPage: Int = 0,
    pageCount: Int,
    selectedIndex: Int,
    contentPaddingValues: PaddingValues = PaddingValues(),
    onPageChanged: (Int) -> Unit,
    pageContent: @Composable (Int) -> Unit
) {

    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }
    val currentOnPageChanged by rememberUpdatedState(onPageChanged)
    var isPagerScrolling by remember { mutableStateOf(false) }

    var hasPagerBeenInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) {
        val scrollJob = launch {
            isPagerScrolling = true
            pagerState.animateScrollToPage(selectedIndex)
            isPagerScrolling = false
        }
        scrollJob.start()
        scrollJob.invokeOnCompletion { isPagerScrolling = false }
    }

    LaunchedEffect(currentOnPageChanged) {
        launch {
            try {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    if (isPagerScrolling.not()) {
                        if (hasPagerBeenInitialized) {
                            onPageChanged(page)
                        } else {
                            hasPagerBeenInitialized = true
                        }
                    }
                }
            } finally {
                isPagerScrolling = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPaddingValues,
            pageSpacing = SPACING_MEDIUM.dp, // TODO: Examine why it has no effect.
        ) { page ->
            pageContent(page)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(
                SPACING_SMALL.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { pageIndex ->
                val dotColor =
                    if (pagerState.currentPage == pageIndex) {
                        MaterialTheme.customColors.genericRed
                    } else {
                        MaterialTheme.customColors.genericRed.copy(alpha = 0.5f)
                    }
                val dotWidth =
                    if (pagerState.currentPage == pageIndex) SPACING_MEDIUM.dp else SPACING_SMALL.dp

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(dotColor)
                        .height(SPACING_SMALL.dp)
                        .width(dotWidth)
                        .clickableNoRipple(onClick = { onPageChanged(pageIndex) })
                )
            }
        }
    }
}
