package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import java.math.BigDecimal
import java.time.Instant

/**
 * The UI representation of a listing.
 *
 * @param canEdit Whether this listing is editable.
 * @param isProcessing Whether this listing is processing -- i.e., a request has been made to the
 * server to delete it.
 */
data class ListingUi(
    val listingId: String,
    val sellerUserId: String,
    val category: ListingEnumGeneric,
    val subcategory: ListingEnumGeneric,
    val condition: ListingEnumGeneric,
    val status: ListingStatusUi,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val priceLabel: String,
    val allImageUrls: List<String>,
    val selectedImageIndex: Int = 0,
    val details: ListingDetailsDomain,
    val favorite: Boolean,
    val postedAt: Instant,
    val postedAtLabel: String,
    val updatedAt: Instant?,
    val updatedAtLabel: String,
    val canEdit: Boolean,
    val isProcessing: Boolean = false
)
