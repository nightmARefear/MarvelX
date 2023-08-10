package com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.viewmodels.AllCharacterComicsEffect
import com.alexgreece45.marvelx.domain.viewmodels.AllCharacterComicsEvent
import com.alexgreece45.marvelx.domain.viewmodels.AllCharacterComicsViewModel
import com.alexgreece45.marvelx.ui.screens.destinations.ComicScreenDestination
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AllCharacterComicsScreen(
    navigator: DestinationsNavigator,
    viewModel: AllCharacterComicsViewModel = hiltViewModel(),
    characterId: Int
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(AllCharacterComicsEvent.InitView(characterId = characterId))

        viewModel.effect.collect { effect ->
            when (effect) {
                AllCharacterComicsEffect.NavigateToCharacterScreen -> {
                    navigator.popBackStack()
                }

                is AllCharacterComicsEffect.NavigateToComicScreen -> {
                    navigator.navigate(ComicScreenDestination(comicId = effect.comicId))
                }
            }
        }
    }

    ContentScreen(
        onBack = { navigator.popBackStack() },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.all_comics_screen_toolbar_title)
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            itemsIndexed(
                items = state.comics,
                key = { _, comicDomain ->
                    comicDomain.id
                }
            ) { index, comicDomain ->
                ComicItem(comic = comicDomain) {
                    viewModel.setEvent(AllCharacterComicsEvent.OnComicClick(comicId = comicDomain.id))
                }

                // Adds vertical padding at the bottom of each item (except for the last item).
                if (index != state.comics.lastIndex) VSpacer.Small()
            }
        }
    }
}

@Composable
private fun ComicItem(
    comic: ComicDomain,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(85.dp)
                    .aspectRatio(ratio = 0.66f)
                    .clip(MaterialTheme.shapes.medium),
                model = ImageRequest.Builder(context)
                    .data(comic.thumbnail.downloadUrl)
                    .crossfade(true).build(),
                contentDescription = comic.thumbnail.contentDescription
            )

            HSpacer.Custom(width = 8)

            Text(
                text = comic.title,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}