package com.alexgreece45.marvelx.core.network.model

internal enum class ListingStatus(val code: String) {
    Draft(code = "draft"),
    Active(code = "active"),
    Reserved(code = "reserved"),
    Sold(code = "sold");
}