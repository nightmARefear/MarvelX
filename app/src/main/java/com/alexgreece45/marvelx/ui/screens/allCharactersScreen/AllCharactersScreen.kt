package com.alexgreece45.marvelx.ui.screens.allCharactersScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEffect
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEvent
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.destinations.CharacterScreenDestination
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.GenericErrorScreen
import com.alexgreece45.marvelx.util.ItemDescription
import com.alexgreece45.marvelx.util.LoadingScreen
import com.alexgreece45.marvelx.util.NoInternetErrorScreen
import com.alexgreece45.marvelx.util.VSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun AllCharacterScreen(
    navigator: DestinationsNavigator,
    viewModel: AllCharactersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var initLock: Unit by remember { mutableStateOf(Unit) }

    LaunchedEffect(initLock) {
        if (!state.isFirstLaunch) {
            viewModel.setEvent(AllCharactersEvent.LoadFavoriteCharacters)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AllCharactersEffect.NavigateToCharacterDetails -> {
                    initLock = Unit
                    navigator.navigate(CharacterScreenDestination(characterId = effect.characterId))
                }

                is AllCharactersEffect.FavoriteCharacter.Success -> {
                    Toast.makeText(context, effect.successMessage, Toast.LENGTH_SHORT).show()
                }

                is AllCharactersEffect.FavoriteCharacter.Error -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is AllCharactersEffect.UnFavoriteCharacter.Error -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is AllCharactersEffect.UnFavoriteCharacter.Success -> {
                    Toast.makeText(context, effect.successMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LoadingScreen(isLoading = state.isLoading) {
        ContentScreen(
            showToolbar = true,
            toolbarTitle = stringResource(R.string.all_characters_screen_toolbar_title)
        ) { padding ->
            LazyColumn(
                contentPadding = padding
            ) {
                state.errorMessage?.let { errorMessage ->
                    item {
                        GenericErrorScreen(
                            errorMessage = errorMessage
                        )
                    }
                }

                if (!state.hasInternetConnection) {
                    item {
                        NoInternetErrorScreen {
                            viewModel.setEvent(
                                AllCharactersEvent.InitView
                            )
                        }
                    }
                }

                if (state.characters.isNotEmpty()) {
                    itemsIndexed(state.characters,
                        key = { _, character ->
                            character.id
                        }
                    ) { index, character ->
                        CharacterItem(
                            character = character,
                            onClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnCharacterClick(characterId = character.id)
                                )
                            },
                            onFavoriteClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnHeartClick(
                                        characterId = character.id
                                    )
                                )
                            }
                        )

                        // Adds vertical padding at the bottom of each item (except for the last item).
                        if (index != state.characters.lastIndex) {
                            VSpacer.Medium()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterItem(
    character: CharacterDomain,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(DEFAULT_SCREEN_PADDING)
        ) {
            // Name.
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = character.name,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )

            VSpacer.Small()

            // Image.
            val borderWidth = 2.dp
            AsyncImage(
                modifier = Modifier
                    .size(300.dp)
                    .border(
                        BorderStroke(
                            width = borderWidth,
                            color = MaterialTheme.colors.primaryVariant
                        ),
                        CircleShape
                    )
                    .padding(borderWidth)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                model = ImageRequest.Builder(context)
                    .data(character.thumbnail.path + "." + character.thumbnail.extension)
                    .crossfade(true)
                    .build(),
                contentDescription = "${character.name} image",
                contentScale = ContentScale.Fit,
                error = painterResource(R.drawable.ic_broken_image)
            )

            VSpacer.Small()

            // Description.
            ItemDescription(
                description = character.description, emptyDescriptionMessage = stringResource(
                    R.string.no_character_description
                )
            )

            // Favorite IconButton
            IconButton(
                modifier = Modifier.align(Alignment.End),
                onClick = { onFavoriteClick() }
            ) {
                when (character.isFavorite) {
                    true -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite_filled_24),
                            contentDescription = stringResource(R.string.all_characters_screen_favorite_character_icon_description),
                            tint = MaterialTheme.colors.primaryVariant
                        )
                    }

                    false -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite_empty_24),
                            contentDescription = stringResource(R.string.all_characters_screen_not_favorite_character_icon_description),
                            tint = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
            }
        }
    }
}