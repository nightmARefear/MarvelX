package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import android.net.Uri
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderItemDetails

internal sealed interface RequestItemReturnContract {

    sealed interface Event : BaseEvent {
        data class Init(
            val orderId: String,
            val orderItemId: String,
            val orderItemTitle: String,
            val orderItemCoverImageUrl: String,
            val orderItemPriceLabel: String
        ) : Event

        data object OnClose : Event
        data object OnBack : Event

        data class OnReturnReasonItemSelected(val item: OrderItemReturnReasonUi) : Event

        data object OnRequestRefundCheckboxClicked : Event

        data class OnDescriptionChanged(val description: String) : Event

        data object OnUploadPhotosButtonClicked : Event
        data class OnImageDeleteButtonClicked(val uri: Uri) : Event

        sealed interface ImageSelectionEvents : Event {

            /**
             * Triggered when images are selected in the `photo picker`.
             */
            data class OnSuccess(val imagesData: List<AttachedFileContentData>?) :
                ImageSelectionEvents

            /**
             * Triggered when an error occurred in the image selection process.
             */
            data object OnFailure : ImageSelectionEvents

            /**
             * Triggered when no images were selected.
             */
            data object OnNoSelection : ImageSelectionEvents
        }

        data class OnItemConditionSelected(val itemCondition: OrderItemReturnConditionUi) : Event

        sealed interface RefundDialogEvent : Event {
            data object OnNegativeButtonClicked : RefundDialogEvent
            data object OnPositiveButtonClicked : RefundDialogEvent
        }

        data object OnErrorButtonClicked : Event

        data object OnStickyBottomButtonClicked : Event
    }

    data class State(
        val uiState: UiState<OrderItemReturnState, Unit> = UiState.Loading,
        val wizardMode: RequestItemReturnWizardMode = RequestItemReturnWizardMode.Create,
        val wizardStep: RequestItemReturnWizardStep = RequestItemReturnWizardStep.INITIAL_STEP,

        val orderItemDetails: OrderItemDetails = OrderItemDetails.EMPTY,

        // User input.

        val returnReasons: List<OrderItemReturnReasonUi> = emptyList(),
        val selectedReturnReason: OrderItemReturnReasonUi? = null,
        val isRequestRefundChecked: Boolean = false,

        val descriptionField: InputFieldUi.Text = InputFieldUi.Text(isMandatory = false),
        val descriptionMaxLength: Int = 0,

        val attachedImages: List<AttachedFileContentData> = emptyList(),
        val imageValidationConfig: ImageValidationConfig = ImageValidationConfig(),

        val itemCondition: OrderItemReturnConditionUi? = null,

        val dialogType: RequestItemReturnDialogType? = null,

        val snackbarStateData: SnackbarStateData? = null,
    ) : BaseState {

        val isSubmitButtonEnabled: Boolean
            get() =
                selectedReturnReason != null
                        && descriptionField.isValid
                        && itemCondition != null
                        && !uiState.isLoading
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
        }

        data object OpenPhotoPicker : Effect

        data class ShowSnackbar(val snackbarStateData: SnackbarStateData) : Effect
        data object HideSnackbar : Effect
    }
}