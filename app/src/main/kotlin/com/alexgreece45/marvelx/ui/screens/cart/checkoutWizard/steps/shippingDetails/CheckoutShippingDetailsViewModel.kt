package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model.CheckoutShippingDetailsContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model.ShippingLocationFloorNumberUi
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model.ShippingLocationTypeUi
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

class CheckoutShippingDetailsViewModel(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
) :
    BaseViewModel<CheckoutShippingDetailsContract.Event, CheckoutShippingDetailsContract.State, CheckoutShippingDetailsContract.Effect>() {

    override fun setInitialState(): CheckoutShippingDetailsContract.State =
        CheckoutShippingDetailsContract.State()

    override fun handleEvents(event: CheckoutShippingDetailsContract.Event) {
        when (event) {
            CheckoutShippingDetailsContract.Event.Init -> init()
            is CheckoutShippingDetailsContract.Event.OnFirstNameChanged -> onFirstNameChanged(event.newFirstName)
            is CheckoutShippingDetailsContract.Event.OnLastNameChanged -> onLastNameChanged(event.newLastName)
            is CheckoutShippingDetailsContract.Event.OnStreetAddressChanged -> onStreetAddressChanged(
                event.newStreetAddress
            )

            is CheckoutShippingDetailsContract.Event.OnCityChanged -> onCityChanged(event.newCity)
            is CheckoutShippingDetailsContract.Event.OnZipCodeChanged -> onZipCodeChanged(event.newZipCode)

            CheckoutShippingDetailsContract.Event.FloorNumber.OnDismissed -> onFloorNumberDropDownDismissed()
            CheckoutShippingDetailsContract.Event.FloorNumber.OnClicked -> onFloorNumberDropDownClicked()
            is CheckoutShippingDetailsContract.Event.FloorNumber.OnItemSelected -> onFloorNumberDropDownItemsSelected(
                event.floorNumber
            )

            CheckoutShippingDetailsContract.Event.LocationType.OnDismissed -> onLocationTypeDropDownDismissed()
            CheckoutShippingDetailsContract.Event.LocationType.OnClicked -> onLocationTypeDropDownClicked()
            is CheckoutShippingDetailsContract.Event.LocationType.OnItemSelected -> onLocationTypeDropDownItemSelected(
                event.locationType
            )
        }
    }

    /**
     * Event functions.
     */

    /**
     * Initializes the viewModel.
     */
    private fun init() {
        setState {
            copy(
                isLoading = true
            )
        }

        if (currentState.isInitialized) {
            setEffect {
                CheckoutShippingDetailsContract.Effect.SetStickyBottomButtonEnabled(true)
            }
            setState {
                copy(
                    isLoading = false
                )
            }
            return
        }

        setState {
            copy(
                isLoading = false,
                firstName = firstName.copy(
                    textValidationRules = getTextFieldValidationRules(
                        minCharacters = 2,
                        maxCharacters = 50
                    )
                ),
                lastName = lastName.copy(
                    textValidationRules = getTextFieldValidationRules(
                        minCharacters = 2,
                        maxCharacters = 50
                    )
                ),
                streetAddress = streetAddress.copy(
                    textValidationRules = getTextFieldValidationRules(
                        minCharacters = 3,
                        maxCharacters = 100
                    )
                ),
                city = city.copy(
                    textValidationRules = getTextFieldValidationRules(
                        minCharacters = 3,
                        maxCharacters = 100
                    )
                ),
                zipCode = zipCode.copy(
                    integerValidationRules = buildList {
                        add(
                            Rule.ValidateIntegerValue(
                                errorMessage = resourceProvider.getString(R.string.screen_checkout_shipping_details_error_input_zip_code_invalid)
                            )
                        )
                        addAll(
                            getTextFieldValidationRules(
                                minCharacters = 5,
                                maxCharacters = 6
                            )
                        )
                    }
                ),
                floorNumbers = ShippingLocationFloorNumberUi.entries.mapIndexed { index, shippingLocationFloorNumberUi ->
                    EnumGeneric(
                        code = index.toString(), // Only used here.
                        label = resourceProvider.getString(shippingLocationFloorNumberUi.labelResId)
                    )
                },
                locationTypes = ShippingLocationTypeUi.entries.map { shippingLocationType ->
                    EnumGeneric(
                        code = shippingLocationType.code,
                        label = resourceProvider.getString(shippingLocationType.labelResId)
                    )
                }
            )
        }
    }

    private fun onFirstNameChanged(newFirstName: String) {
        setState {
            copy(
                firstName = firstName.copy(
                    textValue = newFirstName,
                    textValidationResult = formValidator.validateForm(Form(mapOf(firstName.validationRules to newFirstName)))
                )
            )
        }
        validateAllFields()
    }

    private fun onLastNameChanged(newLastName: String) {
        setState {
            copy(
                lastName = lastName.copy(
                    textValue = newLastName,
                    textValidationResult = formValidator.validateForm(Form(mapOf(firstName.validationRules to newLastName)))
                )
            )
        }
        validateAllFields()
    }

    private fun onStreetAddressChanged(newStreetAddress: String) {
        setState {
            copy(
                streetAddress = streetAddress.copy(
                    textValue = newStreetAddress,
                    textValidationResult = formValidator.validateForm(Form(mapOf(streetAddress.validationRules to newStreetAddress)))
                )
            )
        }
        validateAllFields()
    }

    private fun onCityChanged(newCity: String) {
        setState {
            copy(
                city = city.copy(
                    textValue = newCity,
                    textValidationResult = formValidator.validateForm(Form(mapOf(city.validationRules to newCity)))
                )
            )
        }
        validateAllFields()
    }

    private fun onZipCodeChanged(newZipCode: String) {
        setState {
            copy(
                zipCode = zipCode.copy(
                    integerValue = newZipCode,
                    integerValidationResult = formValidator.validateForm(Form(mapOf(zipCode.validationRules to newZipCode)))
                )
            )
        }
        validateAllFields()
    }

    private fun onFloorNumberDropDownDismissed() {
        setState {
            copy(
                floorNumber = floorNumber.copy(
                    isExpanded = false
                )
            )
        }
    }

    private fun onFloorNumberDropDownClicked() {
        setState {
            copy(
                floorNumber = floorNumber.copy(
                    isExpanded = !floorNumber.isExpanded
                )
            )
        }
    }

    private fun onFloorNumberDropDownItemsSelected(newFloorNumber: EnumGeneric) {
        if (newFloorNumber.code == currentState.floorNumber.value?.code) {
            setState {
                copy(
                    floorNumber = floorNumber.copy(
                        isExpanded = false
                    )
                )
            }
            return
        }

        setState {
            copy(
                floorNumber = floorNumber.copy(
                    value = newFloorNumber,
                    isExpanded = false
                )
            )
        }
        validateAllFields()
    }

    private fun onLocationTypeDropDownDismissed() {
        setState {
            copy(
                locationType = locationType.copy(
                    isExpanded = false
                )
            )
        }
    }

    private fun onLocationTypeDropDownClicked() {
        setState {
            copy(
                locationType = locationType.copy(
                    isExpanded = !locationType.isExpanded
                )
            )
        }
    }

    private fun onLocationTypeDropDownItemSelected(newLocationType: EnumGeneric) {
        if (newLocationType.code == currentState.locationType.value?.code) {
            setState {
                copy(
                    locationType = locationType.copy(
                        isExpanded = false
                    )
                )
            }
            return
        }

        setState {
            copy(
                locationType = locationType.copy(
                    value = newLocationType,
                    isExpanded = false
                )
            )
        }
        validateAllFields()
    }

    /**
     * Helper functions.
     */

    /**
     * Returns the validation rules for all text fields.
     */
    private fun getTextFieldValidationRules(minCharacters: Int, maxCharacters: Int): List<Rule> {
        return listOf(
            Rule.ValidateNotBlank(
                errorMessage = resourceProvider.getString(R.string.screen_checkout_shipping_details_error_input_blank)
            ),
            Rule.ValidateStringMinLength(
                length = minCharacters,
                errorMessage = resourceProvider.getString(
                    R.string.screen_checkout_shipping_details_error_input_min_characters,
                    minCharacters
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxCharacters,
                errorMessage = resourceProvider.getString(
                    R.string.screen_checkout_shipping_details_error_input_max_characters,
                    maxCharacters
                )
            ),
        )
    }

    private fun validateAllFields() {
        val areAllFieldsValid = with(currentState) {
            firstName.isValid
                    && lastName.isValid
                    && streetAddress.isValid
                    && city.isValid
                    && zipCode.isValid
                    && floorNumber.isValid
                    && locationType.isValid
        }
        setEffect {
            CheckoutShippingDetailsContract.Effect.SetStickyBottomButtonEnabled(areAllFieldsValid)
        }
    }
}
