package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi

data class FilteredListingUi(
    val listingId: String,
    val category: EnumGeneric,
    val condition: EnumGeneric,
    val title: String,
    val price: BigDecimalUi,
    val coverImageUrl: String,
    val postedAt: LocalDateUi,
    val isFinalSale: Boolean,
    val favorite: ToggleStateUi,
    val cart: ToggleStateUi,

    val comparison: Boolean = false
)