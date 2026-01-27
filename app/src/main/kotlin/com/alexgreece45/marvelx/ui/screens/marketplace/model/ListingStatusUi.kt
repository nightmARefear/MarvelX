package com.alexgreece45.marvelx.ui.screens.marketplace.model

enum class ListingStatusUi(val code: String, val label: String) {
    Active(code = "active", label = "Active"),
    Reserved(code = "reserved", label = "Reserved"),
    Sold(code = "sold", label = "Sold");
}
