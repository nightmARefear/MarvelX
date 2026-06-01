package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.domain.model_api.order.OrderId
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryErrorType
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.decodeFromUrl
import com.alexgreece45.marvelx.util.extensions.toUrlEncoding
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OrderItemTrackingHistoryViewModel(
    private val orderInteractor: OrderInteractor,
    private val resourceProvider: ResourceProvider,
    private val userRepository: UserRepository,
    private val appLogger: AppLogger,
) :
    BaseViewModel<OrderItemTrackingHistoryContract.Event, OrderItemTrackingHistoryContract.State, OrderItemTrackingHistoryContract.Effect>() {

    override fun setInitialState(): OrderItemTrackingHistoryContract.State =
        OrderItemTrackingHistoryContract.State()

    override fun handleEvents(event: OrderItemTrackingHistoryContract.Event) {
        when (event) {
            is OrderItemTrackingHistoryContract.Event.Init -> init(
                orderId = event.orderId,
                orderItemId = event.orderItemId,
                sellerId = event.sellerId,
                orderItemTitle = event.orderItemTitle,
                orderItemCoverImageUrl = event.orderItemCoverImageUrl,
                orderItemPriceLabel = event.orderItemPriceLabel
            )

            OrderItemTrackingHistoryContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
            OrderItemTrackingHistoryContract.Event.OnBack -> onBack()
            OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnClicked -> onSellerDetailsCardClicked()
            OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnErrorButtonClicked -> onSellerDetailsCardErrorButtonClicked()

            OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnCopyButtonClicked -> onCourierCardCopyButtonClicked()
            OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnViewOnCourierWebsiteButtonClicked -> onCourierCardViewOnCourierWebsiteButtonClicked()
            OrderItemTrackingHistoryContract.Event.OnReturnActionButtonClicked -> onRequestReturnClicked()
            OrderItemTrackingHistoryContract.Event.OnRefundActionButtonClicked -> onRequestRefundClicked()
            OrderItemTrackingHistoryContract.Event.ExtraActionButtonEvents.OnContactSupportClicked -> onExtraActionButtonContactSupportClicked()
            OrderItemTrackingHistoryContract.Event.ExtraActionButtonEvents.OnContactSellerClicked -> onExtraActionButtonContactSellerClicked()
        }
    }

    // Event functions.

    private fun init(
        orderId: String,
        orderItemId: String,
        sellerId: String,
        orderItemTitle: String,
        orderItemCoverImageUrl: String,
        orderItemPriceLabel: String
    ) {
        val decodedOrderItemCoverImageUrl = orderItemCoverImageUrl.decodeFromUrl()
        if (orderId.isBlank()
            || orderItemId.isBlank()
            || sellerId.isBlank()
            || decodedOrderItemCoverImageUrl.isBlank()
        ) {
            setEffect {
                OrderItemTrackingHistoryContract.Effect.Navigation.GoBack
            }
            return
        }

        setState {
            copy(
                orderId = OrderId(orderId),
                orderItemId = OrderId(orderItemId),
                sellerId = sellerId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = decodedOrderItemCoverImageUrl,
                orderItemPriceLabel = orderItemPriceLabel
            )
        }

        viewModelScope.launch {
            launch {
                getOrderItemTrackingHistory()
            }

            launch {
                getSellerInfo()
            }
        }
    }

    private fun onBack() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoBack
        }
    }

    private fun onSellerDetailsCardClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoToProfileDetailsScreen(currentState.sellerId)
        }
    }

    private fun onSellerDetailsCardErrorButtonClicked() {
        viewModelScope.launch {
            getSellerInfo()
        }
    }

    private fun onCourierCardCopyButtonClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.SetClipboardText(
                toastMessage = resourceProvider.getString(R.string.screen_order_item_tracking_history_toast_message_success_copying_to_clipboard),
                text = currentState.orderItemTrackingHistoryUi?.trackingId.orEmpty()
            )
        }
    }

    private fun onCourierCardViewOnCourierWebsiteButtonClicked() {
        // TODO: Navigate to webpage.
    }

    private fun onRequestReturnClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemReturnScreen(
                orderId = currentState.orderId.value,
                orderItemId = currentState.orderItemId.value,
                orderItemTitle = currentState.orderItemTitle,
                orderItemCoverImageUrl = currentState.orderItemCoverImageUrl.toUrlEncoding(),
                orderItemPriceLabel = currentState.orderItemPriceLabel
            )
        }
    }

    private fun onRequestRefundClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemRefundScreen(
                orderId = currentState.orderId.value,
                orderItemId = currentState.orderItemId.value,
                orderItemTitle = currentState.orderItemTitle,
                orderItemCoverImageUrl = currentState.orderItemCoverImageUrl.toUrlEncoding(),
                orderItemPriceLabel = currentState.orderItemPriceLabel
            )
        }
    }

    private fun onExtraActionButtonContactSupportClicked() {

    }

    private fun onExtraActionButtonContactSellerClicked() {
        // TODO: Navigate to chat screen.
    }

    private fun onErrorButtonClicked() {
        // TODO: Refine coroutine handling.
        viewModelScope.launch {
            launch {
                getOrderItemTrackingHistory()
            }

            if (currentState.sellerDetailsUiState is UiState.Error) {
                launch {
                    getSellerInfo()
                }
            }
        }
    }

    // Helper functions.

    private suspend fun getOrderItemTrackingHistory() {
        setState {
            copy(
                uiState = UiState.Loading
            )
        }
        when (
            val partialState = orderInteractor.getOrderItemTrackingHistory(
                orderId = currentState.orderId.value,
                orderItemId = currentState.orderItemId.value
            )
        ) {
            is InteractorPartialState.Failed -> {
                val parsedErrorType =
                    OrderItemTrackingHistoryErrorType.entries.firstOrNull { it.name == partialState.failureMessage }
                        ?: OrderItemTrackingHistoryErrorType.ERROR_FETCHING_DATA
                setState {
                    copy(
                        uiState = UiState.Error(parsedErrorType)
                    )
                }
            }

            is InteractorPartialState.Error,
            InteractorPartialState.NoInternetConnection -> {
                setState {
                    copy(
                        uiState = UiState.Error(OrderItemTrackingHistoryErrorType.ERROR_FETCHING_DATA)
                    )
                }
            }

            is InteractorPartialState.Success -> {
                val orderItemTrackingHistoryUi = partialState.data.toUi(
                    onErrorLog = {
                        appLogger.e("OrderItemTrackingHistoryViewModel") {
                            "An error occurred whilst converting `OrderItemTrackingHistoryDomain` to `OrderItemTrackingHistoryUi`\n$it"
                        }
                    }
                )
                if (orderItemTrackingHistoryUi == null) {
                    setState {
                        copy(
                            uiState = UiState.Error(OrderItemTrackingHistoryErrorType.ERROR_FETCHING_DATA)
                        )
                    }
                    return
                }

                setState {
                    copy(
                        uiState = UiState.Success(orderItemTrackingHistoryUi)
                    )
                }
            }
        }
    }

    private suspend fun getSellerInfo() {
        setState {
            copy(
                sellerDetailsUiState = UiState.Loading
            )
        }
        when (val result = userRepository.getBasicProfileDetails(currentState.sellerId)) {
            is RepositoryResult.Failed,
            is RepositoryResult.NetworkError -> {
                setState {
                    copy(
                        sellerDetailsUiState = UiState.Error(Unit)
                    )
                }
            }

            is RepositoryResult.Success -> {
                setState {
                    copy(
                        sellerDetailsUiState = UiState.Success(result.data)
                    )
                }
            }
        }
    }
}
