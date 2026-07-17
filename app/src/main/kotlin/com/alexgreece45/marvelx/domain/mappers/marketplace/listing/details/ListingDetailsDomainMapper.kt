package com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.details.ResponseListingDetails
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDimensions
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

class ListingDetailsDomainMapper(
    private val listingDetailsRepository: ListingDetailsRepository
) {

    fun toDomain(responseListingDetails: ResponseListingDetails): ListingDetailsDomain {
        return when (responseListingDetails) {
            is ResponseListingDetails.ResponseListingApparelDetails ->
                ListingDetailsDomain.ApparelDetailsDomain(
                    size = listingDetailsRepository.listingApparelSizes.first { it.code == responseListingDetails.sizeCode },
                    color = responseListingDetails.color
                )

            is ResponseListingDetails.ResponseListingDrinkwareDetails ->
                ListingDetailsDomain.DrinkwareDetailsDomain(
                    material = listingDetailsRepository.listingMaterials.first { it.code == responseListingDetails.materialCode },
                    capacityMl = responseListingDetails.capacityMl,
                    dishwasherSafe = responseListingDetails.dishwasherSafe
                )

            is ResponseListingDetails.ResponseListingCollectibleDetails ->
                ListingDetailsDomain.CollectibleDetailsDomain(
                    scale = listingDetailsRepository.listingCollectibleScales.first { it.code == responseListingDetails.scaleCode },
                    boxCondition = listingDetailsRepository.listingConditions.first { it.code == responseListingDetails.boxConditionCode }
                )

            is ResponseListingDetails.ResponseListingArtDetails ->
                ListingDetailsDomain.ArtDetailsDomain(
                    dimensions = ListingDimensions(
                        width = WizardInputFieldUi.Integer(integerValue = responseListingDetails.width.toString()),
                        height = WizardInputFieldUi.Integer(integerValue = responseListingDetails.height.toString())
                    ),
                    framed = responseListingDetails.framed
                )

            is ResponseListingDetails.ResponseListingAccessoryDetails ->
                ListingDetailsDomain.AccessoryDetailsDomain(
                    material = listingDetailsRepository.listingMaterials.first { it.code == responseListingDetails.materialCode },
                    color = responseListingDetails.color
                )

            is ResponseListingDetails.ResponseListingHomeDetails ->
                ListingDetailsDomain.HomeDetailsDomain(
                    dimensions = ListingDimensions(
                        width = WizardInputFieldUi.Integer(integerValue = responseListingDetails.width.toString()),
                        height = WizardInputFieldUi.Integer(integerValue = responseListingDetails.height.toString())
                    )
                )

            is ResponseListingDetails.ResponseListingComicDetails ->
                ListingDetailsDomain.ComicDetailsDomain(
                    grade = listingDetailsRepository.listingGrades.first { it.code == responseListingDetails.gradeCode },
                    publisher = responseListingDetails.publisher,
                    year = responseListingDetails.year
                )

            is ResponseListingDetails.ResponseListingCardDetails ->
                ListingDetailsDomain.CardDetailsDomain(
                    grade = listingDetailsRepository.listingGrades.first { it.code == responseListingDetails.gradeCode },
                    rarity = listingDetailsRepository.listingCardRarities.first { it.code == responseListingDetails.rarityCode },
                    variant = listingDetailsRepository.listingCardVariants.first { it.code == responseListingDetails.variantCode }
                )
        }
    }
}
