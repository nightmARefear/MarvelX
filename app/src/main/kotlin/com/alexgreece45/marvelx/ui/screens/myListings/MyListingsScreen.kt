package com.alexgreece45.marvelx.ui.screens.myListings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExtendedFabData
import com.alexgreece45.marvelx.ui.components.GenericLazyItemInfoScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.NormalFabData
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.chips.WrapChip
import com.alexgreece45.marvelx.ui.components.icons.ProcessingRequestIconLottie
import com.alexgreece45.marvelx.ui.components.isScrollingUp
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.components.modalBottomSheetContents.ModalBottomSheetContentItems
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItemIconData
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingStatusUi
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.showIfHidden
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun MyListingsScreen(
    onEvent: (MyListingsContract.Event) -> Unit,
    state: MyListingsContract.State,
    effect: SingleObserverFlow<MyListingsContract.Effect>,
    onNavigationEffect: (MyListingsContract.Effect.Navigation) -> Unit,
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val modalBottomSheetCoroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    ObserveUiEffect(flow = effect, tag = "MyListingsScreen") { uiEffect ->
        when (uiEffect) {
            is MyListingsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is MyListingsContract.Effect.ShowToastMessage -> context.showToast(uiEffect.message)
            MyListingsContract.Effect.BottomSheetEffects.Hide -> {
                modalBottomSheetCoroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }

            MyListingsContract.Effect.BottomSheetEffects.Show -> {
                modalBottomSheetCoroutineScope.launch {
                    modalBottomSheetState.showIfHidden()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(MyListingsContract.Event.Init)
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.canScrollForward }
            .drop(1)
            .collect {
                onEvent(MyListingsContract.Event.OnEndOfListScrolled)
            }
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            if (state.selectedListingData != null) {
                ModalBottomSheetContent(onBottomSheetEvent = onEvent)
            }
        },
        sheetState = modalBottomSheetState
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = ScreenNavigableAction.Backable(
                onBack = { onEvent(MyListingsContract.Event.OnBackClicked) }
            ),
            onBack = {
                onEvent(MyListingsContract.Event.OnBackClicked)
            },
            showToolbar = true,
            toolbarTitle = stringResource(R.string.screen_my_listings_header_title),
            fabData = if (state.error) null else ExtendedFabData(
                extended = lazyListState.isScrollingUp(),
                normalFabData = NormalFabData(
                    label = stringResource(R.string.screen_my_listings_fab_label),
                    iconResource = R.drawable.ic_edit,
                    onClick = { onEvent(MyListingsContract.Event.OnFabButtonClicked) }
                )
            )
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                if (state.error) {
                    item(key = "errorView") {
                        LazyItemContentErrorScreen(
                            errorMessage = stringResource(R.string.screen_my_listings_error_fetching_listings),
                            onRetryClick = {
                                onEvent(MyListingsContract.Event.OnErrorButtonClicked)
                            }
                        )
                    }
                    return@LazyColumn
                }

                if (state.allListings.isEmpty()) {
                    item(key = "noListingsView") {
                        GenericLazyItemInfoScreen(
                            title = stringResource(R.string.screen_my_listings_no_listings_found_title),
                            buttonLabel = stringResource(R.string.screen_my_listings_no_listings_found_button),
                            onButtonClicked = { onEvent(MyListingsContract.Event.OnCreateListingButtonClicked) }
                        )
                    }
                    return@LazyColumn
                }

                items(
                    items = state.allListings,
                    key = { it.listingId }
                ) { listing ->
                    MyListingCard(
                        title = listing.title,
                        coverImageUrl = listing.coverImageUrl,
                        statusUi = listing.status,
                        isProcessing = listing.isProcessing,
                        onClicked = {
                            onEvent(MyListingsContract.Event.ListingEvents.OnClicked(listing.listingId))
                        },
                        onMoreActionsButtonClicked = {
                            onEvent(
                                MyListingsContract.Event.ListingEvents.OnMoreActionsButtonClicked(
                                    listing
                                )
                            )
                        }
                    )
                }

                item(key = "additionalListingsLoadingView") {
                    AnimatedVisibility(
                        visible = state.isFetchingNewItems,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                                color = MaterialTheme.customColors.genericRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalBottomSheetContent(onBottomSheetEvent: (MyListingsContract.Event.BottomSheetEvents) -> Unit) {
    ModalBottomSheetContentItems(
        bottomSheetContentItems = listOf(
            BottomSheetContentItem(
                title = stringResource(R.string.screen_my_listings_bottom_sheet_item_edit),
                onClickEvent = MyListingsContract.Event.BottomSheetEvents.OnEditListingClicked,
                leadingIconData = BottomSheetContentItemIconData(
                    iconData = IconData(
                        iconResource = R.drawable.ic_edit,
                        contentDescription = R.string.screen_my_listings_content_description_edit_listing
                    )
                )
            ),
            BottomSheetContentItem(
                title = stringResource(R.string.screen_my_listings_bottom_sheet_item_delete),
                titleColor = MaterialTheme.customColors.genericRed,
                onClickEvent = MyListingsContract.Event.BottomSheetEvents.OnDeleteListingClicked,
                leadingIconData = BottomSheetContentItemIconData(
                    iconData = IconData(
                        iconResource = R.drawable.ic_delete,
                        contentDescription = R.string.screen_my_listings_content_description_delete_listing,
                        tint = MaterialTheme.customColors.genericRed
                    )
                )
            )
        ),
        onEventSent = onBottomSheetEvent
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyListingCard(
    title: String,
    coverImageUrl: String,
    statusUi: ListingStatusUi,
    isProcessing: Boolean,
    onClicked: () -> Unit,
    onMoreActionsButtonClicked: () -> Unit
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = {
            WrapChip(
                onClick = {},
                colors = ChipDefaults.chipColors(
                    backgroundColor = when (statusUi) {
                        ListingStatusUi.Draft -> MaterialTheme.customColors.chipBackgroundColors.listingDraft
                        ListingStatusUi.Active -> MaterialTheme.customColors.chipBackgroundColors.listingActive
                        ListingStatusUi.Reserved -> MaterialTheme.customColors.chipBackgroundColors.listingReserved
                        ListingStatusUi.Sold -> MaterialTheme.customColors.chipBackgroundColors.listingSold
                    }
                )
            ) {
                Text(
                    text = stringResource(statusUi.labelResId)
                )
            }
        },
        trailingIcon = {
            if (isProcessing) {
                ProcessingRequestIconLottie()
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.screen_my_listings_content_description_more_actions_listing),
                    modifier = Modifier.clickableNoRipple(onClick = onMoreActionsButtonClicked)
                )
            }
        },
        onClicked = onClicked
    )
}
