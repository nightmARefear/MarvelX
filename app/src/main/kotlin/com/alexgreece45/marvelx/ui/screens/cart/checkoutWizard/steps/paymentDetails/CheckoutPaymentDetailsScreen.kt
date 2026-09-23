package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model.CheckoutPaymentDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.components.WizardTextInputField
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationCardNumber
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationExpirationDate
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL

@Composable
internal fun CheckoutPaymentDetailsScreen(
    onEvent: (CheckoutPaymentDetailsContract.Event) -> Unit,
    state: CheckoutPaymentDetailsContract.State,
    effect: SingleObserverFlow<CheckoutPaymentDetailsContract.Effect>,
    onNavigationEffect: (CheckoutPaymentDetailsContract.Effect.Navigation) -> Unit,
    setStickyBottomButtonEnabled: (Boolean) -> Unit,
    showToastMessage: (String) -> Unit
) {

    ObserveUiEffect(flow = effect, tag = "CheckoutPaymentDetailsScreen") { uiEffect ->
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
        isInnerScreen = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // Input fields.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                WizardTextInputField(
                    label = stringResource(R.string.screen_checkout_payment_details_label_cardholder_name),
                    placeholder = stringResource(R.string.screen_checkout_payment_details_placeholder_cardholder_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CheckoutPaymentDetailsTestTags.CARDHOLDER_NAME_TEXT_FIELD),
                    wizardInputField = state.cardholderNameField,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CheckoutPaymentDetailsTestTags.CARD_NUMBER_TEXT_FIELD),
                    wizardInputField = state.cardNumberField,
                    trailingIcon = {
                        with(state.cardBrand) {
                            Image(
                                painter = painterResource(iconResId),
                                contentDescription = stringResource(contentDescriptionResId),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    },
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
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    WizardTextInputField(
                        label = stringResource(R.string.screen_checkout_payment_details_label_expiration_date),
                        placeholder = state.expirationDatePlaceholder,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CheckoutPaymentDetailsTestTags.EXPIRATION_DATE_TEXT_FIELD),
                        wizardInputField = state.expirationDateField,
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CheckoutPaymentDetailsTestTags.CVV_TEXT_FIELD),
                        wizardInputField = state.cvvField,
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