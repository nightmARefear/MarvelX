package com.alexgreece45.marvelx.ui.screens.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericSearchBar
import com.alexgreece45.marvelx.ui.components.WrapHorizontalPager
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingPreviewUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import kotlinx.coroutines.flow.Flow

@Composable
fun MarketplaceScreen(
    onEvent: (MarketplaceContract.Event) -> Unit,
    state: MarketplaceContract.State,
    effect: Flow<MarketplaceContract.Effect>,
    onNavigation: (MarketplaceContract.Effect.Navigation) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
) {

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is MarketplaceContract.Effect.Navigation -> {
                onNavigation(uiEffect)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(MarketplaceContract.Event.Init)
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_marketplace_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(SPACING_LARGE.dp)
        ) {
            item(key = "searchBar") {
                GenericSearchBar(
                    searchTerm = state.searchBarTerm,
                    hint = stringResource(R.string.screen_marketplace_search_bar_hint),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onEvent(MarketplaceContract.Event.SearchBarEvents.OnSearchTermChanged(it))
                    },
                    onDeleteIconClicked = {
                        onEvent(MarketplaceContract.Event.SearchBarEvents.OnDeleteIconClicked)
                    }
                )
            }

            state.marketplaceUi?.let { marketplaceUi ->
                marketplaceUi.promo?.let { promo ->
                    item(key = "promoSection") {
                        PromoSection(
                            categoryTitle = promo.title,
                            listingsCount = promo.listings.size,
                            selectedListingIndex = promo.selectedListingIndex,
                            onListingClicked = {
                                onEvent(MarketplaceContract.Event.OnListingClicked(it))
                            },
                            onPageChanged = {
                                onEvent(MarketplaceContract.Event.OnPromoCarouselScrolled(it))
                            },
                            getListingByPage = { promo.listings.getOrNull(it) }
                        )
                    }
                }

                marketplaceUi.categories.forEach { categorySectionUi ->
                    item(key = "categorySection_" + categorySectionUi.categoryCode) {
                        ListingCategorySection(
                            categoryTitle = categorySectionUi.title,
                            listings = categorySectionUi.listings,
                            onCategoryClicked = {
                                onEvent(
                                    MarketplaceContract.Event.OnCategoryClicked(
                                        categorySectionUi.categoryCode
                                    )
                                )
                            },
                            onListingClicked = {
                                onEvent(MarketplaceContract.Event.OnListingClicked(it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoSection(
    categoryTitle: String,
    listingsCount: Int,
    selectedListingIndex: Int,
    onListingClicked: (String) -> Unit,
    onPageChanged: (Int) -> Unit,
    getListingByPage: (Int) -> ListingPreviewUi?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        Text(text = categoryTitle)

        WrapHorizontalPager(
            pageCount = listingsCount,
            selectedIndex = selectedListingIndex,
            onPageChanged = onPageChanged
        ) { page ->
            getListingByPage(page)?.let { currentListing ->
                ListingPreviewItem(
                    isPromo = true,
                    listingPreviewUi = currentListing,
                    onListingClicked = { onListingClicked(currentListing.listingId) }
                )
            }
        }
    }
}

@Composable
private fun ListingCategorySection(
    categoryTitle: String,
    listings: List<ListingPreviewUi>,
    onCategoryClicked: (() -> Unit)? = null,
    onListingClicked: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickableNoRipple(
                        enabled = onCategoryClicked != null,
                        onClick = onCategoryClicked ?: {}
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.body1
                )

                NavigateIcon()
            }

            ListingPreviewItems(listings, onListingClicked)
        }
    }
}

@Composable
private fun ListingPreviewItems(
    listings: List<ListingPreviewUi>,
    onListingClicked: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = listings,
            key = { it.listingId }
        ) { listing ->
            ListingPreviewItem(
                listingPreviewUi = listing,
                onListingClicked = { onListingClicked(listing.listingId) }
            )
        }
    }
}

@Composable
private fun ListingPreviewItem(
    isPromo: Boolean = false,
    listingPreviewUi: ListingPreviewUi,
    onListingClicked: () -> Unit
) {
    val columnModifier =
        if (isPromo) {
            Modifier.fillMaxWidth()
        } else {
            Modifier
        }

    val imageModifier =
        if (isPromo) {
            Modifier
                .fillMaxWidth()
                .height(150.dp)
        } else {
            Modifier.size(90.dp)
        }

    Column(
        modifier = columnModifier.then(Modifier.clickableNoRipple(onClick = onListingClicked)),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        ListingImage(
            imageUrl = listingPreviewUi.coverImageUrl,
            title = listingPreviewUi.title,
            modifier = imageModifier
        )

        Text(text = listingPreviewUi.title)

        Text(
            text = listingPreviewUi.priceLabel,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body2
        )
    }
}
