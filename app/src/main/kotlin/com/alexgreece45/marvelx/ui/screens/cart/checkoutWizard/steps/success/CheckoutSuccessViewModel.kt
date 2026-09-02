package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model.CheckoutSuccessContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.navigation.CheckoutSuccessRoute
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class CheckoutSuccessViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CheckoutSuccessContract.Event, CheckoutSuccessContract.State, CheckoutSuccessContract.Effect>() {

    init {
        val route = savedStateHandle.toRoute<CheckoutSuccessRoute>()

        init(route.orderId)
    }

    override fun setInitialState(): CheckoutSuccessContract.State = CheckoutSuccessContract.State()

    override fun handleEvents(event: CheckoutSuccessContract.Event) {
        when (event) {
            CheckoutSuccessContract.Event.OnBack -> onBack()
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

    private fun onBack() {
        setEffect {
            CheckoutSuccessContract.Effect.Navigation.GoBack
        }
    }

    private fun onViewOrderDetailsButtonClicked() {
        setEffect {
            CheckoutSuccessContract.Effect.Navigation.NavigateToOrderDetailsScreen(currentState.orderId)
        }
    }

    private fun onContinueShoppingButtonClicked() {
        setEffect {
            CheckoutSuccessContract.Effect.Navigation.GoBack // TODO: Go to marketplace screen.
        }
    }
}