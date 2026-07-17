package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model.CheckoutShippingDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.components.WizardTextInputField
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import kotlinx.coroutines.flow.Flow

@Composable
internal fun CheckoutShippingDetailsScreen(
    onEvent: (CheckoutShippingDetailsContract.Event) -> Unit,
    state: CheckoutShippingDetailsContract.State,
    effect: Flow<CheckoutShippingDetailsContract.Effect>,
    onNavigationEffect: (CheckoutShippingDetailsContract.Effect.Navigation) -> Unit,
    setStickyBottomButtonEnabled: (Boolean) -> Unit,
    showToastMessage: (String) -> Unit
) {

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CheckoutShippingDetailsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is CheckoutShippingDetailsContract.Effect.SetStickyBottomButtonEnabled -> setStickyBottomButtonEnabled(
                uiEffect.enabled
            )

            is CheckoutShippingDetailsContract.Effect.ShowToastMessage -> showToastMessage(uiEffect.message)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CheckoutShippingDetailsContract.Event.Init)
    }

    ContentScreen(
        isLoading = state.isLoading,
        isInnerScreen = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_first_name),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_first_name),
                    wizardInputField = state.firstName,
                    onValueChanged = {
                        onEvent(CheckoutShippingDetailsContract.Event.OnFirstNameChanged(it))
                    }
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_last_name),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_last_name),
                    wizardInputField = state.lastName,
                    onValueChanged = {
                        onEvent(CheckoutShippingDetailsContract.Event.OnLastNameChanged(it))
                    }
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_street_address),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_street_address),
                    wizardInputField = state.streetAddress,
                    onValueChanged = {
                        onEvent(CheckoutShippingDetailsContract.Event.OnStreetAddressChanged(it))
                    }
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_city),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_city),
                    wizardInputField = state.city,
                    onValueChanged = {
                        onEvent(CheckoutShippingDetailsContract.Event.OnCityChanged(it))
                    }
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_zip_code),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_zip_code),
                    wizardInputField = state.zipCode,
                    onValueChanged = {
                        onEvent(CheckoutShippingDetailsContract.Event.OnZipCodeChanged(it))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    )
                )

                ExposedDropdownMenu(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_floor_number),
                    expanded = state.floorNumber.isExpanded,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_floor_number),
                    value = state.floorNumber.value?.label,
                    onDismissRequest = {
                        onEvent(CheckoutShippingDetailsContract.Event.FloorNumber.OnDismissed)
                    },
                    onExpandedChange = {
                        onEvent(CheckoutShippingDetailsContract.Event.FloorNumber.OnClicked)
                    }
                ) {
                    state.floorNumbers.forEach { floorNumber ->
                        DropdownMenuItem(
                            onClick = {
                                onEvent(
                                    CheckoutShippingDetailsContract.Event.FloorNumber.OnItemSelected(
                                        floorNumber
                                    )
                                )
                            }
                        ) {
                            Text(text = floorNumber.label)
                        }
                    }
                }

                ExposedDropdownMenu(
                    label = stringResource(R.string.screen_checkout_shipping_details_label_location_type),
                    expanded = state.locationType.isExpanded,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(R.string.screen_checkout_shipping_details_placeholder_location_type),
                    value = state.locationType.value?.label,
                    onDismissRequest = {
                        onEvent(CheckoutShippingDetailsContract.Event.LocationType.OnDismissed)
                    },
                    onExpandedChange = {
                        onEvent(CheckoutShippingDetailsContract.Event.LocationType.OnClicked)
                    }
                ) {
                    state.locationTypes.forEach { locationType ->
                        DropdownMenuItem(
                            onClick = {
                                onEvent(
                                    CheckoutShippingDetailsContract.Event.LocationType.OnItemSelected(
                                        locationType
                                    )
                                )
                            }
                        ) {
                            Text(text = locationType.label)
                        }
                    }
                }
            }
        }
    }
}
