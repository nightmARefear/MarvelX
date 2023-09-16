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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEffect
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEvent
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.util.BottomBarAction
import com.alexgreece45.marvelx.util.BottomNavigationBarData
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.ExtendedFabData
import com.alexgreece45.marvelx.util.GenericErrorScreen
import com.alexgreece45.marvelx.util.IconData
import com.alexgreece45.marvelx.util.LoadingScreen
import com.alexgreece45.marvelx.util.ModalBottomSheet
import com.alexgreece45.marvelx.util.NoInternetErrorScreen
import com.alexgreece45.marvelx.util.NormalFabData
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.SearchBar
import com.alexgreece45.marvelx.util.SortingModalBottomSheetContent
import kotlinx.coroutines.coroutineScope

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun AllCharactersScreen(
    navController: NavController,
    viewModel: AllCharactersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Hidden },
        skipHalfExpanded = true
    )

    val lazyStaggeredGridState = rememberLazyStaggeredGridState()

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
                is AllCharactersEffect.NavigateToCharacterScreen -> {
                    initLock = Unit
                    navController.navigate(
                        Screen.CharacterScreen.createNavRoute(
                            characterId = effect.characterId
                        )
                    )
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
                showToolbar = true,
                toolbarTitle = stringResource(R.string.all_characters_screen_toolbar_title),
                fabData = ExtendedFabData(
                    extended = /*lazyStaggeredGridState.isScrollingUp()*/true,
                    normalFabData = NormalFabData(
                        label = stringResource(R.string.all_characters_screen_fab_label),
                        iconResource = R.drawable.ic_filter_32,
                        backgroundColor = null,
                        contentColor = null,
                        onClick = { viewModel.setEvent(AllCharactersEvent.OnFabClick) }
                    )
                ),
                bottomNavigationBarData = BottomNavigationBarData(
                    navController = navController,
                    items = listOf(
                        BottomBarAction(
                            route = Screen.AllCharactersScreen.route,
                            label = stringResource(R.string.bottom_navigation_label_characters),
                            iconData = IconData(
                                iconResource = R.drawable.ic_close_32,
                                contentDescription = stringResource(R.string.bottom_navigation_label_characters),
                                colorTint = null
                            )
                        ),
                        BottomBarAction(
                            route = Screen.AllFavoriteCharactersScreen.route,
                            label = stringResource(R.string.bottom_navigation_label_favorites),
                            iconData = IconData(
                                iconResource = R.drawable.ic_favorite_filled_24,
                                contentDescription = stringResource(R.string.bottom_navigation_label_favorites),
                                colorTint = null
                            )
                        ),
                        BottomBarAction(
                            route = Screen.ProfileScreen.route,
                            label = stringResource(R.string.bottom_navigation_label_profile),
                            iconData = IconData(
                                iconResource = R.drawable.ic_person_32,
                                contentDescription = stringResource(R.string.bottom_navigation_label_profile),
                                colorTint = null
                            )
                        )
                    )
                )
            ) { contentPadding ->
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = lazyStaggeredGridState,
                    contentPadding = contentPadding,
                    verticalItemSpacing = SPACING_SMALL.dp,
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    state.errorMessage?.let { errorMessage ->
                        item(span = StaggeredGridItemSpan.FullLine) {
                            GenericErrorScreen(
                                errorMessage = errorMessage
                            )
                        }
                    }

                    if (!state.hasInternetConnection) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            NoInternetErrorScreen {
                                viewModel.setEvent(
                                    AllCharactersEvent.InitView
                                )
                            }
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
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
                        key = { characterDomain ->
                            characterDomain.id
                        }
                    ) { characterDomain ->
                        CharacterItem(
                            character = characterDomain,
                            onClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnCharacterClick(characterId = characterDomain.id)
                                )
                            },
                            onFavoriteClick = {
                                viewModel.setEvent(
                                    AllCharactersEvent.OnHeartClick(
                                        characterId = characterDomain.id
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