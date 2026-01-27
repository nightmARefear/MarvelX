package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategoryCode
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

class ListingDetailsUiMapper(
    private val listingDetailsRepository: ListingDetailsRepository,
    private val resourceProvider: ResourceProvider
) {

    fun toUi(listingDetails: ListingDetailsDomain): ListingDetailsUi =
        when (listingDetails) {
            is ListingDetailsDomain.ApparelDetailsDomain ->
                ListingDetailsUi.ApparelDetailsUi(
                    size = CreateListingFieldUi.Enum(
                        value = listingDetails.size
                    ),
                    color = CreateListingFieldUi.Text(
                        textValidationRules = getApparelColorValidationRules(),
                        textValue = listingDetails.color
                    )
                )

            is ListingDetailsDomain.DrinkwareDetailsDomain ->
                ListingDetailsUi.DrinkwareDetailsUi(
                    material = CreateListingFieldUi.Enum(
                        value = listingDetails.material
                    ),
                    capacityMl = CreateListingFieldUi.Integer(
                        integerValue = listingDetails.capacityMl.toString(),
                        integerValidationRules = getDrinkwareCapacityMlValidationRules()
                    ),
                    dishwasherSafe = listingDetails.dishwasherSafe
                )


            is ListingDetailsDomain.CollectibleDetailsDomain ->
                ListingDetailsUi.CollectibleDetailsUi(
                    scale = CreateListingFieldUi.Enum(
                        value = listingDetails.scale
                    ),
                    boxCondition = CreateListingFieldUi.Enum(
                        value = listingDetails.boxCondition
                    )
                )

            is ListingDetailsDomain.ArtDetailsDomain ->
                ListingDetailsUi.ArtDetailsUi(
                    dimensions = listingDetails.dimensions,
                    framed = listingDetails.framed
                )

            is ListingDetailsDomain.AccessoryDetailsDomain ->
                ListingDetailsUi.AccessoryDetailsUi(
                    material = CreateListingFieldUi.Enum(
                        value = listingDetails.material
                    ),
                    color = CreateListingFieldUi.Text(
                        textValidationRules = getAccessoryColorValidationRules(),
                        textValue = listingDetails.color
                    )
                )

            is ListingDetailsDomain.HomeDetailsDomain ->
                ListingDetailsUi.HomeDetailsUi(
                    dimensions = listingDetails.dimensions
                )

            is ListingDetailsDomain.ComicDetailsDomain ->
                ListingDetailsUi.ComicDetailsUi(
                    grade = CreateListingFieldUi.Enum(
                        value = listingDetails.grade
                    ),
                    publisher = CreateListingFieldUi.Text(
                        textValidationRules = getComicPublisherValidationRules(),
                        textValue = listingDetails.publisher
                    ),
                    year = CreateListingFieldUi.Integer(
                        integerValue = listingDetails.year.toString(),
                        integerValidationRules = getComicYearValidationRules()
                    )
                )

            is ListingDetailsDomain.CardDetailsDomain ->
                ListingDetailsUi.CardDetailsUi(
                    grade = CreateListingFieldUi.Enum(
                        value = listingDetails.grade
                    ),
                    rarity = CreateListingFieldUi.Enum(
                        value = listingDetails.rarity
                    ),
                    variant = CreateListingFieldUi.Enum(
                        value = listingDetails.variant
                    )
                )
        }

    fun toUi(categoryCode: String): ListingDetailsUi? =
        when (categoryCode) {
            ListingCategoryCode.APPAREL.code -> {
                ListingDetailsUi.ApparelDetailsUi(
                    color = CreateListingFieldUi.Text(textValidationRules = getApparelColorValidationRules()),
                    sizes = listingDetailsRepository.listingApparelSizes
                )
            }

            ListingCategoryCode.DRINKWARE.code -> {
                ListingDetailsUi.DrinkwareDetailsUi(
                    capacityMl = CreateListingFieldUi.Integer(integerValidationRules = getDrinkwareCapacityMlValidationRules()),
                    materials = listingDetailsRepository.listingMaterials
                )
            }

            ListingCategoryCode.COLLECTIBLES.code -> {
                ListingDetailsUi.CollectibleDetailsUi(
                    scales = listingDetailsRepository.listingCollectibleScales,
                    boxConditions = listingDetailsRepository.listingConditions
                )
            }

            ListingCategoryCode.ART.code -> {
                ListingDetailsUi.ArtDetailsUi()
            }

            ListingCategoryCode.ACCESSORIES.code -> {
                ListingDetailsUi.AccessoryDetailsUi(
                    color=CreateListingFieldUi.Text(textValidationRules = getAccessoryColorValidationRules()),
                    materials = listingDetailsRepository.listingMaterials
                )
            }

            ListingCategoryCode.HOME.code -> {
                ListingDetailsUi.HomeDetailsUi()
            }

            ListingCategoryCode.COMICS.code -> {
                ListingDetailsUi.ComicDetailsUi(
                    publisher = CreateListingFieldUi.Text(textValidationRules = getComicPublisherValidationRules()),
                    year = CreateListingFieldUi.Integer(integerValidationRules = getComicYearValidationRules()),
                    grades = listingDetailsRepository.listingGrades
                )
            }

            ListingCategoryCode.CARDS.code -> {
                ListingDetailsUi.CardDetailsUi(
                    grades = listingDetailsRepository.listingGrades,
                    rarities = listingDetailsRepository.listingCardRarities,
                    variants = listingDetailsRepository.listingCardVariants,
                )
            }

            else -> null
        }

    private fun getApparelColorValidationRules(): List<Rule> {
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_blank_color))
        )
    }

    private fun getDrinkwareCapacityMlValidationRules(): List<Rule> {
        val minMlCapacity = 200
        val maxMlCapacity = 2000

        return listOf(
            Rule.ValidateOverMinIntegerValue(
                minValue = minMlCapacity,
                errorMessage = resourceProvider.getString(
                    R.string.screen_create_listing_advanced_details_error_below_minimum_ml_capacity,
                    minMlCapacity
                )
            ),
            Rule.ValidateUnderMaxIntegerValue(
                maxValue = maxMlCapacity,
                errorMessage = resourceProvider.getString(
                    R.string.screen_create_listing_advanced_details_error_over_maximum_ml_capacity,
                    maxMlCapacity
                )
            )
        )
    }

    private fun getAccessoryColorValidationRules(): List<Rule> {
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_blank_color))
        )
    }

    private fun getComicPublisherValidationRules(): List<Rule> {
        val minimumAllowedCharacters = 3
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_blank_publisher)),
            Rule.ValidateStringMinLength(
                length = minimumAllowedCharacters,
                errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_generic_min_allowed_characters, minimumAllowedCharacters)
            )
        )
    }

    private fun getComicYearValidationRules(): List<Rule> {
        val minYear = 1939
        val maxYear = DateUtil.getCurrentLocalDate().year

        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_blank_year)),
            Rule.ValidateIntegerValue(
                errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_invalid_year)
            ),
            Rule.ValidateOverMinIntegerValue(
                minValue = minYear,
                errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_below_min_year, minYear)
            ),
            Rule.ValidateUnderMaxIntegerValue(
                maxValue = maxYear,
                errorMessage = resourceProvider.getString(R.string.screen_create_listing_advanced_details_error_over_max_year, maxYear)
            )
        )
    }
}
