package com.alexgreece45.marvelx.ui.screens.orders.orderDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsContract
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.navigation.OrderDetailsRoute
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.toUrlEncoding
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class OrderDetailsViewModel(
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    private val appLogger: AppLogger,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrderDetailsContract.Event, OrderDetailsContract.State, OrderDetailsContract.Effect>() {

    init {
        val route = savedStateHandle.toRoute<OrderDetailsRoute>()
        init(orderId = route.orderId)
    }

    override fun setInitialState(): OrderDetailsContract.State = OrderDetailsContract.State()

    override fun handleEvents(event: OrderDetailsContract.Event) {
        when (event) {
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
                orderId = OrderId(orderId)
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
                    orderId = currentState.orderId.value,
                    orderItemId = orderItemId.value,
                    orderItemTitle = title,
                    orderItemCoverImageUrl = coverImageUrl,
                    orderItemPriceLabel = price.label,
                    sellerUserId = sellerUserId
                )
            }
        }
    }

    // Helper functions.
    private fun getOrderDetails() {
        setState {
            copy(
                uiState = UiState.Loading
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