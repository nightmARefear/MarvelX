package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric

sealed interface ListingDetailsDomain {

    data class ApparelDetailsDomain(
        val size: ListingEnumGeneric,
        val color: String
    ) : ListingDetailsDomain

    data class DrinkwareDetailsDomain(
        val material: ListingEnumGeneric,
        val capacityMl: Int,
        val dishwasherSafe: Boolean
    ) : ListingDetailsDomain

    data class CollectibleDetailsDomain(
        val scale: ListingEnumGeneric,
        val boxCondition: ListingEnumGeneric
    ) : ListingDetailsDomain

    data class ArtDetailsDomain(
        val dimensions: ListingDimensions,
        val framed: Boolean
    ) : ListingDetailsDomain

    data class AccessoryDetailsDomain(
        val material: ListingEnumGeneric,
        val color: String
    ) : ListingDetailsDomain

    data class HomeDetailsDomain(
        val dimensions: ListingDimensions,
    ) : ListingDetailsDomain

    data class ComicDetailsDomain(
        val grade: ListingEnumGeneric,
        val publisher: String,
        val year: Int
    ) : ListingDetailsDomain

    data class CardDetailsDomain(
        val grade: ListingEnumGeneric,
        val rarity: ListingEnumGeneric,
        val variant: ListingEnumGeneric
    ) : ListingDetailsDomain
}
