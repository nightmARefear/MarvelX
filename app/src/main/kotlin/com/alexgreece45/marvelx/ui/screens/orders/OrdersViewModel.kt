package com.alexgreece45.marvelx.ui.screens.orders

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersBottomSheetType
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersContract
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.util.extensions.transform
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OrdersViewModel(
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
    private val appLogger: AppLogger,
) :
    BaseViewModel<OrdersContract.Event, OrdersContract.State, OrdersContract.Effect>() {

    override fun setInitialState(): OrdersContract.State = OrdersContract.State()

    override fun handleEvents(event: OrdersContract.Event) {
        when (event) {
            OrdersContract.Event.Init -> init()
            OrdersContract.Event.OnBackClicked -> onBackClicked()
            is OrdersContract.Event.OrderEvents.OnClicked -> onOrderClicked(event.orderId)
            is OrdersContract.Event.OrderEvents.OnStatusChipClicked -> onOrderStatusChipClicked(
                event.orderId,
                event.orderStatusUi
            )

            OrdersContract.Event.OrderStatusInfoBottomSheetEvents.OnBottomSheetDismissed -> onOrderStatusInfoBottomSheetDismissed()
            is OrdersContract.Event.OrderStatusInfoBottomSheetEvents.OnViewOrderDetailsClicked -> onViewOrderDetailsClicked(
                event.orderId
            )

            OrdersContract.Event.OnEmptyOrdersButtonClicked -> {}
            OrdersContract.Event.OnErrorButtonClicked -> {}
        }
    }

    // Event functions.

    private fun init() {
        setState {
            copy(
                uiState = UiState.Loading
            )
        }
        getOrders()
    }

    private fun onBackClicked() {
        setEffect {
            OrdersContract.Effect.Navigation.GoBack
        }
    }

    private fun onOrderClicked(orderId: String) {
        setEffect {
            OrdersContract.Effect.Navigation.GoToOrderDetailsScreen(orderId)
        }
    }

    private fun onOrderStatusChipClicked(orderId: String, orderStatusUi: OrderStatusUi) {
        showBottomSheet(
            type = OrdersBottomSheetType.OrderStatusInfo(
                orderId = orderId,
                statusUi = orderStatusUi
            )
        )
    }

    private fun onOrderStatusInfoBottomSheetDismissed() {
        hideBottomSheet()
    }

    private fun onViewOrderDetailsClicked(orderId: String) {
        hideBottomSheet()
        setEffect {
            OrdersContract.Effect.Navigation.GoToOrderDetailsScreen(orderId)
        }
    }

    // Helper functions.
    private fun getOrders() {
        setState {
            copy(
                uiState = UiState.Loading
            )
        }
        viewModelScope.launch {
            when (val partialState = orderInteractor.getOrders()) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            uiState = UiState.Error(Unit)
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val ordersUi = partialState.data.transform { order ->
                        order.toUi(
                            priceScale = configPriceLogic.priceScale,
                            onErrorLog = { appLogger.e("OrdersViewModel") { "Error converting `OrderPreviewDomain` to `OrderPreviewUi`.\n $it" } }
                        )
                    }
                    if (ordersUi == null) {
                        setState {
                            copy(
                                uiState = UiState.Error(Unit)
                            )
                        }
                    } else {
                        setState {
                            copy(
                                uiState = UiState.Success(ordersUi.toImmutableListOrEmpty())
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showBottomSheet(type: OrdersBottomSheetType) {
        setState {
            copy(
                ordersBottomSheetType = type
            )
        }
        setEffect {
            OrdersContract.Effect.ShowBottomSheet
        }
    }

    private fun hideBottomSheet() {
        setState {
            copy(
                ordersBottomSheetType = null
            )
        }
        setEffect {
            OrdersContract.Effect.HideBottomSheet
        }
    }
}
