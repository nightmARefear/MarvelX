package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import java.math.BigDecimal

data class CreateListingDomain(
    val coverImage: ReadFileContentData,
    val allImages: List<ReadFileContentData>,
    val title: String,
    val description: String,
    val categoryId: Int,
    val subcategoryId: Int,
    val listingDetailsDomain: ListingDetailsDomain,
    val conditionCode: String,
    val price: BigDecimal
)
