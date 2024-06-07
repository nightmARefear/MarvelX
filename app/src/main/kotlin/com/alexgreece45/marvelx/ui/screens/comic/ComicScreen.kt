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
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ItemDescription
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.screens.comic.model.ComicContract
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.flow.Flow

@Composable
fun ComicScreen(
    state: ComicContract.State,
    onEventSent: (event: ComicContract.Event) -> Unit,
    effect: Flow<ComicContract.Effect>,
    popBackStack: () -> Unit,
    comicId: Int
) {
    LaunchedEffect(Unit) {
        onEventSent(ComicContract.Event.Init(comicId))

        effect.collect { comicEffect ->
            when (comicEffect) {
                ComicContract.Effect.PopBackStack -> popBackStack()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            ComicContract.Event.OnBackClicked
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Image.
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(comic.thumbnail.path + "." + comic.thumbnail.extension).crossfade(true)
                .build(),
            contentDescription = "${comic.title} image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_broken_image)
        )

        VSpacer.Medium()

        // Description
        ItemDescription(
            description = comic.textObjects.firstOrNull()?.text.orEmpty(),
            emptyDescriptionMessage = stringResource(R.string.comic_screen_no_description)
        )

        VSpacer.Medium()

        // Pricing
        Section(title = "Pricing Details") {
            Column(
                modifier = Modifier
                    .padding(SPACING_TINY.dp)
                    .fillMaxWidth()
            ) {
                comic.prices.forEach { comicPriceDomain ->
                    SectionItem(
                        rowItem = Pair(
                            comicPriceDomain.type.formattedName,
                            comicPriceDomain.price.toString()
                        )
                    )
                    HSpacer.Medium() // todo: do not place in the last element
                }
            }
        }

        // Creators.
        Section(title = "Creators") {
            Column(
                modifier = Modifier
                    .padding(SPACING_TINY.dp)
                    .fillMaxWidth()
            ) {
                comic.creators.items.forEach { creatorSummary ->
                    SectionItem(
                        rowItem = Pair(creatorSummary.name, creatorSummary.role)
                    )
                    HSpacer.Medium() // todo: do not place in the last element
                }
            }
        }
    }
}

@Composable
fun Section(
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
fun SectionItem(
    rowItem: Pair<String, String>,
) {
    Text(text = "${rowItem.first}: ${rowItem.second}")

}