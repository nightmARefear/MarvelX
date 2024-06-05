package com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.ui.components.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ModalBottomSheet
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.SortingModalBottomSheetContent
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.coroutineScope
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllCharacterComicsScreen(
    navController: NavController,
    viewModel: AllCharacterComicsViewModel = koinViewModel(),
    characterId: Int,
    toolbarTitlePrefix: String
) {
    val state by viewModel.uiState.collectAsState()
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { it != ModalBottomSheetValue.Hidden },
            skipHalfExpanded = true
        )

    LaunchedEffect(Unit) {
        viewModel.setEvent(AllCharacterComicsEvent.Init(characterId = characterId))

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                AllCharacterComicsEffect.NavigateToCharacterScreen -> {
                    navController.popBackStack()
                }

                is AllCharacterComicsEffect.NavigateToComicScreen -> {
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = effect.comicId
                        )
                    ) {
                        launchSingleTop = true
                    }
                }

                AllCharacterComicsEffect.BottomSheet.Hide -> {
                    coroutineScope {
                        modalBottomSheetState.hide()
                    }
                }

                AllCharacterComicsEffect.BottomSheet.Show -> {
                    coroutineScope {
                        modalBottomSheetState.show()
                    }
                }
            }
        }
    }

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
                    viewModel.setEvent(AllCharacterComicsEvent.Sorting.OnItemClick(sortingType))
                },
                onCloseClick = {
                    viewModel.setEvent(AllCharacterComicsEvent.Sorting.OnCloseClick)
                }
            )
        }
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = ScreenNavigableAction.Backable {
                navController.popBackStack()
            },
            showToolbar = true,
            toolbarTitle = stringResource(
                R.string.all_character_comics_screen_toolbar_title,
                toolbarTitlePrefix
            )
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                state.error?.let {
                    item {
                        ContentErrorScreen(errorMessage = it.errorMessage) {
                            it.onRetryClick.invoke()
                        }
                    }
                }

                if (!state.hasInternetConnection) {
                    item {
                        ContentErrorScreen(errorMessage = stringResource(R.string.no_internet_connection_error_message)) {
                            viewModel.setEvent(AllCharacterComicsEvent.Init(characterId = characterId))
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.sort_by),
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.subtitle1
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Disables the on tap ripple effect for both single and long clicks. It also removes the background color.
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(radius = 0.dp)
                            ) {
                                viewModel.setEvent(AllCharacterComicsEvent.OnDropDownArrowClick)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.sortingType.label,
                            style = MaterialTheme.typography.h6
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = Icons.Filled.ArrowDropDown.name,
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                }

                item { VSpacer.Custom(height = 10) }

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

            HSpacer.Small()

            Text(
                text = comic.title,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}