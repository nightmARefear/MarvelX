package com.alexgreece45.marvelx.ui.screens.comicScreen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.viewmodels.ComicEvent
import com.alexgreece45.marvelx.domain.viewmodels.ComicViewModel
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.ItemDescription
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun ComicScreen(
    navController: NavController,
    viewModel: ComicViewModel = hiltViewModel(),
    comicId: Int
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            ComicEvent.InitView(
                comicId = comicId
            )
        )

        viewModel.effect.collect { comicEffect ->
            when (comicEffect) {
                else -> TODO("to be implemented")
            }
        }
    }

    ContentScreen(
        onBack = { navController.popBackStack() },
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