package com.alexgreece45.marvelx.network_logic.models_api.marketplace

import com.google.gson.annotations.SerializedName

data class ResponseMarketplace(
    @SerializedName("promo") val promo: ResponseMarketplacePromoSection?,
    @SerializedName("categories") val categories: List<ResponseMarketplaceCategorySection>
)
