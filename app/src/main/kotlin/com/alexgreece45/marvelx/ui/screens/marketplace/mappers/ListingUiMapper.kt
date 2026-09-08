package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toListingFormat
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingStatusDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.toUi
import com.alexgreece45.marvelx.util.DateUtil

// TODO: Revisit in future.
class ListingUiMapper(
    private val listingCategoryRepository: ListingCategoryRepository,
    private val listingDetailsRepository: ListingDetailsRepository,
    private val listingDetailsUiMapper: ListingDetailsUiMapper,
    private val appLogger: AppLogger,
    private val configPriceLogic: ConfigPriceLogic,
) {

    private val loggerTag = this::class.simpleName

    fun toUi(
        listings: List<ListingDomain>,
        currentUserId: String
    ): List<ListingUi>? {
        return listings.map { listing -> toUi(listing, currentUserId) ?: return null }
    }

    fun toUi(
        listing: ListingDomain,
        currentUserId: String
    ): ListingUi? {
        with(listing) {
            return try {
                val category =
                    listingCategoryRepository.listingCategories.first { category -> category.code == categoryCode }
                val subcategory =
                    category.subcategories.first { subcategory -> subcategory.categoryCode == subcategoryCode }

                ListingUi(
                    listingId = listingId,
                    sellerUserId = sellerUserId,
                    category = EnumGeneric(
                        code = category.code,
                        label = category.label
                    ),
                    subcategory = EnumGeneric(
                        code = subcategory.categoryCode,
                        label = subcategory.label
                    ),
                    condition = listingDetailsRepository.listingConditions.first { it.code == conditionCode },
                    status = status.toUi(),
                    title = title,
                    description = description,
                    price = BigDecimalUi(
                        value = price,
                        label = price.formatPrice(scale = configPriceLogic.priceScale)
                    ),
                    coverImageUrl = coverImageUrl,
                    imageUrls = imageUrls,
                    details = details,
                    postedAt = InstantUi(
                        value = postedAt,
                        label = postedAt.toListingFormat()
                    ),
                    updatedAt = updatedAt?.let {
                        InstantUi(
                            value = updatedAt,
                            label = updatedAt.toListingFormat()
                        )
                    },
                    favorite = favorite,
                    cart = cart,
                    canEdit = sellerUserId == UserId(currentUserId),
                    orderIds = orderIds
                )
            } catch (e: Exception) {
                appLogger.e(loggerTag) { "An error occurred whilst converting `ListingDomain` to `ListingUi`.\n" + e.localizedMessage.orEmpty() }
                null
            }
        }
    }

    fun toUi(
        createListingUi: CreateListingUi,
        currentUserId: String
    ): ListingUi? {
        return try {
            with(createListingUi) {
                toUi(
                    listing = ListingDomain(
                        listingId = "",
                        sellerUserId = UserId(currentUserId),
                        categoryCode = category?.code.orEmpty(),
                        subcategoryCode = subcategory?.categoryCode.orEmpty(),
                        conditionCode = condition.code,
                        status = ListingStatusDomain.Draft,
                        title = title,
                        description = description,
                        price = price,
                        coverImageUrl = attachedImages.first().uri.toString(),
                        imageUrls = attachedImages.drop(1).map { it.uri.toString() },
                        details = listingDetailsUiMapper.toDomain(listingDetails!!)!!,
                        finalSale = finalSale,
                        postedAt = DateUtil.getUTCInstant(),
                        updatedAt = null,
                        favorite = false,
                        cart = false,
                        orderIds = null
                    ),
                    currentUserId = currentUserId
                )
            }
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "An error occurred whilst converting `CreateListingUi` to `ListingUi`.\n" + e.localizedMessage.orEmpty() }
            null
        }
    }
}