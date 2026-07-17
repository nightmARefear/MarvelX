package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success

import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model.CheckoutSuccessContract
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CheckoutSuccessViewModel :
    BaseViewModel<CheckoutSuccessContract.Event, CheckoutSuccessContract.State, CheckoutSuccessContract.Effect>() {

    override fun setInitialState(): CheckoutSuccessContract.State = CheckoutSuccessContract.State()

    override fun handleEvents(event: CheckoutSuccessContract.Event) {
        when (event) {
            is CheckoutSuccessContract.Event.Init -> init(event.orderId)
            CheckoutSuccessContract.Event.OnViewOrderDetailsButtonClicked -> onViewOrderDetailsButtonClicked()
            CheckoutSuccessContract.Event.OnContinueShoppingButtonClicked -> onContinueShoppingButtonClicked()
        }
    }

    // Event functions.

    private fun init(orderId: String) {
        setState {
            copy(
                isLoading = false,
                orderId = orderId
            )
        }
    }

    private fun onViewOrderDetailsButtonClicked() {
        setEffect {
            CheckoutSuccessContract.Effect.Navigation.NavigateToOrderDetailsScreen(currentState.orderId)
        }
    }

    private fun onContinueShoppingButtonClicked() {
        setEffect {
            CheckoutSuccessContract.Effect.Navigation.ExitWizard
        }
    }
}
