package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingOrderIdsDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi

/**
 * The UI representation of a listing.
 *
 * @param canEdit Whether this listing is editable.
 */
data class ListingUi(
    val listingId: String,
    val sellerUserId: UserId,
    val category: EnumGeneric,
    val subcategory: EnumGeneric,
    val condition: EnumGeneric,
    val status: ListingStatusUi,
    val title: String,
    val description: String,
    val price: BigDecimalUi,
    val coverImageUrl: String,
    val imageUrls: List<String>,
    val selectedImageIndex: Int = 0,
    val details: ListingDetailsDomain,
    val favorite: Boolean,
    val cart: Boolean,
    val postedAt: InstantUi,
    val updatedAt: InstantUi?,
    val canEdit: Boolean,
    val orderIds: ListingOrderIdsDomain?
) {

    val allImageUrls: List<String>
        get() = buildList {
            add(coverImageUrl)
            addAll(imageUrls)
        }
}