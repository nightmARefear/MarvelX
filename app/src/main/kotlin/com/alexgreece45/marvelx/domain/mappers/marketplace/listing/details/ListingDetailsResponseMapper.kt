package com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.details.ResponseListingDetails
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain

class ListingDetailsResponseMapper {

    fun toResponse(domainDetails: ListingDetailsDomain): ResponseListingDetails {
        return when (domainDetails) {
            is ListingDetailsDomain.ApparelDetailsDomain ->
                ResponseListingDetails.ResponseListingApparelDetails(
                    sizeCode = domainDetails.size.code,
                    color = domainDetails.color
                )

            is ListingDetailsDomain.DrinkwareDetailsDomain ->
                ResponseListingDetails.ResponseListingDrinkwareDetails(
                    materialCode = domainDetails.material.code,
                    capacityMl = domainDetails.capacityMl,
                    dishwasherSafe = domainDetails.dishwasherSafe
                )

            is ListingDetailsDomain.CollectibleDetailsDomain ->
                ResponseListingDetails.ResponseListingCollectibleDetails(
                    scaleCode = domainDetails.scale.code,
                    boxConditionCode = domainDetails.boxCondition.code
                )

            is ListingDetailsDomain.ArtDetailsDomain ->
                ResponseListingDetails.ResponseListingArtDetails(
                    width = domainDetails.dimensions.width.integerValue.toIntOrNull() ?: 0,
                    height = domainDetails.dimensions.height.integerValue.toIntOrNull() ?: 0,
                    framed = domainDetails.framed
                )

            is ListingDetailsDomain.AccessoryDetailsDomain ->
                ResponseListingDetails.ResponseListingAccessoryDetails(
                    materialCode = domainDetails.material.code,
                    color = domainDetails.color
                )

            is ListingDetailsDomain.HomeDetailsDomain ->
                ResponseListingDetails.ResponseListingHomeDetails(
                    width = domainDetails.dimensions.width.integerValue.toIntOrNull() ?: 0,
                    height = domainDetails.dimensions.height.integerValue.toIntOrNull() ?: 0
                )

            is ListingDetailsDomain.ComicDetailsDomain ->
                ResponseListingDetails.ResponseListingComicDetails(
                    gradeCode = domainDetails.grade.code,
                    publisher = domainDetails.publisher,
                    year = domainDetails.year
                )

            is ListingDetailsDomain.CardDetailsDomain ->
                ResponseListingDetails.ResponseListingCardDetails(
                    gradeCode = domainDetails.grade.code,
                    rarityCode = domainDetails.rarity.code,
                    variantCode = domainDetails.variant.code
                )
        }
    }
}