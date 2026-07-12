package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model.CheckoutPaymentDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.components.WizardTextInputField
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationCardNumber
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationExpirationDate
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_TINY
import kotlinx.coroutines.flow.Flow

@Composable
fun CheckoutPaymentDetailsScreen(
    onEvent: (CheckoutPaymentDetailsContract.Event) -> Unit,
    state: CheckoutPaymentDetailsContract.State,
    effect: Flow<CheckoutPaymentDetailsContract.Effect>,
    onNavigationEffect: (CheckoutPaymentDetailsContract.Effect.Navigation) -> Unit,
    setStickyBottomButtonEnabled: (Boolean) -> Unit,
    showToastMessage: (String) -> Unit
) {

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CheckoutPaymentDetailsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is CheckoutPaymentDetailsContract.Effect.SetStickyBottomButtonEnabled -> setStickyBottomButtonEnabled(
                uiEffect.enabled
            )

            is CheckoutPaymentDetailsContract.Effect.ShowToastMessage -> showToastMessage(uiEffect.message)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CheckoutPaymentDetailsContract.Event.Init)
    }

    ContentScreen(
        isLoading = state.isLoading,
        paddingValues = PaddingValues(0.dp)
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Input fields.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_payment_details_label_cardholder_name),
                    placeholder = stringResource(R.string.screen_checkout_payment_details_placeholder_cardholder_name),
                    wizardInputField = state.cardholderName,
                    onValueChanged = {
                        onEvent(
                            CheckoutPaymentDetailsContract.Event.OnCardholderNameChanged(
                                it
                            )
                        )
                    }
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_payment_details_label_card_number),
                    placeholder = stringResource(R.string.screen_checkout_payment_details_placeholder_card_number),
                    wizardInputField = state.cardNumber,
                    onValueChanged = {
                        onEvent(
                            CheckoutPaymentDetailsContract.Event.OnCardNumberChanged(
                                it
                            )
                        )
                    },
                    visualTransformation = VisualTransformationCardNumber(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
//                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WizardTextInputField(
                        label = stringResource(R.string.screen_checkout_payment_details_label_expiration_date),
                        placeholder = state.expirationDatePlaceholder,
                        modifier = Modifier.weight(1f),
                        wizardInputField = state.expirationDate,
                        onValueChanged = {
                            onEvent(
                                CheckoutPaymentDetailsContract.Event.OnExpirationDateChanged(
                                    it
                                )
                            )
                        },
                        visualTransformation = VisualTransformationExpirationDate(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        )
                    )

                    WizardTextInputField(
                        label = stringResource(R.string.screen_checkout_payment_details_label_cvv),
                        placeholder = stringResource(R.string.screen_checkout_payment_details_placeholder_CVV),
                        modifier = Modifier.weight(1f),
                        wizardInputField = state.cvv,
                        onValueChanged = {
                            onEvent(
                                CheckoutPaymentDetailsContract.Event.OnCvvChanged(
                                    it
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        )
                    )
                }
            }
        }
    }
}
