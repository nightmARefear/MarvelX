package com.alexgreece45.marvelx.ui.screens.favoriteListings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.WrapTextButton
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.FavoriteListingUi
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.FavoriteListingsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

/**
 * Height of the image tile. Kept identical to the marketplace search grid so the two feeds line
 * up visually when the user moves between them.
 */
private val LISTING_IMAGE_HEIGHT = 180.dp
private val CART_BUTTON_HEIGHT = 32.dp
private val CART_BUTTON_BACKGROUND = Color(0xD821F33D)
private val CART_ICON_TINT = Color(0xFF386641)

@Composable
internal fun FavoriteListingsScreen(
    onEvent: (FavoriteListingsContract.Event) -> Unit,
    state: FavoriteListingsContract.State,
    effect: SingleObserverFlow<FavoriteListingsContract.Effect>,
    snackbarEffects: SingleObserverFlow<SnackbarEvent>,
    onNavigationEffect: (FavoriteListingsContract.Effect.Navigation) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    LaunchedEffect(Unit) {
        onEvent(FavoriteListingsContract.Event.Init)
    }

    ObserveUiEffect(flow = effect, tag = "FavoriteListingsScreen") { uiEffect ->
        when (uiEffect) {
            is FavoriteListingsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_favorite_listings_toolbar_title),
        snackbarEvents = snackbarEffects,
        bottomNavigationBarData = bottomNavigationBarData()
    ) { paddingValues ->
        when {
            state.hasError -> {
                GenericErrorScreen(
                    errorMessage = stringResource(R.string.screen_favorite_listings_error_loading),
                    modifier = Modifier.padding(paddingValues),
                    buttonTestTag = FavoriteListingsTestTags.ERROR_RETRY_BUTTON,
                    onErrorButtonClick = { onEvent(FavoriteListingsContract.Event.OnRetryClicked) }
                )
            }

            state.isEmpty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(SPACING_MEDIUM.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.screen_favorite_listings_empty_message),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    items(
                        items = state.listings,
                        key = { listing -> listing.listingId }
                    ) { listing ->
                        FavoriteListingGridItem(
                            listing = listing,
                            onListingClick = {
                                onEvent(
                                    FavoriteListingsContract.Event.OnListingClicked(listing.listingId)
                                )
                            },
                            onFavoriteClick = {
                                onEvent(
                                    FavoriteListingsContract.Event.OnFavoriteClicked(listing.listingId)
                                )
                            },
                            onCartClick = {
                                onEvent(
                                    FavoriteListingsContract.Event.OnCartClicked(listing.listingId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mirrors the marketplace search grid tile, minus the comparison control which only belongs to
 * the marketplace's compare flow.
 */
@Composable
private fun FavoriteListingGridItem(
    listing: FavoriteListingUi,
    onListingClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(FavoriteListingsTestTags.listingCard(listing.listingId))
            .clickable(onClick = onListingClick),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.customColors.white)
                .fillMaxWidth()
                .height(LISTING_IMAGE_HEIGHT),
            contentAlignment = Alignment.Center
        ) {
            ListingImage(
                imageUrl = listing.coverImageUrl,
                title = listing.title,
                modifier = Modifier
                    .padding(SPACING_MEDIUM.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .fillMaxSize()
            )

            FavoriteActionButton(
                isLoading = listing.favorite.isLoading,
                isChecked = listing.favorite.isChecked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .testTag(FavoriteListingsTestTags.listingFavoriteButton(listing.listingId)),
                onClick = onFavoriteClick
            )

            AnimatedContent(
                targetState = listing.cart.isChecked,
                modifier = Modifier.align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomEnd,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 300)) using
                            SizeTransform { _, _ -> tween(durationMillis = 500) }
                },
                label = "AddToCartAnimation"
            ) { isInCart ->
                if (isInCart) {
                    WrapTextButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CART_BUTTON_HEIGHT)
                            .testTag(FavoriteListingsTestTags.listingCartButton(listing.listingId)),
                        shape = RoundedCornerShape(
                            topStart = SPACING_MEDIUM.dp,
                            topEnd = SPACING_MEDIUM.dp
                        ),
                        colors = ButtonDefaults.textButtonColors(backgroundColor = CART_BUTTON_BACKGROUND)
                    ) {
                        if (listing.cart.isLoading) {
                            ActionLoadingIndicator(indicatorColor = MaterialTheme.customColors.white)
                        } else {
                            Text(text = stringResource(R.string.screen_marketplace_filtered_listing_remove_from_cart_button))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = SPACING_MEDIUM.dp))
                            .background(color = CART_BUTTON_BACKGROUND)
                            .testTag(FavoriteListingsTestTags.listingCartButton(listing.listingId))
                            .clickableNoRipple(onClick = onCartClick, role = Role.Button)
                            .height(CART_BUTTON_HEIGHT)
                            .padding(horizontal = SPACING_SMALL.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (listing.cart.isLoading) {
                            ActionLoadingIndicator(indicatorColor = MaterialTheme.customColors.white)
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_cart),
                                contentDescription = stringResource(R.string.screen_favorite_listings_content_description_add_to_cart),
                                modifier = Modifier.size(18.dp),
                                tint = CART_ICON_TINT
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            Text(
                text = listing.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            Text(
                text = listing.priceLabel,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun FavoriteActionButton(
    isLoading: Boolean,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = Color.Black.copy(alpha = 0.35f))
                .padding(SPACING_TINY.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                ActionLoadingIndicator(indicatorColor = MaterialTheme.customColors.white)
            } else {
                Icon(
                    painter = painterResource(
                        if (isChecked) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_empty
                    ),
                    contentDescription = stringResource(R.string.screen_favorite_listings_content_description_favorite),
                    tint = if (isChecked) {
                        MaterialTheme.customColors.genericRed
                    } else {
                        MaterialTheme.customColors.white
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionLoadingIndicator(indicatorColor: Color) {
    CircularProgressIndicator(
        modifier = Modifier.size(18.dp),
        color = indicatorColor,
        strokeWidth = 2.dp
    )
}
