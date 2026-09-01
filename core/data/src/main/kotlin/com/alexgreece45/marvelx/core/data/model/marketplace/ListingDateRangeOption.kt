package com.alexgreece45.marvelx.core.data.model.marketplace

enum class ListingDateRangeOption(val key: String) {
    ALL(key = "all"),
    LAST_24_HOURS(key = "last_24_hours"),
    LAST_7_DAYS(key = "last_7_days"),
    LAST_30_DAYS(key = "last_30_days"),
    CUSTOM(key = "custom");
}