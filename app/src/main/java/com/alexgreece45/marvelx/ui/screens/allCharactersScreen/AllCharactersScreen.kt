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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.alexgreece45.marvelx.util.ExtendedFabData
import com.alexgreece45.marvelx.util.GenericErrorScreen
import com.alexgreece45.marvelx.util.LoadingScreen
import com.alexgreece45.marvelx.util.ModalBottomSheet
import com.alexgreece45.marvelx.util.NoInternetErrorScreen
import com.alexgreece45.marvelx.util.NormalFabData
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.SearchBar
import com.alexgreece45.marvelx.util.SortingModalBottomSheetContent
import com.alexgreece45.marvelx.util.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun AllCharacterScreen(
    navigator: DestinationsNavigator,
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

    val verticalScroll = rememberScrollState()
    var fabExtended by remember { mutableStateOf(true) }

    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(initLock) {
        if (!state.isFirstLaunch) {
            viewModel.setEvent(AllCharactersEvent.LoadFavoriteCharacters)
        }
    }

    LaunchedEffect(verticalScroll) {
        var prev = 0
        snapshotFlow { verticalScroll.value }.collect {
            fabExtended = it <= prev
            prev = it
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

    LoadingScreen(isLoading = state.isLoading) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(topStart = SPACING_SMALL.dp, topEnd = SPACING_SMALL.dp),
            scrimColor = MaterialTheme.colors.background.copy(
                alpha = LocalContentAlpha.current.minus(0.5f)
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
                showToolbar = true,
                toolbarTitle = stringResource(R.string.all_characters_screen_toolbar_title),
                fabData = ExtendedFabData(
                    extended = lazyGridState.isScrollingUp(),
                    normalFabData = NormalFabData(
                        label = stringResource(R.string.all_characters_screen_fab_label),
                        iconResource = R.drawable.ic_filter_32,
                        backgroundColor = null,
                        contentColor = null,
                        onClick = { viewModel.setEvent(AllCharactersEvent.OnFabClick) }
                    )
                )
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

                    item {
                        SearchBar(
                            searchTerm = state.searchBarTerm,
                            hint = stringResource(R.string.all_characters_screen_search_bar_hint),
                            backgroundColor = MaterialTheme.colors.surface,
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

                    if (state.characters.isNotEmpty()) {
                        item {
                            LazyVerticalGrid(
                                modifier = Modifier.fillParentMaxHeight(),
                                columns = GridCells.Fixed(2),
                                state = lazyGridState,
                                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                            ) {
                                items(
                                    state.filteredCharacters
                                ) { character ->
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CharacterItem(
    character: CharacterDomain,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                    .data(character.thumbnail.downloadUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${character.name} image",
                contentScale = ContentScale.Fit,
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