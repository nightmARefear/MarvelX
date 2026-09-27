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
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.SortingModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllCharacterComicsScreen(
    onEventSent: (AllCharacterComicsEvent) -> Unit,
    state: AllCharacterComicsState,
    effect: SingleObserverFlow<AllCharacterComicsEffect>,
    onNavigationEffect: (AllCharacterComicsEffect.Navigation) -> Unit,
    characterId: Int,
    toolbarTitlePrefix: String,
) {
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { it != ModalBottomSheetValue.Hidden },
            skipHalfExpanded = true
        )

    ObserveUiEffect(flow = effect, tag = "AllCharacterComicsScreen") { uiEffect ->
        when (uiEffect) {
            is AllCharacterComicsEffect.Navigation -> onNavigationEffect(uiEffect)
            AllCharacterComicsEffect.BottomSheet.Show -> {
                coroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }

            AllCharacterComicsEffect.BottomSheet.Hide -> {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onEventSent(AllCharacterComicsEvent.Init(characterId = characterId))
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            SortingModalBottomSheetContent(
                contentPadding = DEFAULT_SCREEN_PADDING,
                selectedItem = state.sortingType,
                onItemClick = { sortingType ->
                    onEventSent(AllCharacterComicsEvent.Sorting.OnItemClick(sortingType))
                },
                onCloseClick = {
                    onEventSent(AllCharacterComicsEvent.Sorting.OnCloseClick)
                }
            )
        },
        sheetState = modalBottomSheetState,
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = ScreenNavigableAction.Backable(testTag = AllCharacterComicsTestTags.BACK_BUTTON) {
                onEventSent(
                    AllCharacterComicsEvent.OnBackClick
                )
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
                        LazyItemContentErrorScreen(
                            errorMessage = it.errorMessage,
                            retryTestTag = AllCharacterComicsTestTags.ERROR_RETRY_BUTTON
                        ) {
                            it.onRetryClick()
                        }
                    }
                }

                if (!state.hasInternetConnection) {
                    item {
                        LazyItemContentErrorScreen(
                            errorMessage = stringResource(R.string.no_internet_connection_error_message),
                            retryTestTag = AllCharacterComicsTestTags.NO_INTERNET_RETRY_BUTTON
                        ) {
                            onEventSent(AllCharacterComicsEvent.Init(characterId = characterId))
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
                            .testTag(AllCharacterComicsTestTags.SORT_DROPDOWN)
                            // Disables the on tap ripple effect for both single and long clicks. It also removes the background color.
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    onEventSent(AllCharacterComicsEvent.OnDropDownArrowClick)
                                }
                            ),
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

                item { VSpacer.Custom(height = 10.dp) }

                itemsIndexed(
                    items = state.comics,
                    key = { _, comicDomain ->
                        comicDomain.id
                    }
                ) { index, comicDomain ->
                    ComicItem(
                        comic = comicDomain,
                        testTag = AllCharacterComicsTestTags.comicItem(comicDomain.id)
                    ) {
                        onEventSent(AllCharacterComicsEvent.OnComicClick(comicId = comicDomain.id))
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
    testTag: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(comic.thumbnail.downloadUrl)
                    .crossfade(true).build(),
                contentDescription = comic.thumbnail.contentDescription,
                modifier = Modifier
                    .width(85.dp)
                    .aspectRatio(ratio = 0.66f)
                    .clip(MaterialTheme.shapes.medium),
            )

            HSpacer.Small()

            Text(
                text = comic.title,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
