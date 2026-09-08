package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric

data class MarketplaceComparisonUi(
    val listings: List<FilteredListingUi> = emptyList(),
    val comparisonCategory: EnumGeneric? = null,
    val currentCarouselPageIndex: Int = 0,
) {

    val carouselPageCount: Int
        get() = listings.size.let { count ->
            if (count > 1) count - 1 else 1
        }

    val currentCarouselItemIndices: Pair<Int?, Int?>
        get() = when (val listingsCount = listings.size) {
            0 -> Pair(null, null)
            1 -> Pair(0, null)
            else -> when (currentCarouselPageIndex) {
                0 -> Pair(0, 1)
                else -> {
                    val firstIndex = currentCarouselPageIndex
                    val secondIndex = currentCarouselPageIndex + 1
                    Pair(
                        first = firstIndex,
                        second = if (secondIndex == listingsCount) null else secondIndex
                    )
                }
            }
        }
}