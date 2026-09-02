package com.alexgreece45.marvelx.core.network.model.api.order.item

import com.google.gson.annotations.SerializedName

data class ResponseReturnPolicies(
    @SerializedName("returnPolicies") val returnPolicies: List<ResponseReturnPolicy>
)

data class ResponseReturnPolicy(
    @SerializedName("header") val header: String,
    @SerializedName("description") val description: String,
    @SerializedName("items") val items: List<String>? = emptyList()
)