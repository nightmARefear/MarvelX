package com.alexgreece45.marvelx.data.model.marketplace.listing

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingEnumGeneric

/**
 * A generic model for listing enum-related values.
 *
 * @param code The unique identifier of this value.
 * @param label The value to display.
 * @param sortOrder The optional sort order if the enum is sortable.
 * @param description The optional description of this value.
 */
data class ListingEnumGeneric(
    val code: String,
    val label: String,
    val sortOrder: Int? = null,
    val description: String? = null
)

fun ResponseListingEnumGeneric.toData(): ListingEnumGeneric =
    ListingEnumGeneric(
        code = code,
        label = label,
        sortOrder = sortOrder?.toInt(),
        description = description
    )
