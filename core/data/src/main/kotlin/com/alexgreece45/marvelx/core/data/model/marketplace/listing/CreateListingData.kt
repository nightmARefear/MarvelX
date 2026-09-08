package com.alexgreece45.marvelx.core.data.model.marketplace.listing

import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import java.math.BigDecimal

data class CreateListingData(
    val coverImage: AttachedFileContentData,
    val additionalImages: List<AttachedFileContentData>,
    val title: String,
    val description: String,
    val categoryCode: String,
    val subcategoryCode: String,
    val conditionCode: String,
    val price: BigDecimal,
    val finalSale: Boolean
)