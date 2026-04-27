package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.util.extensions.decodeFromUrl

class RequestItemReturnViewModel(
    private val appLogger: AppLogger
):
    BaseViewModel<RequestItemReturnContract.Event, RequestItemReturnContract.State, RequestItemReturnContract.Effect>() {

    override fun setInitialState(): RequestItemReturnContract.State = RequestItemReturnContract.State()

    override fun handleEvents(event: RequestItemReturnContract.Event) {
        when (event) {
            is RequestItemReturnContract.Event.Init -> init(
                orderId = event.orderId,
                orderItemId = event.orderItemId,
                orderItemTitle = event.orderItemTitle,
                orderItemCoverImageUrl = event.orderItemCoverImageUrl,
                orderItemPriceLabel = event.orderItemPriceLabel
            )

            RequestItemReturnContract.Event.OnBack -> onBack()
            is RequestItemReturnContract.Event.OnReturnReasonSelected -> onReturnReasonSelected(event.reason)
            is RequestItemReturnContract.Event.OnDescriptionChanged -> onDescriptionChanged(event.description)
            RequestItemReturnContract.Event.OnSubmitClicked -> onSubmitClicked()
        }
    }

    // Event functions.

    private fun init(
        orderId: String,
        orderItemId: String,
        orderItemTitle: String,
        orderItemCoverImageUrl: String,
        orderItemPriceLabel: String
    ) {
        setState {
            copy(
                orderId = orderId,
                orderItemId = orderItemId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = orderItemCoverImageUrl.decodeFromUrl(),
                orderItemPriceLabel = orderItemPriceLabel
            )
        }
    }

    private fun onBack() {
        setEffect {
            RequestItemReturnContract.Effect.Navigation.GoBack
        }
    }

    private fun onReturnReasonSelected(reason: OrderItemReturnReasonUi) {
        setState {
            copy(selectedReason = reason)
        }
    }

    private fun onDescriptionChanged(description: String) {
        setState {
            copy(description = description)
        }
    }

    private fun onSubmitClicked() {
        val reason = currentState.selectedReason ?: return

        setState { copy(isSubmitting = true) }

        // TODO: Call interactor to submit the return request when the backend API is available.
        appLogger.d("RequestReturnViewModel") {
            "Submitting return request for order ${currentState.orderId}, item ${currentState.orderItemId}. " +
                "Reason: $reason, Description: ${currentState.description}"
        }

        setEffect {
            RequestItemReturnContract.Effect.Navigation.GoBack
        }
    }
}
