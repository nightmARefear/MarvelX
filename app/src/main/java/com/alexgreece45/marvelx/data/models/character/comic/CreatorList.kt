package com.alexgreece45.marvelx.data.models.character.comic

data class CreatorList(
    val available: Int,
    val returned: Int,
    val collectionURI: String,
    val items: Array<CreatorSummary>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatorList

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
