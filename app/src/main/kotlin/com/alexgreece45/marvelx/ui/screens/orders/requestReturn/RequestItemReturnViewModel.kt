package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.net.Uri
import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.imageValidator.ImageValidator
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationError
import com.alexgreece45.marvelx.common_ui.imageValidator.model.MultiImageValidationResult
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.InputFieldValidator
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState
import com.alexgreece45.marvelx.core.data.repositories.OrderItemReturnRepository
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.toData
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.toData
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderItemDetails
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnConditionUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnDialogType
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardMode
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardStep
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toData
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeDecodeFromUrl
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

@KoinViewModel
internal class RequestItemReturnViewModel(
    private val inputFieldValidator: InputFieldValidator,
    private val resourceProvider: ResourceProvider,
    private val orderItemReturnRepository: OrderItemReturnRepository,
    private val imageValidator: ImageValidator,
    private val configLogic: ConfigLogic,
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

            RequestItemReturnContract.Event.OnClose -> onClose()
            RequestItemReturnContract.Event.OnBack -> onBack()
            is RequestItemReturnContract.Event.OnReturnReasonItemSelected -> onReturnReasonDropdownItemSelected(
                event.item
            )

            RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked -> onRequestRefundCheckboxClicked()

            is RequestItemReturnContract.Event.OnDescriptionChanged -> onDescriptionChanged(
                event.description
            )

            RequestItemReturnContract.Event.OnUploadPhotosButtonClicked -> onUploadPhotosButtonClicked()
            is RequestItemReturnContract.Event.OnImageDeleteButtonClicked -> onImageDeleteButtonClicked(
                event.uri
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

            RequestItemReturnContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()

            RequestItemReturnContract.Event.OnStickyBottomButtonClicked -> submitOrderItemReturn()
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
                uiState = UiState.Loading,
                orderItemDetails = OrderItemDetails(
                    orderId = OrderIdDomain(orderId),
                    orderItemId = OrderIdDomain(orderItemId),
                    orderItemTitle = orderItemTitle,
                    orderItemCoverImageUrl = orderItemCoverImageUrl.safeDecodeFromUrl(""),
                    orderItemPriceLabel = orderItemPriceLabel
                ),
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
                descriptionMaxLength = DESCRIPTION_MAX_LENGTH,
                imageValidationConfig = ImageValidationConfig(
                    allowedExtensions = configLogic.supportedImageFileExtensions,
                    maxCount = 5,
                    maxSizeMB = 5
                )
            )
        }

        getOrderItemReturn()
    }

    private fun onClose() {
        onBack()
    }

    private fun onBack() {
        if (currentState.dialogType != null) {
            setState {
                copy(
                    dialogType = null
                )
            }
            return
        }

        if (currentState.wizardMode is RequestItemReturnWizardMode.Exists) {
            setEffect {
                RequestItemReturnContract.Effect.Navigation.GoBack
            }
        } else {
            when (currentState.wizardStep) {
                RequestItemReturnWizardStep.RETURN_REASON -> {
                    setState {
                        copy(
                            dialogType = RequestItemReturnDialogType.Exit
                        )
                    }
                }

                RequestItemReturnWizardStep.RETURN_DETAILS,
                RequestItemReturnWizardStep.OVERVIEW -> {
                    setState {
                        copy(
                            wizardStep = wizardStep.previousStep
                        )
                    }
                }
            }
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
                    dialogType = RequestItemReturnDialogType.Refund
                )
            }
        }
    }

    private fun onDescriptionChanged(description: String) {
        val descriptionValidationResult = inputFieldValidator.validateInputSerially(
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

    private fun onUploadPhotosButtonClicked() {
        setEffect {
            RequestItemReturnContract.Effect.OpenPhotoPicker
        }
    }

    private fun onImageDeleteButtonClicked(uri: Uri) {
        setState {
            copy(
                attachedImages = attachedImages.filterNot { it.uri == uri }
            )
        }
    }

    private fun onImageSelectionSuccess(attachedFiles: List<AttachedFileContentData>?) {
        if (attachedFiles == null) {
            onImageSelectionFailure()
            return
        }

        val imageValidationResult = imageValidator.validateImages(
            oldImages = currentState.attachedImages,
            newImages = attachedFiles,
            config = currentState.imageValidationConfig
        )
        if (imageValidationResult is MultiImageValidationResult.Success) {
            setState {
                copy(
                    attachedImages = attachedImages + attachedFiles
                )
            }
            return
        }

        val errorStringResId = when (imageValidationResult) {
            is MultiImageValidationResult.FileError -> {
                when (imageValidationResult.error) {
                    ImageValidationError.OverMaxSize -> R.string.screen_request_item_return_refund_error_attaching_images_over_max_size
                    ImageValidationError.InvalidExtensionType -> R.string.screen_request_item_return_refund_error_attaching_images_invalid_extension
                    ImageValidationError.AttachmentError -> R.string.screen_request_item_return_refund_error_attaching_images_generic
                }
            }

            MultiImageValidationResult.OverMaxCount -> R.string.screen_request_item_return_refund_error_attaching_images_over_max_count
            MultiImageValidationResult.AlreadyExists -> R.string.screen_request_item_return_refund_error_attaching_images_existing_image
        }
        showErroneousSnackbar(message = resourceProvider.getString(errorStringResId))
    }

    private fun onImageSelectionFailure() {
        showErroneousSnackbar(
            message = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_failure_message),
            snackbarActionData = SnackbarActionData(
                label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_failure_button),
                onClick = { hideSnackbar() }
            )
        )
    }

    private fun onImageSelectionNone() {
        showSnackbar(
            message = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_none_message),
            type = SnackbarType.INFO,
            snackbarActionData = SnackbarActionData(
                label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_none_button),
                onClick = { hideSnackbar() }
            )
        )
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
                dialogType = null
            )
        }
    }

    private fun onRefundDialogPositiveButtonClicked() {
        setState {
            copy(
                dialogType = null,
                isRequestRefundChecked = true
            )
        }
    }

    private fun onErrorButtonClicked() {
        getOrderItemReturn()
    }

    private fun onStickyBottomButtonClicked() {
        if (currentState.uiState.isLoading || currentState.wizardMode is RequestItemReturnWizardMode.Exists) return

        when (currentState.wizardStep) {
            RequestItemReturnWizardStep.RETURN_REASON,
            RequestItemReturnWizardStep.RETURN_DETAILS -> {
                setState {
                    copy(
                        wizardStep = wizardStep.nextStep
                    )
                }
            }

            RequestItemReturnWizardStep.OVERVIEW -> submitOrderItemReturn()
        }
    }

    // Helper functions.

    private fun submitOrderItemReturn() {
        val returnReason = currentState.selectedReturnReason
        val itemCondition = currentState.itemCondition
        if (currentState.uiState.isLoading || returnReason == null || itemCondition == null) return

        setState { copy(uiState = UiState.Loading) }
        viewModelScope.launch {
            val result =
                orderItemReturnRepository.submitOrderItemReturn(
                    orderId = currentState.orderItemDetails.orderId.toData(),
                    orderItemId = currentState.orderItemDetails.orderItemId.toData(),
                    returnReason = returnReason.toData(),
                    requestRefund = currentState.isRequestRefundChecked,
                    attachedImages = currentState.attachedImages,
                    description = currentState.descriptionField.value,
                    itemCondition = itemCondition.toData()
                )
            when (result) {
                is MultipartResult.GenericError,
                MultipartResult.NetworkError,
                MultipartResult.ImageProcessingError -> {
                    setState {
                        copy(
                            uiState = UiState.Success(OrderItemReturnState.Available)
                        )
                    }
                    showErroneousSnackbar(
                        message = resourceProvider.getString(R.string.screen_request_item_return_snackbar_error_submitting_item_return_message),
                        snackbarActionData = SnackbarActionData(
                            label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_error_submitting_item_return_button),
                            onClick = { submitOrderItemReturn() }
                        )
                    )
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

    private fun getOrderItemReturn() {
        setState {
            copy(
                uiState = UiState.Loading
            )
        }
        viewModelScope.launch {
            val result = orderItemReturnRepository.getOrderItemReturn(
                orderId = currentState.orderItemDetails.orderId.toData(),
                orderItemId = currentState.orderItemDetails.orderItemId.toData()
            )
            when (result) {
                is RepositoryResult.Failed,
                RepositoryResult.NetworkError -> {
                    setState {
                        copy(
                            uiState = UiState.Error(Unit)
                        )
                    }
                }

                is RepositoryResult.Success -> {
                    val returnState = result.data
                    setState {
                        copy(
                            uiState = UiState.Success(returnState),
                            wizardMode = if (returnState is OrderItemReturnState.Available) RequestItemReturnWizardMode.Create else RequestItemReturnWizardMode.Exists,
                            wizardStep = RequestItemReturnWizardStep.INITIAL_STEP
                        )
                    }
                }
            }
        }
    }

    private fun showSnackbar(
        message: String,
        type: SnackbarType,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    ) {
        val newSnackbarUuid = UUID.randomUUID()
        val snackbarStateData = SnackbarStateData(
            uuid = newSnackbarUuid,
            message = message,
            type = type,
            duration = duration,
            snackbarActionData = snackbarActionData,
            onDismiss = {
                if (currentState.snackbarStateData?.uuid == newSnackbarUuid) {
                    setState {
                        copy(
                            snackbarStateData = null
                        )
                    }
                }
            }
        )

        setState {
            copy(
                snackbarStateData = snackbarStateData
            )
        }
        setEffect {
            RequestItemReturnContract.Effect.ShowSnackbar(snackbarStateData = snackbarStateData)
        }
    }

    private fun showErroneousSnackbar(
        message: String,
        snackbarActionData: SnackbarActionData? = null
    ) {
        showSnackbar(
            message = message,
            type = SnackbarType.ERROR,
            snackbarActionData = snackbarActionData
        )
    }

    private fun hideSnackbar() {
        setEffect {
            RequestItemReturnContract.Effect.HideSnackbar
        }
    }

    private val DESCRIPTION_MAX_LENGTH = 500
}