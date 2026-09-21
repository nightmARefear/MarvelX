package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderItemDetails
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryErrorType
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.OrderItemTrackingHistoryRoute
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class OrderItemTrackingHistoryViewModel(
    private val orderInteractor: OrderInteractor,
    private val resourceProvider: ResourceProvider,
    private val userRepository: UserRepository,
    private val appLogger: AppLogger,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<OrderItemTrackingHistoryContract.Event, OrderItemTrackingHistoryContract.State, OrderItemTrackingHistoryContract.Effect>() {

    init {
        val route = savedStateHandle.toRoute<OrderItemTrackingHistoryRoute>()
        with(route) {
            init(
                orderId = orderId,
                orderItemId = orderItemId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = orderItemCoverImageUrl,
                orderItemPriceLabel = orderItemPriceLabel,
                sellerUserId = sellerUserId
            )
        }
    }

    override fun setInitialState(): OrderItemTrackingHistoryContract.State =
        OrderItemTrackingHistoryContract.State()

    override fun handleEvents(event: OrderItemTrackingHistoryContract.Event) {
        when (event) {
            OrderItemTrackingHistoryContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
            OrderItemTrackingHistoryContract.Event.OnBack -> onBack()
            OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnClicked -> onSellerDetailsCardClicked()
            OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnErrorButtonClicked -> onSellerDetailsCardErrorButtonClicked()

            OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnCopyButtonClicked -> onCourierCardCopyButtonClicked()
            OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnViewOnCourierWebsiteButtonClicked -> onCourierCardViewOnCourierWebsiteButtonClicked()
            OrderItemTrackingHistoryContract.Event.OnCurrentStatusClicked -> onCurrentStatusClicked()
            OrderItemTrackingHistoryContract.Event.OnReturnActionButtonClicked -> onRequestReturnClicked()
            OrderItemTrackingHistoryContract.Event.OnRefundActionButtonClicked -> onRequestRefundClicked()
            OrderItemTrackingHistoryContract.Event.OnContactSellerButtonClicked -> onContactSellerButtonClicked()
            OrderItemTrackingHistoryContract.Event.OnBottomSheetDismissed -> onBottomSheetDismissed()
        }
    }

    // Event functions.

    private fun init(
        orderId: String,
        orderItemId: String,
        orderItemTitle: String,
        orderItemCoverImageUrl: String,
        orderItemPriceLabel: String,
        sellerUserId: String
    ) {
        if (orderId.isBlank()
            || orderItemId.isBlank()
            || sellerUserId.isBlank()
            || orderItemCoverImageUrl.isBlank()
        ) {
            setEffect {
                OrderItemTrackingHistoryContract.Effect.Navigation.GoBack
            }
            return
        }

        setState {
            copy(
                orderItemDetails = OrderItemDetails(
                    orderId = OrderIdDomain(orderId),
                    orderItemId = OrderIdDomain(orderItemId),
                    orderItemTitle = orderItemTitle,
                    orderItemCoverImageUrl = orderItemCoverImageUrl,
                    orderItemPriceLabel = orderItemPriceLabel
                ),
                sellerUserId = UserId(sellerUserId)
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
        if (currentState.isBottomSheetVisible) {
            setState {
                copy(
                    isBottomSheetVisible = false
                )
            }
        } else {
            setEffect {
                OrderItemTrackingHistoryContract.Effect.Navigation.GoBack
            }
        }
    }

    private fun onSellerDetailsCardClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoToProfileDetailsScreen(currentState.sellerUserId.value)
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

    private fun onCurrentStatusClicked() {
        setState {
            copy(
                isBottomSheetVisible = true
            )
        }
        setEffect {
            OrderItemTrackingHistoryContract.Effect.ShowBottomSheet
        }
    }

    private fun onRequestReturnClicked() {
        setEffect {
            with(currentState.orderItemDetails) {
                OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemReturnScreen(
                    orderId = orderId.value,
                    orderItemId = orderItemId.value
                )
            }
        }
    }

    private fun onRequestRefundClicked() {
        onRequestReturnClicked()
    }

    private fun onContactSellerButtonClicked() {
        setEffect {
            OrderItemTrackingHistoryContract.Effect.Navigation.GoToConversationScreen(
                orderId = currentState.orderItemDetails.orderId.value,
                orderItemId = currentState.orderItemDetails.orderItemId.value
            )
        }
    }

    private fun onBottomSheetDismissed() {
        setState {
            copy(
                isBottomSheetVisible = false
            )
        }
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
                orderId = currentState.orderItemDetails.orderId,
                orderItemId = currentState.orderItemDetails.orderItemId
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
        when (val result = userRepository.getBasicProfileDetails(currentState.sellerUserId)) {
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