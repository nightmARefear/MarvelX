package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric

sealed interface ListingDetailsDomain {

    data class ApparelDetailsDomain(
        val size: EnumGeneric,
        val color: String
    ) : ListingDetailsDomain

    data class DrinkwareDetailsDomain(
        val material: EnumGeneric,
        val capacityMl: Int,
        val dishwasherSafe: Boolean
    ) : ListingDetailsDomain

    data class CollectibleDetailsDomain(
        val scale: EnumGeneric,
        val boxCondition: EnumGeneric
    ) : ListingDetailsDomain

    data class ArtDetailsDomain(
        val dimensions: ListingDimensions,
        val framed: Boolean
    ) : ListingDetailsDomain

    data class AccessoryDetailsDomain(
        val material: EnumGeneric,
        val color: String
    ) : ListingDetailsDomain

    data class HomeDetailsDomain(
        val dimensions: ListingDimensions,
    ) : ListingDetailsDomain

    data class ComicDetailsDomain(
        val grade: EnumGeneric,
        val publisher: String,
        val year: Int
    ) : ListingDetailsDomain

    data class CardDetailsDomain(
        val grade: EnumGeneric,
        val rarity: EnumGeneric,
        val variant: EnumGeneric
    ) : ListingDetailsDomain
}
