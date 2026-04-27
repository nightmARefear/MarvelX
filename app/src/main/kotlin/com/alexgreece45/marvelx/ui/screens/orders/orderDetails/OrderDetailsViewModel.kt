package com.alexgreece45.marvelx.ui.screens.orders.orderDetails

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsContract
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.toUrlEncoding
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    private val appLogger: AppLogger,
) : BaseViewModel<OrderDetailsContract.Event, OrderDetailsContract.State, OrderDetailsContract.Effect>() {

    override fun setInitialState(): OrderDetailsContract.State = OrderDetailsContract.State()

    override fun handleEvents(event: OrderDetailsContract.Event) {
        when (event) {
            is OrderDetailsContract.Event.Init -> init(event.orderId)
            OrderDetailsContract.Event.OnBack -> onBack()
            OrderDetailsContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
            OrderDetailsContract.Event.OnOrderStatusBannerClicked -> onOrderStatusBannerClicked()
            is OrderDetailsContract.Event.OrderItemEvents.OnTrackButtonClicked -> onOrderItemButtonClicked(
                event.orderItem
            )

        }
    }

    // Event functions.

    private fun init(orderId: String) {
        setState {
            copy(
                orderId = orderId
            )
        }
        getOrderDetails()
    }

    private fun onBack() {
        setEffect {
            OrderDetailsContract.Effect.Navigation.GoBack
        }
    }

    private fun onErrorButtonClicked() {
        getOrderDetails()
    }

    private fun onOrderStatusBannerClicked() {
        currentState.uiState.onSuccess { order ->
            setState {
                copy(
                    bottomSheetType = OrderDetailsBottomSheetType.OrderStatusInfo(order.status)
                )
            }
            setEffect {
                OrderDetailsContract.Effect.ShowBottomSheet
            }
        }
    }

    private fun onOrderItemButtonClicked(orderItem: OrderItemUi) {
        with(orderItem) {
            setEffect {
                OrderDetailsContract.Effect.Navigation.NavigateToOrderItemTrackingHistoryScreen(
                    orderId = currentState.orderId,
                    sellerUserId = sellerUserId,
                    orderItemId = id.value,
                    orderItemTitle = title,
                    orderItemCoverImageUrl = coverImageUrl.toUrlEncoding(),
                    orderItemPriceLabel = price.label
                )
            }
        }
    }

    // Helper functions.
    private fun getOrderDetails() {
        setState {
            copy(
                uiState = UiState.Loading,
            )
        }
        viewModelScope.launch {
            when (val partialState = orderInteractor.getOrderDetails(currentState.orderId)) {
                is InteractorPartialState.Failed,
                is InteractorPartialState.Error,
                InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            uiState = UiState.Error(Unit)
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val orderUi = partialState.data.toUi(
                        getString = resourceProvider::getString,
                        priceScale = configPriceLogic.priceScale,
                        onErrorLog = {
                            appLogger.e("OrderDetailsViewModel") { "Failed to convert `OrderDomain` to `OrderUi`.\n${it}" }
                        }
                    )
                    setState {
                        copy(
                            uiState = if (orderUi == null) UiState.Error(Unit) else UiState.Success(
                                orderUi
                            )
                        )
                    }
                }
            }
        }
    }
}
