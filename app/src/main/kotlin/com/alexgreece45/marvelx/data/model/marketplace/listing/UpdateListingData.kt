package com.alexgreece45.marvelx.data.model.marketplace.listing

data class UpdateListingData(
    val title: String?,
    val description: String?,
    val coverImageUrl: String?,
    val imageUrls: List<String>?
)
