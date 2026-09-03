package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.domain.model_api.marketplace.FilteredListingDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.FilteredListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi

class FilteredListingUiMapper(
    private val appLogger: AppLogger,
    private val configPriceLogic: ConfigPriceLogic
) {

    private val loggerTag = "FilteredListingUiMapper"

    fun toUiList(filteredListings: List<FilteredListingDomain>): List<FilteredListingUi>? =
        filteredListings.map { toUi(it) ?: return null }

    private fun toUi(filteredListing: FilteredListingDomain): FilteredListingUi? =
        with(filteredListing) {
            try {
                FilteredListingUi(
                    listingId = listingId,
                    category = category,
                    condition = condition,
                    title = title,
                    price = BigDecimalUi(
                        value = price,
                        label = price.formatPrice(
                            scale = configPriceLogic.priceScale,
                            roundingMode = configPriceLogic.roundingMode
                        )
                    ),
                    coverImageUrl = coverImageUrl,
                    postedAt = postedAt.toLocalDate().let { postedAt ->
                        LocalDateUi(
                            value = postedAt,
                            label = postedAt.toCustomFormat(pattern = "dd/MM/YYYY")
                        )
                    },
                    isFinalSale = isFinalSale,
                    favorite = ToggleStateUi(isChecked = favorite),
                    cart = ToggleStateUi(isChecked = cart)
                )

            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `FilteredListingDomain` to `FilteredListingUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
}