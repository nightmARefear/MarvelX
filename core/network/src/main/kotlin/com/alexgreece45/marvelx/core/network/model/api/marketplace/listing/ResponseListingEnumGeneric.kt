package com.alexgreece45.marvelx.core.network.model.api.marketplace.listing

import com.google.gson.annotations.SerializedName

/**
 * A generic response for listing enum-related values.
 *
 * @param code The unique identifier of this value.
 * @param label The value to display.
 * @param sortOrder The optional sort order if the enum is sortable.
 * @param description The optional description of this value.
 */
data class ResponseListingEnumGeneric(
    @SerializedName("code") val code: String,
    @SerializedName("label") val label: String,
    @SerializedName("sort_order") val sortOrder: String? = null,
    @SerializedName("description") val description: String? = null
)
