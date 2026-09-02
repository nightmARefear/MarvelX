package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import android.net.Uri
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.core.data.model.order.item.ReturnPolicy
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal sealed interface RequestItemReturnContract {

    sealed interface Event : BaseEvent {
        data object OnClose : Event
        data object OnBack : Event

        data class OnReturnReasonItemSelected(val item: OrderItemReturnReasonUi) : Event
        data class OnReturnReasonOtherTextChanged(val text: String) : Event

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

        data class OnReturnConditionItemSelected(val itemCondition: OrderItemReturnConditionUi) :
            Event

        data class OnReturnMethodItemSelected(val item: OrderItemReturnMethodUi) : Event

        data object OnAgreeToReturnPolicyCheckboxClicked : Event
        data object OnReturnPolicyTextClicked : Event

        sealed interface Dialog : Event {
            data object OnNegativeButtonClicked : Dialog
            data object OnPositiveButtonClicked : Dialog
        }

        data object OnErrorButtonClicked : Event

        data object OnStickyBottomButtonClicked : Event

        data object OnBottomSheetErrorButtonClicked : Event
        data object OnBottomSheetDismissed : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val returnState: OrderItemReturnStateUi? = null,
        val hasError: Boolean = false,

        val wizardMode: RequestItemReturnWizardMode = RequestItemReturnWizardMode.Create,
        val wizardStep: RequestItemReturnWizardStep = RequestItemReturnWizardStep.INITIAL_STEP,

        val toolbarTitle: String = "",

        val orderId: OrderIdDomain = OrderIdDomain.EMPTY,
        val orderItemId: OrderIdDomain = OrderIdDomain.EMPTY,

        // User input.

        // Return reason section.
        val returnReasons: ImmutableList<OrderItemReturnReasonUi> = OrderItemReturnReasonUi.entries.toTypedArray()
            .toImmutableList(),
        val selectedReturnReason: OrderItemReturnReasonUi? = null,
        val returnReasonOtherTextField: InputFieldUi.Text = InputFieldUi.Text(isMandatory = true),
        val returnReasonOtherTextMaxLength: Int = 0,
        val isRequestRefundChecked: Boolean = false,

        // Return details section.
        val descriptionField: InputFieldUi.Text = InputFieldUi.Text(isMandatory = false),
        val descriptionMaxLength: Int = 0,

        val attachedImages: List<AttachedFileContentData> = emptyList(),
        val imageValidationConfig: ImageValidationConfig = ImageValidationConfig(),

        val returnConditions: ImmutableList<OrderItemReturnConditionUi> = OrderItemReturnConditionUi.entries.toImmutableList(),
        val selectedReturnCondition: OrderItemReturnConditionUi? = null,

        // Overview section.
        val returnMethods: ImmutableList<OrderItemReturnMethodUi> = OrderItemReturnMethodUi.entries.toImmutableList(),
        val selectedReturnMethod: OrderItemReturnMethodUi? = null,
        val isAgreeToReturnPolicyChecked: Boolean = false,

        // Other variables.
        val returnPoliciesUiState: UiState<ImmutableList<ReturnPolicy>, Unit> = UiState.Loading,

        val isBottomSheetVisible: Boolean = false,
        val dialogType: RequestItemReturnDialogType? = null,
    ) : BaseState {
        val progress: Float
            get() = (wizardStep.ordinal + 1) / RequestItemReturnWizardStep.TOTAL_STEPS.toFloat()

        val isLinearProgressIndicatorVisible: Boolean
            get() = returnState != null
                    && returnState is OrderItemReturnStateUi.Available

        val isStickyBottomButtonVisible: Boolean
            get() = returnState != null
                    && returnState is OrderItemReturnStateUi.Available

        val isStickyBottomButtonEnabled: Boolean
            get() = when (wizardStep) {
                RequestItemReturnWizardStep.RETURN_REASON -> selectedReturnReason != null && if (selectedReturnReason == OrderItemReturnReasonUi.OTHER) returnReasonOtherTextField.isValid else true
                RequestItemReturnWizardStep.RETURN_DETAILS -> descriptionField.isValid && selectedReturnCondition != null
                RequestItemReturnWizardStep.OVERVIEW -> !isLoading && selectedReturnMethod != null && isAgreeToReturnPolicyChecked
            }
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class ExitWithError(val snackbarNavigationPayload: SnackbarNavigationPayload) :
                Navigation

            data class GoToSuccessScreen(
                val orderId: String,
                val orderItemId: String,
                val orderItemReturnId: String
            ) : Navigation
        }

        data object OpenPhotoPicker : Effect

        data object ShowBottomSheet : Effect
        data object HideBottomSheet : Effect
    }
}