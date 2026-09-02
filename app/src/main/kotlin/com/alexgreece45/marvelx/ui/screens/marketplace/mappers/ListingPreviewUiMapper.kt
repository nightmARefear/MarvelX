package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingPreviewUi
import kotlinx.collections.immutable.ImmutableList

class ListingPreviewUiMapper(
    private val configPriceLogic: ConfigPriceLogic,
    private val appLogger: AppLogger
) {

    private val loggerTag = "ListingPreviewUiMapper"

    fun toUiList(listingPreviewsDomain: List<ListingPreviewDomain>): ImmutableList<ListingPreviewUi>? =
        listingPreviewsDomain.map { toUi(it) ?: return null }.toImmutableListOrEmpty()

    fun toUi(listingPreviewDomain: ListingPreviewDomain): ListingPreviewUi? =
        with(listingPreviewDomain) {
            try {
                ListingPreviewUi(
                    listingId = listingId,
                    title = title,
                    priceLabel = price.formatPrice(configPriceLogic.priceScale),
                    coverImageUrl = coverImageUrl,
                    favorite = favorite,
                    cart = cart
                )

            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `ListingPreviewDomain` to `ListingPreviewUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
}