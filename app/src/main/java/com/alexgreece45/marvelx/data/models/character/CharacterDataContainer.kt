package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class CharacterDataContainer(
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: Array<CharacterResponse>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterDataContainer

        if (offset != other.offset) return false
        if (limit != other.limit) return false
        if (total != other.total) return false
        if (count != other.count) return false
        if (!results.contentEquals(other.results)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = offset
        result = 31 * result + limit
        result = 31 * result + total
        result = 31 * result + count
        result = 31 * result + results.contentHashCode()
        return result
    }
}