package com.alexgreece45.marvelx.ui.screens.characterScreen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.viewmodels.CharacterEffect
import com.alexgreece45.marvelx.domain.viewmodels.CharacterEvent
import com.alexgreece45.marvelx.domain.viewmodels.CharacterViewModel
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.GeneralErrorScreen
import com.alexgreece45.marvelx.util.VSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(/*route="allCharacters/character/{characterId}"*/)
@Composable
fun CharacterScreen(
    navigator: DestinationsNavigator,
    viewModel: CharacterViewModel = hiltViewModel(),
    characterId: Int,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = (context as? Activity)

    LaunchedEffect(Unit) {
        viewModel.setEvent(CharacterEvent.InitView(characterId))

        viewModel.effect.collect { effect ->
            when (effect) {
                CharacterEffect.OnBackClick -> {
                    navigator.popBackStack()
                }
            }
        }
    }

    ContentScreen(
        onBack = { viewModel.setEvent(CharacterEvent.OnBackClick) },
        toolBar = true,
        toolbarTitle = state.character?.name
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                state.errorMessage?.let { errorMessage ->
                    // Show error screen.
                    GeneralErrorScreen(errorMessage = errorMessage)
                }

                state.character?.let { character ->
                    CharacterItem(character = character)

                    // Comics.
                    VSpacer.Medium()

                    Text(
                        text = "Comics",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )

                    VSpacer.Medium()

                    LazyRow(
                        contentPadding = padding,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = state.comics,
                            key = { comic ->
                                comic.id
                            }
                        ) { comic ->
                            ComicItem(comic = comic)
                        }
                    }

                    // Events.

                    // Series.

                    // Stories.
                }
            }
        }
    }
}

@Composable
private fun CharacterItem(
    character: CharacterDomain
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 200.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(character.thumbnail.path + "." + character.thumbnail.extension)
                .crossfade(true).build(),
            contentDescription = "${character.name} image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_broken_image)
        )

        VSpacer.Small()

        // Description.
        if (character.description.isBlank().not()) {
            Text(
                text = character.description, style = MaterialTheme.typography.body1.copy(
                    textAlign = TextAlign.Justify
                )
            )
        } else {
            Text(
                text = stringResource(R.string.no_character_description),
                style = MaterialTheme.typography.body1.copy(
                    color = Color.Gray, fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Composable
private fun ComicItem(
    comic: ComicDomain
) {
    Card {
        Column {
            AsyncImage(
                modifier = Modifier.size(150.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(comic.thumbnail.path + "." + comic.thumbnail.extension)
                    .crossfade(true)
                    .build(),
                contentDescription = comic.description,
                contentScale = ContentScale.Fit,
                error = painterResource(R.drawable.ic_broken_image)
            )
        }
    }
}