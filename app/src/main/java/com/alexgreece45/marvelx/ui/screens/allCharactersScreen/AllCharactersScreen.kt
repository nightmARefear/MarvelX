package com.alexgreece45.marvelx.ui.screens.allCharactersScreen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.character.CharacterDomain
import com.alexgreece45.marvelx.data.uiModels.CharacterCardUi
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEffect
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEvent
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersViewModel
import com.alexgreece45.marvelx.ui.BottomBarAction
import com.alexgreece45.marvelx.ui.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.ui.ExtendedFabData
import com.alexgreece45.marvelx.ui.GenericErrorScreen
import com.alexgreece45.marvelx.ui.ModalBottomSheet
import com.alexgreece45.marvelx.ui.NormalFabData
import com.alexgreece45.marvelx.ui.SearchBar
import com.alexgreece45.marvelx.ui.SortingModalBottomSheetContent
import com.alexgreece45.marvelx.ui.isScrollingUp
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import kotlinx.coroutines.coroutineScope

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun AllCharactersScreen(
    navigateToCharacterScreen: (characterId: Int) -> Unit,
    bottomNavigationBarData: (items: List<BottomBarAction>, backgroundColor: Color, topHorizontalColor: Color?, selectedItemTopHorizontalColor: Color?, elevation: Dp) -> BottomNavigationBarData,
    viewModel: AllCharactersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Hidden },
        skipHalfExpanded = true
    )

    val lazyGridState = rememberLazyGridState()

    val context = LocalContext.current
    var initLock: Unit by remember { mutableStateOf(Unit) }

    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(initLock) {
        if (!state.isFirstLaunch) {
            viewModel.setEvent(AllCharactersEvent.LoadFavoriteCharacters)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(AllCharactersEvent.InitView)

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AllCharactersEffect.Navigation.NavigateToCharacterScreen -> {
                    initLock = Unit
                    navigateToCharacterScreen(effect.characterId)
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

                AllCharactersEffect.SortingBottomSheet.Hide -> {
                    coroutineScope {
                        modalBottomSheetState.hide()
                    }
                }

                AllCharactersEffect.SortingBottomSheet.Show -> {
                    coroutineScope {
                        modalBottomSheetState.show()
                    }
                }

                AllCharactersEffect.CloseKeyboard -> {
                    keyboard?.hide()
                }
            }
        }
    }

    ModalBottomSheet(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(
            topStart = SPACING_SMALL.dp,
            topEnd = SPACING_SMALL.dp
        ),
        scrimColor = MaterialTheme.colors.background.copy(
            alpha = 0.5f
        ),
        sheetContent = {
            SortingModalBottomSheetContent(
                contentPadding = DEFAULT_SCREEN_PADDING,
                selectedItem = state.sortingType,
                onItemClick = { sortingType ->
                    viewModel.setEvent(AllCharactersEvent.Sorting.OnItemClick(sortingType))
                },
                onCloseClick = {
                    viewModel.setEvent(AllCharactersEvent.Sorting.OnCloseClick)
                }
            )
        }
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            showToolbar = true,
            toolbarTitle = stringResource(R.string.all_characters_screen_toolbar_title),
            fabData = if (state.error == null) { // Display FAB only when there's no error.
                ExtendedFabData(
                    extended = lazyGridState.isScrollingUp(),
                    normalFabData = NormalFabData(
                        label = stringResource(R.string.all_characters_screen_fab_label),
                        iconResource = R.drawable.ic_filter_32,
                        backgroundColor = null,
                        contentColor = null,
                        onClick = { viewModel.setEvent(AllCharactersEvent.OnFabClick) }
                    )
                )
            } else null,
            bottomNavigationBarData = bottomNavigationBarData(
                BottomBarAction.getDefaultBottomBarActions(),
                MaterialTheme.colors.background,
                MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                MaterialTheme.colors.primaryVariant.copy(alpha = 0.8f),
                16.dp,
            )
        ) { contentPadding ->
            state.error?.let { error ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding
                ) {
                    item {
                        GenericErrorScreen(
                            errorMessage = error.errorMessage,
                            onRetryClick = {
                                error.onRetryClick.invoke()
                            }
                        )
                    }
                }
            } ?: run {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        SearchBar(
                            searchTerm = state.searchBarTerm,
                            hint = stringResource(R.string.all_characters_screen_search_bar_hint),
                            onValueChange = { newSearchTerm ->
                                viewModel.setEvent(
                                    AllCharactersEvent.SearchBar.OnKeyboardType(
                                        searchTerm = newSearchTerm
                                    )
                                )
                            },
                            onCloseClick = {
                                viewModel.setEvent(AllCharactersEvent.SearchBar.OnCloseClick)
                            }
                        )
                    }

                    items(
                        items = state.filteredCharacters,
                        key = { characterCardUi ->
                            characterCardUi.id
                        }
                    ) { characterCardUi ->
                        CharacterItem(
                            character = characterCardUi,
                            onClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnCharacterClick(characterId = characterCardUi.id)
                                )
                            },
                            onFavoriteClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnHeartClick(
                                        characterId = characterCardUi.id
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CharacterItem(
    character: CharacterCardUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Image.
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                model = ImageRequest.Builder(context)
                    .data(character.imageDownloadUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = character.imageContentDescription,
                contentScale = ContentScale.FillBounds,
                error = painterResource(R.drawable.ic_broken_image)
            )

            // Favorite IconButton
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
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

            // Name.
            Text(
                modifier = Modifier
                    .background(
                        MaterialTheme.colors.background.copy(
                            alpha = LocalContentAlpha.current.minus(
                                0.35f
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(SPACING_TINY.dp)
                    .basicMarquee()
                    .align(Alignment.BottomCenter),
                text = character.name,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}