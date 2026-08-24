package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

enum class ListingStatusDomain(val code: String) {
    Draft(code = "draft"),
    Active(code = "active"),
    Reserved(code = "reserved"),
    Sold(code = "sold");
}