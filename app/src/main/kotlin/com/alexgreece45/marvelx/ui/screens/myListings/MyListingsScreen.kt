package com.alexgreece45.marvelx.ui.screens.myListings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExtendedFabData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.LottieComposable
import com.alexgreece45.marvelx.ui.components.NormalFabData
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.isScrollingUp
import com.alexgreece45.marvelx.ui.components.modalBottomSheetContents.ModalBottomSheetContentItems
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItemIconData
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.showIfHidden
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MyListingsScreen(
    onEvent: (MyListingsContract.Event) -> Unit,
    state: MyListingsContract.State,
    effect: Flow<MyListingsContract.Effect>,
    onNavigationEffect: (MyListingsContract.Effect.Navigation) -> Unit,
) {

    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val modalBottomSheetCoroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    ObserveUiEffect(effect) { uiEffect ->
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
        snapshotFlow {
            lazyListState.canScrollForward
        }.collect {
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
            fabData = ExtendedFabData(
                extended = lazyListState.isScrollingUp(),
                normalFabData = NormalFabData(
                    label = stringResource(R.string.screen_my_listings_fab_label),
                    iconResource = R.drawable.ic_edit,
                    onClick = { onEvent(MyListingsContract.Event.OnFabButtonClicked) }
                )
            )
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = lazyListState
            ) {
                items(
                    items = state.allListings,
                    key = { it.listingId }
                ) { listing ->
                    ListingCard(
                        listingUi = listing,
                        onClicked = {
                            onEvent(
                                MyListingsContract.Event.ListingEvents.OnClicked(
                                    listing.listingId
                                )
                            )
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

@Composable
private fun ListingCard(
    listingUi: ListingUi,
    onClicked: () -> Unit,
    onMoreActionsButtonClicked: () -> Unit
) {
    SectionHeader(
        title = listingUi.title,
        leadingIcon = {
            ListingImage(
                imageUrl = listingUi.allImageUrls.first(),
                title = listingUi.title,
                modifier = Modifier.size(90.dp)
            )
        },
        trailingIcon = {
            if (listingUi.isProcessing) {
                LottieComposable(
                    resourceId = R.raw.lottie_processing_request,
                    size = ConstantsUi.MIN_ICON_SIZE
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.screen_my_listings_content_description_more_actions_listing),
                    modifier = Modifier.clickableNoRipple(onClick = onMoreActionsButtonClicked)
                )
            }
        },
        onClick = onClicked
    )
}
