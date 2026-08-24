package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model.CheckoutPaymentDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.extensions.toExpirationDate
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CheckoutPaymentDetailsViewModel(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
) :
    BaseViewModel<CheckoutPaymentDetailsContract.Event, CheckoutPaymentDetailsContract.State, CheckoutPaymentDetailsContract.Effect>() {

    override fun setInitialState(): CheckoutPaymentDetailsContract.State =
        CheckoutPaymentDetailsContract.State()

    override fun handleEvents(event: CheckoutPaymentDetailsContract.Event) {
        when (event) {
            CheckoutPaymentDetailsContract.Event.Init -> init()
            is CheckoutPaymentDetailsContract.Event.OnCardholderNameChanged -> onCardholderNameChanged(
                event.newCardholderName
            )

            is CheckoutPaymentDetailsContract.Event.OnCardNumberChanged -> onCardNumberChanged(event.newCardNumber)
            is CheckoutPaymentDetailsContract.Event.OnExpirationDateChanged -> onExpirationDateChanged(
                event.newExpirationDate
            )

            is CheckoutPaymentDetailsContract.Event.OnCvvChanged -> onCvvChanged(event.newCvv)
        }
    }

    // Event functions.

    private fun init() {
        setState {
            copy(
                isLoading = true
            )
        }

        if (currentState.isInitialized) {
            setEffect {
                CheckoutPaymentDetailsContract.Effect.SetStickyBottomButtonEnabled(true)
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
                isInitialized = true,
                cardNumberField = WizardInputFieldUi.Text(
                    textValidationRules = listOf(
                        Rule.ValidateNotBlank(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_card_number_blank)
                        ),
                        Rule.ValidateCardNumberValue(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_card_number_invalid)
                        )
                    )
                ),
                expirationDateField = WizardInputFieldUi.Text(
                    textValidationRules = listOf(
                        Rule.ValidateNotBlank(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_expiration_date_blank)
                        ),
                        Rule.ValidateCardNumberExpirationDate(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_expiration_date_invalid),
                            currentYearMonth = DateUtil.getYearMonth()
                        )
                    )
                ),
                cvvField = WizardInputFieldUi.Text(
                    textValidationRules = listOf(
                        Rule.ValidateNotBlank(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_cvv_blank)
                        ),
                        Rule.ValidateStringLength(
                            errorMessage = resourceProvider.getString(R.string.screen_checkout_payment_details_error_cvv_invalid_length),
                            length = 3
                        )
                    )
                ),
                expirationDatePlaceholder = DateUtil.getYearMonth().toExpirationDate(),
            )
        }
    }

    private fun onCardholderNameChanged(newCardholderName: String) {
        setState {
            copy(
                cardholderNameField = cardholderNameField.copy(
                    textValue = newCardholderName,
                    textValidationResult = formValidator.validateForm(Form(mapOf(cardholderNameField.textValidationRules to newCardholderName)))
                )
            )
        }
        validateAllFields()
    }

    private fun onCardNumberChanged(newCardNumber: String) {
        val filteredNewCardNumber = newCardNumber.take(16)
        setState {
            copy(
                cardNumberField = cardNumberField.copy(
                    textValue = filteredNewCardNumber,
                    textValidationResult = formValidator.validateForm(Form(mapOf(cardNumberField.textValidationRules to filteredNewCardNumber)))
                )
            )
        }
        validateAllFields()
    }

    private fun onExpirationDateChanged(newExpirationDate: String) {
        val filteredNewExpirationDate = newExpirationDate.take(4)
        setState {
            copy(
                expirationDateField = expirationDateField.copy(
                    textValue = filteredNewExpirationDate,
                    textValidationResult = formValidator.validateForm(Form(mapOf(expirationDateField.textValidationRules to filteredNewExpirationDate)))
                )
            )
        }
        validateAllFields()
    }

    private fun onCvvChanged(newCvv: String) {
        val filteredNewCvv = newCvv.take(3)
        setState {
            copy(
                cvvField = cvvField.copy(
                    textValue = filteredNewCvv,
                    textValidationResult = formValidator.validateForm(Form(mapOf(cvvField.textValidationRules to filteredNewCvv)))
                )
            )
        }
        validateAllFields()
    }

    // Helper functions.

    private fun validateAllFields() {
        val allFieldsValid = with(currentState) {
            cardholderNameField.isValid
                    && cardNumberField.isValid
                    && expirationDateField.isValid
                    && cvvField.isValid
        }
        setEffect {
            CheckoutPaymentDetailsContract.Effect.SetStickyBottomButtonEnabled(allFieldsValid)
        }
    }
}
