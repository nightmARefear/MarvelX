package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class EventList(
    @SerializedName("available") val available: Int,
    @SerializedName("returned") val returned: Int, // The number of events returned in this collection (up to 20).
    @SerializedName("collectionURI") val collectionURI: String,
    @SerializedName("items") val items: Array<EventSummary>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventList

        if (available != other.available) return false
        if (returned != other.returned) return false
        if (collectionURI != other.collectionURI) return false
        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = available
        result = 31 * result + returned
        result = 31 * result + collectionURI.hashCode()
        result = 31 * result + items.contentHashCode()
        return result
    }
}

