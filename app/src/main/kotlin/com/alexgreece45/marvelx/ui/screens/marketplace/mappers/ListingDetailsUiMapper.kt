package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingCategoryCode
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

class ListingDetailsUiMapper(
    private val listingDetailsRepository: ListingDetailsRepository,
    private val resourceProvider: ResourceProvider,
    private val appLogger: AppLogger,
) {

    private val loggerTag = "ListingDetailsUiMapper"

    fun toUi(listingDetails: ListingDetailsDomain): ListingDetailsUi =
        when (listingDetails) {
            is ListingDetailsDomain.ApparelDetailsDomain ->
                ListingDetailsUi.ApparelDetailsUi(
                    size = WizardInputFieldUi.Enum(
                        value = listingDetails.size
                    ),
                    color = WizardInputFieldUi.Text(
                        textValidationRules = getApparelColorValidationRules(),
                        textValue = listingDetails.color
                    )
                )

            is ListingDetailsDomain.DrinkwareDetailsDomain ->
                ListingDetailsUi.DrinkwareDetailsUi(
                    material = WizardInputFieldUi.Enum(
                        value = listingDetails.material
                    ),
                    capacityMl = WizardInputFieldUi.Integer(
                        integerValue = listingDetails.capacityMl.toString(),
                        integerValidationRules = getDrinkwareCapacityMlValidationRules()
                    ),
                    dishwasherSafe = listingDetails.dishwasherSafe
                )


            is ListingDetailsDomain.CollectibleDetailsDomain ->
                ListingDetailsUi.CollectibleDetailsUi(
                    scale = WizardInputFieldUi.Enum(
                        value = listingDetails.scale
                    ),
                    boxCondition = WizardInputFieldUi.Enum(
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
                    material = WizardInputFieldUi.Enum(
                        value = listingDetails.material
                    ),
                    color = WizardInputFieldUi.Text(
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
                    grade = WizardInputFieldUi.Enum(
                        value = listingDetails.grade
                    ),
                    publisher = WizardInputFieldUi.Text(
                        textValidationRules = getComicPublisherValidationRules(),
                        textValue = listingDetails.publisher
                    ),
                    year = WizardInputFieldUi.Integer(
                        integerValue = listingDetails.year.toString(),
                        integerValidationRules = getComicYearValidationRules()
                    )
                )

            is ListingDetailsDomain.CardDetailsDomain ->
                ListingDetailsUi.CardDetailsUi(
                    grade = WizardInputFieldUi.Enum(
                        value = listingDetails.grade
                    ),
                    rarity = WizardInputFieldUi.Enum(
                        value = listingDetails.rarity
                    ),
                    variant = WizardInputFieldUi.Enum(
                        value = listingDetails.variant
                    )
                )
        }

    fun toUi(categoryCode: String): ListingDetailsUi? =
        when (categoryCode) {
            ListingCategoryCode.APPAREL.code -> {
                ListingDetailsUi.ApparelDetailsUi(
                    color = WizardInputFieldUi.Text(textValidationRules = getApparelColorValidationRules()),
                    sizes = listingDetailsRepository.listingApparelSizes
                )
            }

            ListingCategoryCode.DRINKWARE.code -> {
                ListingDetailsUi.DrinkwareDetailsUi(
                    capacityMl = WizardInputFieldUi.Integer(integerValidationRules = getDrinkwareCapacityMlValidationRules()),
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
                    color = WizardInputFieldUi.Text(textValidationRules = getAccessoryColorValidationRules()),
                    materials = listingDetailsRepository.listingMaterials
                )
            }

            ListingCategoryCode.HOME.code -> {
                ListingDetailsUi.HomeDetailsUi()
            }

            ListingCategoryCode.COMICS.code -> {
                ListingDetailsUi.ComicDetailsUi(
                    publisher = WizardInputFieldUi.Text(textValidationRules = getComicPublisherValidationRules()),
                    year = WizardInputFieldUi.Integer(integerValidationRules = getComicYearValidationRules()),
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

    fun toDomain(listingDetailsUi: ListingDetailsUi): ListingDetailsDomain? =
        try {
            when (listingDetailsUi) {
                is ListingDetailsUi.ApparelDetailsUi -> ListingDetailsDomain.ApparelDetailsDomain(
                    size = listingDetailsUi.size.value!!,
                    color = listingDetailsUi.color.value
                )

                is ListingDetailsUi.DrinkwareDetailsUi -> ListingDetailsDomain.DrinkwareDetailsDomain(
                    material = listingDetailsUi.material.value!!,
                    capacityMl = listingDetailsUi.capacityMl.integerValue.toInt(),
                    dishwasherSafe = listingDetailsUi.dishwasherSafe
                )

                is ListingDetailsUi.CollectibleDetailsUi -> ListingDetailsDomain.CollectibleDetailsDomain(
                    scale = listingDetailsUi.scale.value!!,
                    boxCondition = listingDetailsUi.boxCondition.value!!
                )

                is ListingDetailsUi.ArtDetailsUi -> ListingDetailsDomain.ArtDetailsDomain(
                    dimensions = listingDetailsUi.dimensions,
                    framed = listingDetailsUi.framed
                )

                is ListingDetailsUi.AccessoryDetailsUi -> ListingDetailsDomain.AccessoryDetailsDomain(
                    material = listingDetailsUi.material.value!!,
                    color = listingDetailsUi.color.value
                )

                is ListingDetailsUi.HomeDetailsUi -> ListingDetailsDomain.HomeDetailsDomain(
                    dimensions = listingDetailsUi.dimensions
                )

                is ListingDetailsUi.ComicDetailsUi -> ListingDetailsDomain.ComicDetailsDomain(
                    grade = listingDetailsUi.grade.value!!,
                    publisher = listingDetailsUi.publisher.value,
                    year = listingDetailsUi.year.integerValue.toInt()
                )

                is ListingDetailsUi.CardDetailsUi -> ListingDetailsDomain.CardDetailsDomain(
                    grade = listingDetailsUi.grade.value!!,
                    rarity = listingDetailsUi.rarity.value!!,
                    variant = listingDetailsUi.variant.value!!
                )
            }
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "An error occurred whilst converting `ListingDetailsUi` to `ListingDetailsDomain`.\n" + e.localizedMessage.orEmpty() }
            null
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
                errorMessage = resourceProvider.getString(
                    R.string.screen_create_listing_advanced_details_error_generic_min_allowed_characters,
                    minimumAllowedCharacters
                )
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
                errorMessage = resourceProvider.getString(
                    R.string.screen_create_listing_advanced_details_error_below_min_year,
                    minYear
                )
            ),
            Rule.ValidateUnderMaxIntegerValue(
                maxValue = maxYear,
                errorMessage = resourceProvider.getString(
                    R.string.screen_create_listing_advanced_details_error_over_max_year,
                    maxYear
                )
            )
        )
    }
}
