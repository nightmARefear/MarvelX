package com.alexgreece45.marvelx.network_logic.models_api.faq

import com.google.gson.annotations.SerializedName

data class ResponseFaq(
    @SerializedName("sections") val sections: List<ResponseFaqSection>
)

data class ResponseFaqSection(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("order") val order: String
)
