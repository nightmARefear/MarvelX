package com.alexgreece45.marvelx.core.network.model.api.marketplace

import com.google.gson.annotations.SerializedName

data class ResponseMarketplace(
    @SerializedName("promo") val promo: ResponseMarketplacePromoSection?,
    @SerializedName("categories") val categories: List<ResponseMarketplaceCategorySection>
)