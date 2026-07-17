package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.Rule

sealed interface CreateListingAdvancedDetailsContract {

    data class PriceField(
        val value: String = "",
        val validationResult: FormValidationResult = FormValidationResult(isValid = true),
        val validationRules: List<Rule> = emptyList()
    ) {

        val isValid: Boolean get() = value.isNotBlank() && validationResult.isValid
    }

    sealed interface Event : BaseEvent {

        data class Init(
            val createListingAdvancedDetailsScreenData: CreateListingAdvancedDetailsScreenData
        ) : Event

        data object OnBackButtonClicked : Event

        sealed interface ListingDetailsEvents : Event {
            sealed interface Apparel : ListingDetailsEvents {
                data object OnSizeDropDownClicked : Apparel
                data object OnSizeDropDownOnDismissed : Apparel
                data class OnSizeDropDownItemSelected(val newSize: EnumGeneric) : Apparel

                data class OnColorChanged(val newColor: String) : Apparel
            }

            sealed interface Drinkware : ListingDetailsEvents {
                data object OnMaterialDropDownClicked : Drinkware
                data object OnMaterialDropDownOnDismissed : Drinkware
                data class OnMaterialDropDownItemSelected(val newSize: EnumGeneric) :
                    Drinkware

                data class OnCapacityMlChanged(val newCapacity: String) : Drinkware
                data object OnDishwasherSafeCheckboxChanged : Drinkware
            }

            sealed interface Collectible : ListingDetailsEvents {
                data object OnScaleDropDownClicked : Collectible
                data object OnScaleDropDownOnDismissed : Collectible
                data class OnScaleDropDownItemSelected(val newScale: EnumGeneric) :
                    Collectible

                data object OnBoxConditionDropDownClicked : Collectible
                data object OnBoxConditionDropDownOnDismissed : Collectible
                data class OnBoxConditionDropDownItemSelected(val newBoxCondition: EnumGeneric) :
                    Collectible
            }

            sealed interface Art : ListingDetailsEvents {
                data class OnDimensionsWidthChanged(val width: String) : Art
                data class OnDimensionsHeightChanged(val height: String) : Art
                data object OnFramedCheckboxChanged : Art
            }

            sealed interface Accessory : ListingDetailsEvents {
                data object OnMaterialDropDownClicked : Accessory
                data object OnMaterialDropDownOnDismissed : Accessory
                data class OnMaterialDropDownItemSelected(val newMaterial: EnumGeneric) :
                    Accessory

                data class OnColorChanged(val newColor: String) : Accessory
            }

            sealed interface Home : ListingDetailsEvents {
                data class OnDimensionsWidthChanged(val width: String) : Home
                data class OnDimensionsHeightChanged(val height: String) : Home
            }

            sealed interface Comic : ListingDetailsEvents {
                data object OnGradeDropDownClicked : Comic
                data object OnGradeDropDownOnDismissed : Comic
                data class OnGradeDropDownItemSelected(val newGrade: EnumGeneric) : Comic

                data class OnPublisherChanged(val newPublisher: String) : Comic
                data class OnYearChanged(val newYear: String) : Comic
            }

            sealed interface CardDetails : ListingDetailsEvents {
                data object OnGradeDropDownClicked : CardDetails
                data object OnGradeDropDownOnDismissed : CardDetails
                data class OnGradeDropDownItemSelected(val newGrade: EnumGeneric) :
                    CardDetails

                data object OnRarityDropDownClicked : CardDetails
                data object OnRarityDropDownOnDismissed : CardDetails
                data class OnRarityDropDownItemSelected(val newRarity: EnumGeneric) :
                    CardDetails

                data object OnVariantDropDownClicked : CardDetails
                data object OnVariantDropDownOnDismissed : CardDetails
                data class OnVariantDropDownItemSelected(val newVariant: EnumGeneric) :
                    CardDetails
            }
        }

        sealed interface ConditionDropDownEvents : Event {
            data object OnClicked : ConditionDropDownEvents
            data object OnDismissed : ConditionDropDownEvents
            data class OnItemSelected(val newCondition: EnumGeneric) :
                ConditionDropDownEvents
        }

        data class OnPriceChanged(val newPrice: String) : Event
    }

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = true,
        val wizardMode: CreateListingWizardMode = CreateListingWizardMode.CREATE,
        val categoryCode: String = "",

        // Create listing data.
        val conditions: List<EnumGeneric> = emptyList(),

        // User input data.
        val listingDetails: ListingDetailsUi? = null,
        val condition: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val price: PriceField = PriceField(),
    ) : BaseState


    sealed interface Effect : BaseEffect {

        data object GoToPreviousStep : Effect
        data class SetNextButtonEnabled(val enabled: Boolean) : Effect
        data class ShowToast(val message: String) : Effect
    }
}
