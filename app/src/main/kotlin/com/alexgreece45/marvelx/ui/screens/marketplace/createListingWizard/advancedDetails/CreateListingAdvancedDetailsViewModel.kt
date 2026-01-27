package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsScreenData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingDetailsUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

class CreateListingAdvancedDetailsViewModel(
    private val listingDetailsUiMapper: ListingDetailsUiMapper,
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val listingDetailsRepository: ListingDetailsRepository,
) :
    BaseViewModel<CreateListingAdvancedDetailsContract.Event, CreateListingAdvancedDetailsContract.State, CreateListingAdvancedDetailsContract.Effect>() {
    override fun setInitialState(): CreateListingAdvancedDetailsContract.State =
        CreateListingAdvancedDetailsContract.State()

    override fun handleEvents(event: CreateListingAdvancedDetailsContract.Event) {
        when (event) {
            is CreateListingAdvancedDetailsContract.Event.Init -> init(
                createListingAdvancedDetailsScreenData = event.createListingAdvancedDetailsScreenData
            )

            CreateListingAdvancedDetailsContract.Event.OnBackButtonClicked -> onBackButtonClicked()

            CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnClicked -> onConditionDropDownClicked()
            CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnDismissed -> onConditionDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnItemSelected -> onConditionDropDownItemSelected(
                event.newCondition
            )

            is CreateListingAdvancedDetailsContract.Event.OnPriceChanged -> {
                onPriceChanged(event.newPrice)
            }

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents -> {
                handleListingDetailsEvents(event)
            }
        }
    }

    private fun init(createListingAdvancedDetailsScreenData: CreateListingAdvancedDetailsScreenData) {
        if (currentState.isInitialized) {
            validateAllFields()
            return
        }

        when (createListingAdvancedDetailsScreenData) {
            is CreateListingAdvancedDetailsScreenData.Create -> {
                if (createListingAdvancedDetailsScreenData.categoryCode == null) {
                    showErrorAndGoToPreviousStep()
                    return
                }

                val scale = 2
                val minPrice = "0.99".toBigDecimalOrNull()
                val maxPrice = "9999.99".toBigDecimalOrNull()
                if (minPrice == null || maxPrice == null) {
                    showErrorAndGoToPreviousStep()
                    return
                }

                setState {
                    copy(
                        conditions = listingDetailsRepository.listingConditions,
                        price = CreateListingAdvancedDetailsContract.PriceField(
                            validationRules = listOf(
                                Rule.ValidateBigDecimal(
                                    scale = scale,
                                    errorMessage = resourceProvider.getString(
                                        R.string.screen_create_listing_advanced_details_error_invalid_price
                                    )
                                ),
                                Rule.ValidateOverMinimumBigDecimalValue(
                                    scale = scale,
                                    minValue = minPrice,
                                    errorMessage = resourceProvider.getString(
                                        R.string.screen_create_listing_advanced_details_error_below_min_price,
                                        minPrice
                                    )
                                ),
                                Rule.ValidateUnderMaximumBigDecimalValue(
                                    scale = scale,
                                    maxValue = maxPrice,
                                    errorMessage = resourceProvider.getString(
                                        R.string.screen_create_listing_advanced_details_error_over_max_price,
                                        maxPrice
                                    )
                                )
                            )
                        )
                    )
                }

                val listingDetails =
                    listingDetailsUiMapper.toUi(createListingAdvancedDetailsScreenData.categoryCode)
                if (listingDetails == null) {
                    showErrorAndGoToPreviousStep()
                } else {
                    setState {
                        copy(
                            isInitialized = true,
                            isLoading = false,
                            listingDetails = listingDetails
                        )
                    }
                }
            }

            is CreateListingAdvancedDetailsScreenData.Update -> {
                val listingDetails =
                    listingDetailsUiMapper.toUi(createListingAdvancedDetailsScreenData.listingDetails)

                val stringifiedPrice = createListingAdvancedDetailsScreenData.price.toPlainString()
                setState {
                    copy(
                        isInitialized = true,
                        isLoading = false,
                        wizardMode = CreateListingWizardMode.UPDATE,
                        listingDetails = listingDetails,
                        condition = condition.copy(value = createListingAdvancedDetailsScreenData.condition),
                        price = price.copy(
                            value = stringifiedPrice,
                            validationResult = formValidator.validateForm(Form(mapOf(price.validationRules to stringifiedPrice)))
                        )
                    )
                }

                // Enable button since all fields are valid by default.
                setEffect {
                    CreateListingAdvancedDetailsContract.Effect.SetNextButtonEnabled(true)
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        setEffect {
            CreateListingAdvancedDetailsContract.Effect.GoToPreviousStep
        }
    }

    private fun onConditionDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                condition = condition.copy(
                    isExpanded = !condition.isExpanded
                )
            )
        }
    }

    private fun onConditionDropDownDismissed() {
        setState {
            copy(
                condition = condition.copy(
                    isExpanded = false
                )
            )
        }
    }

    private fun onConditionDropDownItemSelected(newCondition: ListingEnumGeneric) {
        setState {
            copy(
                condition = condition.copy(
                    value = newCondition,
                    isExpanded = false
                )
            )
        }
        validateAllFields()
    }

    private fun onPriceChanged(newPrice: String) {
        setState {
            copy(
                price = price.copy(
                    value = newPrice,
                    validationResult = formValidator.validateForm(Form(mapOf(price.validationRules to newPrice)))
                )
            )
        }
        validateAllFields()
    }

    private fun handleListingDetailsEvents(event: CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents) {
        when (event) {
            // Apparel
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownClicked -> onApparelSizeDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownOnDismissed -> onApparelSizeDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownItemSelected -> onApparelSizeDropDownItemSelected(
                event.newSize
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnColorChanged -> onApparelColorChanged(
                event.newColor
            )

            // Drinkware
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownClicked -> onDrinkwareMaterialDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownOnDismissed -> onDrinkwareMaterialDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownItemSelected -> onDrinkwareMaterialDropDownItemSelected(
                event.newSize
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnCapacityMlChanged -> onDrinkwareCapacityChanged(
                event.newCapacity
            )

            CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnDishwasherSafeCheckboxChanged -> onDrinkwareDishwasherSafeChanged()

            // Collectible
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownClicked -> onCollectibleScaleDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownOnDismissed -> onCollectibleScaleDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownItemSelected -> onCollectibleScaleDropDownItemSelected(
                event.newScale
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownClicked -> onCollectibleBoxConditionDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownOnDismissed -> onCollectibleBoxConditionDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownItemSelected -> onCollectibleBoxConditionDropDownItemSelected(
                event.newBoxCondition
            )

            // Art
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnDimensionsWidthChanged -> onArtDimensionsWidthChanged(
                event.width
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnDimensionsHeightChanged -> onArtDimensionsHeightChanged(
                event.height
            )

            CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnFramedCheckboxChanged -> onArtFramedChanged()

            // Accessory
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownClicked -> onAccessoryMaterialDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownOnDismissed -> onAccessoryMaterialDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownItemSelected -> onAccessoryMaterialDropDownItemSelected(
                event.newMaterial
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnColorChanged -> onAccessoryColorChanged(
                event.newColor
            )

            // Home
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Home.OnDimensionsWidthChanged -> onHomeDimensionsWidthChanged(
                event.width
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Home.OnDimensionsHeightChanged -> onHomeDimensionsHeightChanged(
                event.height
            )

            // Comic
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownClicked -> onComicGradeDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownOnDismissed -> onComicGradeDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownItemSelected -> onComicGradeDropDownItemSelected(
                event.newGrade
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnPublisherChanged -> onComicPublisherChanged(
                event.newPublisher
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnYearChanged -> onComicYearChanged(
                event.newYear
            )

            // CardDetails
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownClicked -> onCardDetailsGradeDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownOnDismissed -> onCardDetailsGradeDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownItemSelected -> onCardDetailsGradeDropDownItemSelected(
                event.newGrade
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownClicked -> onCardDetailsRarityDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownOnDismissed -> onCardDetailsRarityDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownItemSelected -> onCardDetailsRarityDropDownItemSelected(
                event.newRarity
            )

            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownClicked -> onCardDetailsVariantDropDownClicked()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownOnDismissed -> onCardDetailsVariantDropDownDismissed()
            is CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownItemSelected -> onCardDetailsVariantDropDownItemSelected(
                event.newVariant
            )
        }

        validateAllFields()
    }

    private fun onApparelSizeDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ApparelDetailsUi) listingDetails.copy(
                    size = listingDetails.size.copy(
                        isExpanded = !listingDetails.size.isExpanded
                    )
                ) else listingDetails
            )
        }
    }

    private fun onApparelSizeDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ApparelDetailsUi) listingDetails.copy(
                    size = listingDetails.size.copy(
                        isExpanded = false
                    )
                ) else listingDetails
            )
        }
    }

    private fun onApparelSizeDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ApparelDetailsUi) listingDetails.copy(
                    size = listingDetails.size.copy(
                        value = item,
                        isExpanded = false
                    )
                ) else listingDetails
            )
        }
    }

    private fun onApparelColorChanged(newColor: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ApparelDetailsUi)
                    with(listingDetails) {
                        copy(
                            color = color.copy(
                                textValue = newColor,
                                textValidationResult = formValidator.validateForm(Form(mapOf(color.textValidationRules to newColor)))
                            ),
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onDrinkwareMaterialDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.DrinkwareDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                isExpanded = !material.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onDrinkwareMaterialDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.DrinkwareDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onDrinkwareMaterialDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.DrinkwareDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onDrinkwareCapacityChanged(newCapacityMl: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.DrinkwareDetailsUi)
                    with(listingDetails) {
                        copy(
                            capacityMl = capacityMl.copy(
                                integerValue = newCapacityMl,
                                integerValidationResult = formValidator.validateForm(
                                    Form(
                                        mapOf(
                                            capacityMl.integerValidationRules to newCapacityMl
                                        )
                                    )
                                )
                            ),
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onDrinkwareDishwasherSafeChanged() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.DrinkwareDetailsUi)
                    listingDetails.copy(dishwasherSafe = !listingDetails.dishwasherSafe)
                else listingDetails
            )
        }
    }

    private fun onCollectibleScaleDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            scale = scale.copy(
                                isExpanded = !scale.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCollectibleScaleDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            scale = scale.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCollectibleScaleDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            scale = scale.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCollectibleBoxConditionDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            boxCondition = boxCondition.copy(
                                isExpanded = !boxCondition.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCollectibleBoxConditionDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            boxCondition = boxCondition.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCollectibleBoxConditionDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CollectibleDetailsUi)
                    with(listingDetails) {
                        copy(
                            boxCondition = boxCondition.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onArtDimensionsWidthChanged(width: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ArtDetailsUi)
                    with(listingDetails) {
                        copy(
                            dimensions = dimensions.copy(
                                width = dimensions.width.copy(
                                    integerValue = width,
                                    integerValidationResult = formValidator.validateForm(
                                        Form(
                                            mapOf(
                                                dimensions.width.validationRules to width
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onArtDimensionsHeightChanged(height: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ArtDetailsUi)
                    with(listingDetails) {
                        copy(
                            dimensions = dimensions.copy(
                                height = dimensions.height.copy(
                                    integerValue = height,
                                    integerValidationResult = formValidator.validateForm(
                                        Form(
                                            mapOf(
                                                dimensions.height.validationRules to height
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onArtFramedChanged() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ArtDetailsUi)
                    with(listingDetails) {
                        copy(
                            framed = !framed
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onAccessoryMaterialDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.AccessoryDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                isExpanded = !material.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onAccessoryMaterialDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.AccessoryDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onAccessoryMaterialDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.AccessoryDetailsUi)
                    with(listingDetails) {
                        copy(
                            material = material.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onAccessoryColorChanged(newColor: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.AccessoryDetailsUi)
                    with(listingDetails) {
                        copy(
                            color = color.copy(
                                textValue = newColor,
                                textValidationResult = formValidator.validateForm(Form(mapOf(color.textValidationRules to newColor)))
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onHomeDimensionsWidthChanged(width: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.HomeDetailsUi)
                    with(listingDetails) {
                        copy(
                            dimensions = dimensions.copy(
                                width = dimensions.width.copy(
                                    integerValue = width,
                                    integerValidationResult = formValidator.validateForm(
                                        Form(
                                            mapOf(
                                                dimensions.width.validationRules to width
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onHomeDimensionsHeightChanged(height: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.HomeDetailsUi)
                    with(listingDetails) {
                        copy(
                            dimensions = dimensions.copy(
                                height = dimensions.height.copy(
                                    integerValue = height,
                                    integerValidationResult = formValidator.validateForm(
                                        Form(
                                            mapOf(
                                                dimensions.height.validationRules to height
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onComicGradeDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ComicDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(isExpanded = !grade.isExpanded)
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onComicGradeDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ComicDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(isExpanded = false)
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onComicGradeDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ComicDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onComicPublisherChanged(newPublisher: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ComicDetailsUi)
                    with(listingDetails) {
                        copy(
                            publisher = publisher.copy(
                                textValue = newPublisher,
                                textValidationResult = formValidator.validateForm(
                                    Form(
                                        mapOf(
                                            publisher.textValidationRules to newPublisher
                                        )
                                    )
                                )
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onComicYearChanged(newYear: String) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.ComicDetailsUi)
                    with(listingDetails) {
                        copy(
                            year = year.copy(
                                integerValue = newYear,
                                integerValidationResult = formValidator.validateForm(Form(mapOf(year.integerValidationRules to newYear)))
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    // Card Details :: Grade

    private fun onCardDetailsGradeDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(
                                isExpanded = !grade.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsGradeDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsGradeDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            grade = grade.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    // Card Details :: Rarity

    private fun onCardDetailsRarityDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            rarity = rarity.copy(
                                isExpanded = !rarity.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsRarityDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            rarity = rarity.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsRarityDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            rarity = rarity.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    // Card Details :: Variant

    private fun onCardDetailsVariantDropDownClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            variant = variant.copy(
                                isExpanded = !variant.isExpanded
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsVariantDropDownDismissed() {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            variant = variant.copy(
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    private fun onCardDetailsVariantDropDownItemSelected(item: ListingEnumGeneric) {
        setState {
            copy(
                listingDetails = if (listingDetails is ListingDetailsUi.CardDetailsUi)
                    with(listingDetails) {
                        copy(
                            variant = variant.copy(
                                value = item,
                                isExpanded = false
                            )
                        )
                    }
                else listingDetails
            )
        }
    }

    /**
     * Helper functions
     */
    private fun showErrorAndGoToPreviousStep(errorMessage: String = resourceProvider.getGenericErrorMessage()) {
        setEffect {
            CreateListingAdvancedDetailsContract.Effect.ShowToast(
                message = errorMessage
            )
        }
        setEffect {
            CreateListingAdvancedDetailsContract.Effect.GoToPreviousStep
        }
    }

    private fun validateAllFields() {
        val shouldEnableNextButton = with(currentState) {
            listingDetails?.isValid == true && condition.isValid && price.isValid
        }
        setEffect {
            CreateListingAdvancedDetailsContract.Effect.SetNextButtonEnabled(shouldEnableNextButton)
        }
    }
}
