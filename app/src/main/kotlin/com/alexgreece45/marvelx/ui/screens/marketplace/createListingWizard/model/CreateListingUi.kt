package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.CreateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import java.math.BigDecimal

data class CreateListingUi(
    val attachedImages: List<AttachedFileContentData> = emptyList(),
    val title: String = "",
    val description: String = "",
    val category: ListingCategory? = null,
    val subcategory: ListingSubcategory? = null,
    val listingDetails: ListingDetailsUi? = null,
    val condition: EnumGeneric = EnumGeneric("", ""),
    val price: BigDecimal = BigDecimal.ZERO
)

internal fun CreateListingUi.toDomain(
    listingDetailsDomain: ListingDetailsDomain
): CreateListingDomain =
    CreateListingDomain(
        coverImage = attachedImages.first(),
        additionalImages = attachedImages.drop(1),
        title = title,
        description = description,
        categoryId = category?.id ?: -1,
        subcategoryId = subcategory?.id ?: -1,
        listingDetailsDomain = listingDetailsDomain,
        conditionCode = condition.code,
        price = price
    )