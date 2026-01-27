package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingStatusUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.util.extensions.formatPrice
import com.alexgreece45.marvelx.util.extensions.toListingFormat

// TODO: Revisit in future.
class ListingUiMapper(
    private val listingCategoryRepository: ListingCategoryRepository,
    private val listingDetailsRepository: ListingDetailsRepository,
    private val appLogger: AppLogger
) {

    private val loggerTag = "ListingUiMapper"

    fun toUi(listings: List<ListingDomain>, currentUserId: String): List<ListingUi>? {
        return listings.map { listing -> toUi(listing, currentUserId) ?: return null }
    }

    fun toUi(listing: ListingDomain, currentUserId: String): ListingUi? {
        with(listing) {
            return try {
                val category =
                    listingCategoryRepository.listingCategories.first { category -> category.code == categoryCode }
                val subcategory =
                    category.subcategories.first { subcategory -> subcategory.code == subcategoryCode }

                ListingUi(
                    listingId = listingId,
                    sellerUserId = sellerUserId,
                    category = ListingEnumGeneric(
                        code = category.code,
                        label = category.label
                    ),
                    subcategory = ListingEnumGeneric(
                        code = subcategory.code,
                        label = subcategory.label
                    ),
                    condition = listingDetailsRepository.listingConditions.first { it.code == conditionCode },
                    status = ListingStatusUi.entries.first { it.code == statusCode },
                    title = title,
                    description = description,
                    price = price,
                    priceLabel = price.formatPrice(),
                    allImageUrls = listOf(coverImageUrl) + imageUrls,
                    details = details,
                    favorite = favorite,
                    postedAt = postedAt,
                    postedAtLabel = postedAt.toListingFormat(),
                    updatedAt = updatedAt,
                    updatedAtLabel = updatedAt?.toListingFormat() ?: "-",
                    canEdit = sellerUserId == currentUserId
                )
            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `ListingDomain` to `ListingUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
    }
}
