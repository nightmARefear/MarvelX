package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.core.data.model.marketplace.listing.CreateListingData
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import java.math.BigDecimal

data class CreateListingDomain(
    val coverImage: AttachedFileContentData,
    val additionalImages: List<AttachedFileContentData>,
    val title: String,
    val description: String,
    val categoryId: Int,
    val subcategoryId: Int,
    val listingDetailsDomain: ListingDetailsDomain,
    val conditionCode: String,
    val price: BigDecimal,
    val finalSale: Boolean
)

internal fun CreateListingDomain.toData(): CreateListingData =
    CreateListingData(
        coverImage = coverImage,
        additionalImages = additionalImages,
        title = title,
        description = description,
        categoryCode = categoryId.toString(),
        subcategoryCode = subcategoryId.toString(),
        conditionCode = conditionCode,
        price = price,
        finalSale = finalSale
    )