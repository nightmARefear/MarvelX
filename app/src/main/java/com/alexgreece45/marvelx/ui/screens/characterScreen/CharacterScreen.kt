package com.alexgreece45.marvelx.ui.screens.characterScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.viewmodels.CharacterEffect
import com.alexgreece45.marvelx.domain.viewmodels.CharacterEvent
import com.alexgreece45.marvelx.domain.viewmodels.CharacterViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.util.IMAGE_BORDER_WIDTH
import com.alexgreece45.marvelx.ui.IconData
import com.alexgreece45.marvelx.ui.ItemDescription
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.ui.ToolbarAction
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.ui.WrapperContentScreen

@Composable
fun CharacterScreen(
    navController: NavController,
    viewModel: CharacterViewModel = hiltViewModel(),
    characterId: Int
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(CharacterEvent.InitView(characterId))

        viewModel.effect.collect { effect ->
            when (effect) {
                CharacterEffect.OnBackClick -> {
                    navController.popBackStack()
                }

                is CharacterEffect.NavigateToComicScreen -> {
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = effect.comicId
                        )
                    )
                }

                is CharacterEffect.NavigateToAllComicsScreen -> {
                    navController.navigate(
                        Screen.AllCharacterComicsScreen.createNavRoute(
                            characterId = effect.characterId,
                            toolbarTitlePrefix = effect.toolbarTitlePrefix
                        )
                    )
                }

                is CharacterEffect.DisplayError -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is CharacterEffect.DisplayOnFavoriteClickMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    WrapperContentScreen(
        isLoading = state.isLoading,
        hasInternetConnection = state.hasInternetConnection,
        errorMessage = state.errorMessage,
        onRetryClick = {
            viewModel.setEvent(
                CharacterEvent.InitView(
                    characterId = characterId
                )
            )
        }
    ) {
        ContentScreen(
            onBack = { viewModel.setEvent(CharacterEvent.OnBackClick) },
            showToolbar = true,
            toolbarTitle = state.character?.name,
            toolbarActions = state.character?.let { characterDomain ->
                listOf(
                    // Favorite button.
                    ToolbarAction(
                        label = stringResource(R.string.character_screen_row_comic_toolbar_action_favorite_title),
                        order = 0,
                        iconData = when (characterDomain.isFavorite) {
                            true -> {
                                IconData(
                                    iconResource = R.drawable.ic_favorite_filled_24,
                                    contentDescription = stringResource(R.string.ic_favorite_content_description),
                                    colorTint = MaterialTheme.colors.primaryVariant
                                )
                            }

                            false -> {
                                IconData(
                                    iconResource = R.drawable.ic_favorite_empty_24,
                                    contentDescription = stringResource(R.string.ic_not_favorite_content_description),
                                    colorTint = MaterialTheme.colors.primaryVariant
                                )
                            }
                        }
                    ) {
                        viewModel.setEvent(CharacterEvent.OnFavoriteClick)
                    }
                )
            }
        ) { padding ->
            LazyColumn(contentPadding = padding) {
                state.character?.let { character ->
                    item {
                        // Character.
                        CharacterItem(
                            character = character
                        )

                        // Comics.
                        LazyRowTemplate(
                            title = stringResource(R.string.character_screen_row_comic_toolbar_title),
                            items = state.comics,
                            itemKey = { comic -> comic.id },
                            onArrowRightClick = { viewModel.setEvent(CharacterEvent.OnArrowRightClick) }
                        ) { comic ->
                            ComicItem(
                                comic = comic,
                                onClick = {
                                    viewModel.setEvent(
                                        CharacterEvent.OnComicClick(
                                            comicId = comic.id
                                        )
                                    )
                                }
                            )
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
    character: CharacterDomain,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Image.
        AsyncImage(
            modifier = Modifier
                .size(225.dp)
                .border(
                    BorderStroke(
                        width = IMAGE_BORDER_WIDTH,
                        color = MaterialTheme.colors.primaryVariant
                    ),
                    CircleShape
                )
                .padding(IMAGE_BORDER_WIDTH)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            model = ImageRequest.Builder(LocalContext.current)
                .data(character.thumbnail.downloadUrl)
                .crossfade(true)
                .build(),
            contentDescription = "${character.name}'s image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_broken_image)
        )

        VSpacer.Medium()

        // Description.
        ItemDescription(
            description = character.description, emptyDescriptionMessage = stringResource(
                R.string.no_character_description
            )
        )
    }
}

@Composable
fun <T> LazyRowTemplate(
    title: String,
    items: List<T>,
    itemKey: ((item: T) -> Any)? = null,
    onArrowRightClick: () -> Unit,
    content: @Composable (T) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
        )

        IconButton(
            onClick = { onArrowRightClick() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_32),
                contentDescription = stringResource(R.string.character_screen_arrow_right_comic_content_description),
                tint = MaterialTheme.colors.onBackground
            )
        }
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        items(
            items = items,
            key = itemKey
        ) {
            content(it)
        }
    }
}

@Composable
private fun ComicItem(
    backgroundColor: Color = Color.Transparent,
    comic: ComicDomain,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(backgroundColor),
    ) {
        AsyncImage(
            modifier = Modifier
                .width(120.dp)
                .aspectRatio(ratio = 0.66f)
                .clip(RoundedCornerShape(SPACING_SMALL.dp))
                .clickable {
                    onClick()
                },
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
