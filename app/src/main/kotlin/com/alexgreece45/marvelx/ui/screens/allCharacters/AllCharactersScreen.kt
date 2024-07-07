package com.alexgreece45.marvelx.ui.screens.allCharacters

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExtendedFabData
import com.alexgreece45.marvelx.ui.components.NormalFabData
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.SearchBar
import com.alexgreece45.marvelx.ui.components.SortingModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.effect.DisposableEffectWithLifecycle
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.components.isScrollingUp
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.screens.allCharacters.uiModel.CharacterCardUi
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.extensions.getPendingDeepLinkAction
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllCharactersScreen(
    onEventSent: (AllCharactersEvent) -> Unit,
    state: AllCharactersState,
    effect: Flow<AllCharactersEffect>,
    navigateToCharacterScreen: (characterId: Int) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Hidden },
        skipHalfExpanded = true
    )
    val lazyGridState = rememberLazyGridState()

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    var initLock: Unit by remember { mutableStateOf(Unit) }

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OneTimeLaunchedEffect {
        onEventSent(AllCharactersEvent.Init(context.getPendingDeepLinkAction()))
    }

    LaunchedEffect(initLock) {
        if (!state.isFirstLaunch) {
            onEventSent(AllCharactersEvent.LoadFavoriteCharacters)
        }
    }

    DisposableEffectWithLifecycle(lifecycleEvent = Lifecycle.Event.ON_START) {
        // Hides the bottom sheet when the screen initializes. This allows the previously opened
        // bottom sheet to be hidden.
        coroutineScope.launch {
            modalBottomSheetState.hide()
        }
    }

    ObserveUiEffect(flow = effect) { uiEffect ->
        when (uiEffect) {
            is AllCharactersEffect.Navigation.NavigateToCharacterScreen -> {
                initLock = Unit
                navigateToCharacterScreen(uiEffect.characterId)
            }

            is AllCharactersEffect.FavoriteCharacter.Success -> {
                Toast.makeText(context, uiEffect.successMessage, Toast.LENGTH_SHORT).show()
            }

            is AllCharactersEffect.FavoriteCharacter.Error -> {
                Toast.makeText(context, uiEffect.errorMessage, Toast.LENGTH_SHORT).show()
            }

            is AllCharactersEffect.UnFavoriteCharacter.Error -> {
                Toast.makeText(context, uiEffect.errorMessage, Toast.LENGTH_SHORT).show()
            }

            is AllCharactersEffect.UnFavoriteCharacter.Success -> {
                Toast.makeText(context, uiEffect.successMessage, Toast.LENGTH_SHORT).show()
            }

            AllCharactersEffect.SortingBottomSheet.Hide -> {
                coroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }

            AllCharactersEffect.SortingBottomSheet.Show -> {
                coroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }

            AllCharactersEffect.CloseKeyboardAndRemoveFocus -> {
                keyboard?.hide()
                focusManager.clearFocus()
            }

            is AllCharactersEffect.Navigation.OpenDeepLinkAction -> {
                openDeepLinkAction(uiEffect.navController, uiEffect.deepLinkAction)
            }
        }
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            SortingModalBottomSheetContent(
                contentPadding = DEFAULT_SCREEN_PADDING,
                selectedItem = state.sortingType,
                onItemClick = { sortingType ->
                    onEventSent(AllCharactersEvent.Sorting.OnItemClick(sortingType))
                },
                onCloseClick = {
                    onEventSent(AllCharactersEvent.Sorting.OnCloseClick)
                }
            )
        },
        sheetState = modalBottomSheetState,
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            showToolbar = true,
            toolbarTitle = stringResource(R.string.all_characters_screen_toolbar_title),
            bottomNavigationBarData = bottomNavigationBarData(),
            fabData = if (state.error == null) { // Display FAB only when there's no error.
                ExtendedFabData(
                    extended = lazyGridState.isScrollingUp(),
                    normalFabData = NormalFabData(
                        label = stringResource(R.string.all_characters_screen_fab_label),
                        iconResource = R.drawable.ic_filter,
                        backgroundColor = null,
                        contentColor = null,
                        onClick = { onEventSent(AllCharactersEvent.OnFabClick) }
                    )
                )
            } else null
        ) { contentPadding ->
            state.error?.let { error ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding
                ) {
                    item {
                        ContentErrorScreen(
                            errorMessage = error.errorMessage,
                            onRetryClick = error.onRetryClick
                        )
                    }
                }
                return@ContentScreen
            }

            PullRefreshable(
                onRefresh = { onEventSent(AllCharactersEvent.OnRefresh) },
                isRefreshing = state.isLoading
            ) {
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
                                onEventSent(
                                    AllCharactersEvent.SearchBar.OnKeyboardType(
                                        searchTerm = newSearchTerm
                                    )
                                )
                            },
                            onCloseClick = {
                                onEventSent(AllCharactersEvent.SearchBar.OnCloseClick)
                            }
                        )
                    }

                    // If the search term does not match any character's name.
                    if (state.filteredCharacters.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = stringResource(R.string.all_characters_screen_error_no_characters_found_search),
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }

                    items(
                        items = state.filteredCharacters,
                        key = { characterCardUi ->
                            characterCardUi.id
                        }
                    ) { characterCardUi ->
                        CharacterItem(
                            character = characterCardUi,
                            context = context,
                            onClick = {
                                onEventSent(
                                    AllCharactersEvent.OnCharacterClick(characterId = characterCardUi.id)
                                )
                            },
                            onFavoriteClick = {
                                onEventSent(
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
    context: Context,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image.
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                model = ImageRequest.Builder(context)
                    .data(character.imageDownloadUrl)
                    .crossfade(true)
                    .fallback(R.drawable.ic_broken_image)
                    .build(),
                contentDescription = character.imageContentDescription,
                contentScale = ContentScale.FillBounds,
                error = painterResource(R.drawable.ic_broken_image)
            )

            // Favorite icon.
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                when (character.isFavorite) {
                    true -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite_filled),
                            contentDescription = stringResource(R.string.all_characters_screen_favorite_character_icon_description),
                            tint = MaterialTheme.colors.error
                        )
                    }

                    false -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite_empty),
                            contentDescription = stringResource(R.string.all_characters_screen_not_favorite_character_icon_description),
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }

            // Name.
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.6f))
                    .padding(vertical = SPACING_TINY.dp)
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