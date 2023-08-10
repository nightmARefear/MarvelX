package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class EventList(
    @SerializedName("available") val available: Int,
    @SerializedName("returned") val returned: Int, // The number of events returned in this collection (up to 20).
    @SerializedName("collectionURI") val collectionURI: String,
    @SerializedName("items") val items: List<EventSummary>
)
