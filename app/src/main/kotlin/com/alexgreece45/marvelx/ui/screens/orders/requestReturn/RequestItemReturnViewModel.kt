package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.InputFieldValidator
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.repositories.OrderItemReturnRepository
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.toDomain
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnConditionUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toData
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.decodeFromUrl
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class RequestItemReturnViewModel(
    private val inputFieldValidator: InputFieldValidator,
    private val resourceProvider: ResourceProvider,
    private val orderItemReturnRepository: OrderItemReturnRepository
) :
    BaseViewModel<RequestItemReturnContract.Event, RequestItemReturnContract.State, RequestItemReturnContract.Effect>() {

    override fun setInitialState(): RequestItemReturnContract.State =
        RequestItemReturnContract.State()

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
            is RequestItemReturnContract.Event.OnReturnReasonItemSelected -> onReturnReasonDropdownItemSelected(
                event.item
            )

            RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked -> onRequestRefundCheckboxClicked()

            is RequestItemReturnContract.Event.OnDescriptionChanged -> onDescriptionChanged(
                event.description
            )

            is RequestItemReturnContract.Event.ImageSelectionEvents.OnSuccess -> onImageSelectionSuccess(
                event.imagesData
            )

            RequestItemReturnContract.Event.ImageSelectionEvents.OnFailure -> onImageSelectionFailure()
            RequestItemReturnContract.Event.ImageSelectionEvents.OnNoSelection -> onImageSelectionNone()

            is RequestItemReturnContract.Event.OnItemConditionSelected -> onItemConditionSelected(
                event.itemCondition
            )

            RequestItemReturnContract.Event.RefundDialogEvent.OnNegativeButtonClicked -> onRefundDialogNegativeButtonClicked()
            RequestItemReturnContract.Event.RefundDialogEvent.OnPositiveButtonClicked -> onRefundDialogPositiveButtonClicked()

            RequestItemReturnContract.Event.OnSubmitButtonClicked -> onSubmitClicked()
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
                orderItemPriceLabel = orderItemPriceLabel,
                descriptionField = descriptionField.copy(
                    validationRules = listOf(
                        InputFieldRule.ValidateStringMaxLength(
                            length = DESCRIPTION_MAX_LENGTH,
                            errorMessage = resourceProvider.getString(
                                R.string.screen_request_return_description_error_max_characters,
                                DESCRIPTION_MAX_LENGTH
                            )
                        )
                    )
                ),
                descriptionMaxLength = DESCRIPTION_MAX_LENGTH
            )
        }
    }

    private fun onBack() {
        setEffect {
            RequestItemReturnContract.Effect.Navigation.GoBack
        }
    }

    private fun onReturnReasonDropdownItemSelected(item: OrderItemReturnReasonUi) {
        setState {
            copy(
                selectedReturnReason = item
            )
        }
    }

    private fun onRequestRefundCheckboxClicked() {
        if (currentState.isRequestRefundChecked) {
            setState {
                copy(
                    isRequestRefundChecked = false
                )
            }
        } else {
            setState {
                copy(
                    isRefundDialogVisible = true
                )
            }
        }
    }

    private fun onDescriptionChanged(description: String) {
        viewModelScope.launch {
            val descriptionValidationResult = inputFieldValidator.validateInput(
                value = description,
                rules = currentState.descriptionField.validationRules
            )

            setState {
                copy(
                    descriptionField = descriptionField.copy(
                        value = description,
                        validationResult = descriptionValidationResult
                    )
                )
            }
        }
    }

    private fun onImageSelectionSuccess(attachedFiles: List<AttachedFileContentData>?) {
        if (attachedFiles == null) {
            onImageSelectionFailure()
            return
        }
    }

    private fun onImageSelectionFailure() {
        setEffect {
            RequestItemReturnContract.Effect.ShowSnackbar(
                message = resourceProvider.getString(R.string.screen_request_item_refund_toast_image_selection_failure),
                type = SnackbarType.ERROR
            )
        }
    }

    private fun onImageSelectionNone() {
        setEffect {
            RequestItemReturnContract.Effect.ShowSnackbar(
                message = resourceProvider.getString(R.string.screen_request_item_refund_toast_image_selection_none),
                type = SnackbarType.INFO
            )
        }
    }

    private fun onItemConditionSelected(itemCondition: OrderItemReturnConditionUi) {
        setState {
            copy(
                itemCondition = itemCondition
            )
        }
    }

    private fun onRefundDialogNegativeButtonClicked() {
        setState {
            copy(
                isRefundDialogVisible = false
            )
        }
    }

    private fun onRefundDialogPositiveButtonClicked() {
        setState {
            copy(
                isRefundDialogVisible = false,
                isRequestRefundChecked = true
            )
        }
    }

    private fun onSubmitClicked() {
        val returnReason = currentState.selectedReturnReason
        val itemCondition = currentState.itemCondition
        if (currentState.uiState.isLoading || returnReason == null || itemCondition == null) return

        setState { copy(uiState = UiState.Loading) }
        viewModelScope.launch {
            when (val result =
                orderItemReturnRepository.placeOrderItemReturnRequest(
                    orderId = currentState.orderId,
                    orderItemId = currentState.orderItemId,
                    reason = returnReason.toDomain().key,
                    requestRefund = currentState.isRequestRefundChecked,
                    attachedImages = currentState.attachedImages,
                    description = currentState.descriptionField.value,
                    itemCondition = itemCondition.toData()
                )
            ) {
                is MultipartResult.GenericError,
                MultipartResult.NetworkError,
                MultipartResult.ImageProcessingError -> {
                    setState {
                        copy(
                            uiState = UiState.Error(Unit)
                        )
                    }
                }

                is MultipartResult.Success -> {
                    setState {
                        copy(
                            uiState = UiState.Success(
                                result.data
                            )
                        )
                    }
                }
            }
        }
    }

    // Helper functions.

    private val DESCRIPTION_MAX_LENGTH = 500
}