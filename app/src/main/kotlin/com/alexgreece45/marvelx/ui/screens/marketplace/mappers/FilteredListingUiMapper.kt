package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.FilteredListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi

class FilteredListingUiMapper(
    private val appLogger: AppLogger,
    private val configPriceLogic: ConfigPriceLogic,
    private val listingDetailsRepository: ListingDetailsRepository,
    private val listingCategoryRepository: ListingCategoryRepository,
) {

    private val loggerTag = "FilteredListingUiMapper"

    fun toUiList(listings: List<ListingDomain>): List<FilteredListingUi>? =
        listings.map { toUi(it) ?: return null }

    private fun toUi(listing: ListingDomain): FilteredListingUi? =
        with(listing) {
            try {
                val category =
                    listingCategoryRepository.listingCategories.first { category -> category.code == categoryCode }

                FilteredListingUi(
                    listingId = listingId,
                    category = EnumGeneric(
                        code = category.code,
                        label = category.label
                    ),
                    condition = listingDetailsRepository.listingConditions.first { it.code == conditionCode },
                    title = title,
                    price = BigDecimalUi(
                        value = price,
                        label = price.formatPrice(
                            scale = configPriceLogic.priceScale,
                            roundingMode = configPriceLogic.roundingMode
                        )
                    ),
                    coverImageUrl = coverImageUrl,
                    details = details,
                    finalSale = finalSale,
                    postedAt = postedAt.toLocalDate().let { postedAt ->
                        LocalDateUi(
                            value = postedAt,
                            label = postedAt.toCustomFormat(pattern = "dd/MM/YYYY")
                        )
                    },
                    favorite = ToggleStateUi(isChecked = favorite),
                    cart = ToggleStateUi(isChecked = cart)
                )

            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `ListingDomain` to `FilteredListingUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
}