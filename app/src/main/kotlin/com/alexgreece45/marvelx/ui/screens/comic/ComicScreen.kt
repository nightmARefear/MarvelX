package com.alexgreece45.marvelx.ui.screens.comic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ItemDescription
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.screens.comic.model.ComicContract
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer

@Composable
fun ComicScreen(
    onEventSent: (event: ComicContract.Event) -> Unit,
    state: ComicContract.State,
    effect: SingleObserverFlow<ComicContract.Effect>,
    onNavigationEffect: (ComicContract.Effect.Navigation) -> Unit,
    comicId: Int
) {
    LaunchedEffect(Unit) {
        onEventSent(ComicContract.Event.Init(comicId))
    }

    ObserveUiEffect(flow = effect, tag = "ComicScreen") { uiEffect ->
        when (uiEffect) {
            is ComicContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        onBack = {
            onEventSent(ComicContract.Event.OnBackClicked)
        },
        navigableAction = ScreenNavigableAction.Backable(testTag = ComicTestTags.BACK_BUTTON) {
            onEventSent(ComicContract.Event.OnBackClicked)
        },
        showToolbar = true,
        toolbarTitle = state.comic?.title.orEmpty(),
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            state.comic?.let { comic ->
                item {
                    ComicItem(
                        comic = comic
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicItem(comic: ComicDomain) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Image.
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(comic.thumbnail.path + "." + comic.thumbnail.extension).crossfade(true)
                .build(),
            contentDescription = "${comic.title} image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            error = painterResource(R.drawable.ic_broken_image),
            contentScale = ContentScale.Fit
        )

        VSpacer.Medium()

        // Description
        ItemDescription(
            description = comic.textObjects.firstOrNull()?.text.orEmpty(),
            emptyDescriptionMessage = stringResource(R.string.comic_screen_no_description)
        )

        VSpacer.Medium()

        // Pricing
        Section(title = stringResource(R.string.comic_screen_label_section_pricing_details)) {
            Column(
                modifier = Modifier
                    .padding(SPACING_TINY.dp)
                    .fillMaxWidth()
            ) {
                comic.prices.forEachIndexed { index, comicPriceDomain ->
                    SectionItem(
                        prefix = comicPriceDomain.type.formattedName,
                        suffix = comicPriceDomain.price.toString()
                    )
                    if (index != comic.prices.size - 1) {
                        HSpacer.Medium()
                    }
                }
            }
        }

        // Creators.
        Section(title = stringResource(R.string.comic_screen_label_section_creators)) {
            Column(
                modifier = Modifier
                    .padding(SPACING_TINY.dp)
                    .fillMaxWidth()
            ) {
                comic.creators.items.forEachIndexed { index, creatorSummary ->
                    SectionItem(
                        prefix = creatorSummary.name,
                        suffix = creatorSummary.role
                    )
                    if (index != comic.creators.items.size - 1) {
                        HSpacer.Medium()
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        content()
    }
}

@Composable
private fun SectionItem(
    prefix: String,
    suffix: String
) {
    Text(text = "$prefix: $suffix")
}
