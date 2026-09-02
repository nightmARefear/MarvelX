package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.data.helper.onError
import com.alexgreece45.marvelx.core.data.helper.onSuccess
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnId
import com.alexgreece45.marvelx.core.data.repositories.OrderItemReturnRepository
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.model.RequestItemReturnSuccessContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.navigation.RequestItemReturnSuccessRoute
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class RequestItemReturnSuccessViewModel(
    savedStateHandle: SavedStateHandle,
    private val configPriceLogic: ConfigPriceLogic,
    private val orderItemReturnRepository: OrderItemReturnRepository,
) : BaseViewModel<RequestItemReturnSuccessContract.Event, RequestItemReturnSuccessContract.State, RequestItemReturnSuccessContract.Effect>() {

    init {
        val route = savedStateHandle.toRoute<RequestItemReturnSuccessRoute>()

        init(
            orderId = route.orderId,
            orderItemId = route.orderItemId,
            orderItemReturnId = OrderItemReturnId(route.orderItemReturnId)
        )
    }

    override fun setInitialState(): RequestItemReturnSuccessContract.State =
        RequestItemReturnSuccessContract.State()

    override fun handleEvents(event: RequestItemReturnSuccessContract.Event) {
        when (event) {
            RequestItemReturnSuccessContract.Event.OnBack -> onBack()
            RequestItemReturnSuccessContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
            RequestItemReturnSuccessContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()
        }
    }

    // Event functions.

    private fun onBack() {
        setEffect {
            RequestItemReturnSuccessContract.Effect.Navigation.GoBack
        }
    }

    private fun onErrorButtonClicked() {
        getOrderItemReturn()
    }

    private fun onStickyBottomButtonClicked() {
        setEffect {
            RequestItemReturnSuccessContract.Effect.Navigation.NavigateToRequestItemReturnScreen(
                orderId = currentState.orderId,
                orderItemId = currentState.orderItemId
            )
        }
    }

    // Helper functions.

    private fun init(
        orderId: String,
        orderItemId: String,
        orderItemReturnId: OrderItemReturnId
    ) {
        setState {
            copy(
                uiState = UiState.Loading,
                orderId = orderId,
                orderItemId = orderItemId,
                orderItemReturnId = orderItemReturnId
            )
        }
        getOrderItemReturn()
    }

    private fun getOrderItemReturn() {
        setState {
            copy(
                uiState = UiState.Loading
            )
        }
        viewModelScope.launch {
            orderItemReturnRepository.getOrderItemReturnById(currentState.orderItemReturnId)
                .onSuccess { data ->
                    data.toUi(configPriceLogic.priceScale)
                        .onSuccess { orderItemReturnUi ->
                            setState {
                                copy(
                                    uiState = UiState.Success(orderItemReturnUi)
                                )
                            }
                        }
                        .onFailure {
                            setState {
                                copy(
                                    uiState = UiState.Error(Unit)
                                )
                            }
                        }
                }
                .onError {
                    setState {
                        copy(
                            uiState = UiState.Error(Unit)
                        )
                    }
                }
        }
    }
}