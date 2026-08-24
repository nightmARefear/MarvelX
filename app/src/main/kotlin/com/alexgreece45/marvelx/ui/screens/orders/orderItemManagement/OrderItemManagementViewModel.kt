package com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.model.OrderItemManagementContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.OrderItemManagementRoute
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class OrderItemManagementViewModel(
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<OrderItemManagementContract.Event, OrderItemManagementContract.State, OrderItemManagementContract.Effect>() {

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
            OrderItemManagementContract.Event.DropdownEvents.OnClicked -> onDropdownClicked()
            OrderItemManagementContract.Event.DropdownEvents.OnDismissed -> onDropdownDismissed()
            is OrderItemManagementContract.Event.DropdownEvents.OnItemSelected -> onDropdownItemSelected(
                event.status
            )

            OrderItemManagementContract.Event.OnApplyStatusButtonClicked -> onApplyStatusButtonClicked()
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

    private fun onDropdownClicked() {
        setState {
            copy(isDropdownExpanded = !isDropdownExpanded)
        }
    }

    private fun onDropdownDismissed() {
        setState {
            copy(isDropdownExpanded = false)
        }
    }

    private fun onDropdownItemSelected(status: OrderItemStatusUi) {
        setState {
            copy(
                selectedNewStatus = status,
                isDropdownExpanded = false
            )
        }
    }

    private fun onApplyStatusButtonClicked() {
        val newStatus = currentState.selectedNewStatus ?: return
        val currentData = (currentState.uiState as? UiState.Success)?.data ?: return
        if (newStatus == currentData.status) return

        setState {
            copy(
                uiState = UiState.Success(currentData.copy(status = newStatus)),
                selectedNewStatus = null
            )
        }

        setEffect {
            OrderItemManagementContract.Effect.ShowToast(
                message = resourceProvider.getString(
                    R.string.screen_order_item_management_toast_message_status_updated
                )
            )
        }
    }

    // Helper functions.

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
