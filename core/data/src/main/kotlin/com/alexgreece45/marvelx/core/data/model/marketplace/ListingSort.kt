package com.alexgreece45.marvelx.core.data.model.marketplace

enum class ListingSort(val key: String) {
    PRICE_ASCENDING(key = "price_ascending"),
    PRICE_DESCENDING(key = "price_descending"),
    DATE_POSTED_ASCENDING(key = "date_posted_ascending"),
    DATE_POSTED_DESCENDING(key = "date_posted_descending");
}