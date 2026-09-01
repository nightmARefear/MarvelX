package com.alexgreece45.marvelx.ui.screens.cart.mappers

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.ui.screens.cart.model.CartItemUi
import com.alexgreece45.marvelx.core.common.extensions.formatPrice


class CartItemUiMapper(
    private val configPriceLogic: ConfigPriceLogic,
    private val appLogger: AppLogger
) {

    private val loggerTag = "CartItemUiMapper"

    fun toUiList(listingPreviewsDomain: List<ListingPreviewDomain>): List<CartItemUi>? =
        listingPreviewsDomain.map { toUi(it) ?: return null }

    fun toUi(listingPreviewDomain: ListingPreviewDomain): CartItemUi? =
        with(listingPreviewDomain) {
            try {
                CartItemUi(
                    listingId = listingId,
                    title = title,
                    price = price,
                    priceLabel = price.formatPrice(configPriceLogic.priceScale),
                    coverImageUrl = coverImageUrl,
                    favorite = favorite
                )

            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `ListingPreviewDomain` to `CartItemUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
}