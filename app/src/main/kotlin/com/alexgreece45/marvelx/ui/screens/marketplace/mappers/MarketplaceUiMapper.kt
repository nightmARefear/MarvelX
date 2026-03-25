package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplacePromoSectionDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceCategorySectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplacePromoSectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceUi

class MarketplaceUiMapper(
    private val listingPreviewUiMapper: ListingPreviewUiMapper,
    private val appLogger: AppLogger
) {

    private val loggerTag = "MarketplaceUiMapper"

    fun toUi(marketplaceDomain: MarketplaceDomain): MarketplaceUi? {
        with(marketplaceDomain) {
            return try {
                MarketplaceUi(
                    promo = promo?.toUi(),
                    categories = categories.map { it.toUi() }
                )
            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `MarketplaceDomain` to `MarketplaceUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
    }

    private fun MarketplacePromoSectionDomain.toUi(): MarketplacePromoSectionUi =
        MarketplacePromoSectionUi(
            title = title,
            listings = listingPreviewUiMapper.toUiList(listings)
                ?: throw Exception("Error converting ListingDomain to UI.")
        )

    private fun MarketplaceCategorySectionDomain.toUi(): MarketplaceCategorySectionUi =
        MarketplaceCategorySectionUi(
            title = title,
            categoryCode = categoryCode,
            listings = listingPreviewUiMapper.toUiList(listings)
                ?: throw Exception("Error converting ListingDomain to UI.")
        )
}
