package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import java.math.BigDecimal

sealed interface CreateListingAdvancedDetailsScreenData {

    data class Create(val categoryCode: String?) : CreateListingAdvancedDetailsScreenData

    data class Update(
        val listingDetails: ListingDetailsDomain,
        val condition: EnumGeneric,
        val price: BigDecimal
    ) : CreateListingAdvancedDetailsScreenData
}
