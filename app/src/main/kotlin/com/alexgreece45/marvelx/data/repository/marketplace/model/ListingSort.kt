package com.alexgreece45.marvelx.data.repository.marketplace.model

sealed class ListingSort(open val direction: SortDirection) {
    data class ByPrice(override val direction: SortDirection) : ListingSort(direction)
    data class ByDatePosted(override val direction: SortDirection) : ListingSort(direction)
}

enum class SortDirection {
    ASCENDING,
    DESCENDING;
}