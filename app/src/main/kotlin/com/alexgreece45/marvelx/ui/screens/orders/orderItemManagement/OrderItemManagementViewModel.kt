package com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.data.helper.onFailed
import com.alexgreece45.marvelx.core.data.helper.onNetworkError
import com.alexgreece45.marvelx.core.data.helper.onSuccess
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.core.data.repositories.OrderItemReturnRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.model.OrderItemManagementContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.OrderItemManagementRoute
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStateUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class OrderItemManagementViewModel(
    private val orderInteractor: OrderInteractor,
    private val orderItemReturnRepository: OrderItemReturnRepository,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    private val appLogger: AppLogger,
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<OrderItemManagementContract.Event, OrderItemManagementContract.State, OrderItemManagementContract.Effect>() {

    private val loggerTag = "OrderItemManagementViewModel"

    init {
        val route = savedStateHandle.toRoute<OrderItemManagementRoute>()
        with(route) {
            init(
                orderId = orderId,
                orderItemId = orderItemId
            )
        }
    }

    override fun setInitialState(): OrderItemManagementContract.State =
        OrderItemManagementContract.State()

    override fun handleEvents(event: OrderItemManagementContract.Event) {
        when (event) {
            OrderItemManagementContract.Event.OnBack -> onBack()
            OrderItemManagementContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()

            OrderItemManagementContract.Event.OnMarkAsShippedClicked -> onMarkAsShippedClicked()

            OrderItemManagementContract.Event.OnReturnCardClicked -> onReturnCardClicked()

            OrderItemManagementContract.Event.OnMessageBuyerClicked -> {
                setEffect {
                    OrderItemManagementContract.Effect.Navigation.GoToConversation(
                        orderId = currentState.orderId.value,
                        orderItemId = currentState.orderItemId.value
                    )
                }
            }
        }
    }

    // Event functions.

    private fun init(orderId: String, orderItemId: String) {
        if (orderId.isBlank() || orderItemId.isBlank()) {
            setEffect {
                OrderItemManagementContract.Effect.Navigation.GoBack
            }
            return
        }

        setState {
            copy(
                orderId = OrderId(orderId),
                orderItemId = OrderId(orderItemId)
            )
        }

        viewModelScope.launch {
            getOrderItemDetails()
        }

        viewModelScope.launch {
            getExistingReturnStatus()
        }
    }

    private fun onBack() {
        setEffect {
            OrderItemManagementContract.Effect.Navigation.GoBack
        }
    }

    private fun onErrorButtonClicked() {
        viewModelScope.launch {
            getOrderItemDetails()
        }
    }

    // TODO: Apply the new status through the backend once an order item status endpoint exists.
    //  Until then the transition only lives in this screen's state and is lost on reload.
    private fun onMarkAsShippedClicked() {
        val currentData = (currentState.uiState as? UiState.Success)?.data ?: return
        if (currentData.status != OrderItemStatusUi.Processing) return

        setState {
            copy(uiState = UiState.Success(currentData.copy(status = OrderItemStatusUi.Shipped)))
        }

        setEffect {
            OrderItemManagementContract.Effect.ShowToast(
                message = resourceProvider.getString(
                    R.string.screen_order_item_management_toast_message_status_updated
                )
            )
        }
    }

    private fun onReturnCardClicked() {
        setEffect {
            OrderItemManagementContract.Effect.Navigation.GoToRequestItemReturnScreen(
                orderId = currentState.orderId.value,
                orderItemId = currentState.orderItemId.value
            )
        }
    }

    // Helper functions.

    private suspend fun getExistingReturnStatus() {
        orderItemReturnRepository.getOrderItemReturnState(
            orderId = currentState.orderId,
            orderItemId = currentState.orderItemId
        )
            .onSuccess { returnState ->
                returnState.toUi(configPriceLogic.priceScale)
                    .onSuccess { returnStateUi ->
                        val existingReturnStatus =
                            (returnStateUi as? OrderItemReturnStateUi.Existing)?.orderItemReturn?.returnStatus
                        setState {
                            copy(existingReturnStatus = existingReturnStatus)
                        }
                    }
                    .onFailure { throwable ->
                        appLogger.e(loggerTag) { "Failed to map order item return state to UI: ${throwable.message}" }
                    }
            }
            .onFailed { errorMessage ->
                appLogger.e(loggerTag) { "Failed to fetch order item return state for orderId=${currentState.orderId.value}, orderItemId=${currentState.orderItemId.value}: $errorMessage" }
            }
            .onNetworkError {
                appLogger.e(loggerTag) { "Network error while fetching order item return state for orderId=${currentState.orderId.value}, orderItemId=${currentState.orderItemId.value}" }
            }
    }

    private suspend fun getOrderItemDetails() {
        setState {
            copy(uiState = UiState.Loading)
        }
        when (
            val partialState = orderInteractor.getOrderItemDetails(
                orderId = currentState.orderId,
                orderItemId = currentState.orderItemId
            )
        ) {
            is InteractorPartialState.Failed,
            is InteractorPartialState.Error,
            InteractorPartialState.NoInternetConnection -> {
                setState {
                    copy(uiState = UiState.Error(Unit))
                }
            }

            is InteractorPartialState.Success -> {
                val orderItemUi = partialState.data.toUi(priceScale = configPriceLogic.priceScale)
                setState {
                    copy(uiState = UiState.Success(orderItemUi))
                }
            }
        }
    }
}
