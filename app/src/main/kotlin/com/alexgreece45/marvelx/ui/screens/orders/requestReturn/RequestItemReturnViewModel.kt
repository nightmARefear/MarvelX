package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.net.Uri
import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.imageValidator.ImageValidator
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationError
import com.alexgreece45.marvelx.common_ui.imageValidator.model.MultiImageValidationResult
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.InputFieldValidator
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.helper.onError
import com.alexgreece45.marvelx.core.data.helper.onSuccess
import com.alexgreece45.marvelx.core.data.repositories.OrderItemReturnRepository
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.data.repository.marketplace.MarketplaceRepository
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.toData
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.toData
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnConditionUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnMethodUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnSellerActionUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStateUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnDialogType
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardMode
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardStep
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toData
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.RequestItemReturnRoute
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [RequestItemReturnViewModel::class])
internal class RequestItemReturnViewModel(
    private val inputFieldValidator: InputFieldValidator,
    private val resourceProvider: ResourceProvider,
    private val orderItemReturnRepository: OrderItemReturnRepository,
    private val imageValidator: ImageValidator,
    private val configLogic: ConfigLogic,
    savedStateHandle: SavedStateHandle,
    private val marketplaceRepository: MarketplaceRepository,
    snackbarDelegate: SnackbarDelegate,
    private val configPriceLogic: ConfigPriceLogic,
    private val userSessionRepository: UserSessionRepository,
) : BaseViewModel<RequestItemReturnContract.Event, RequestItemReturnContract.State, RequestItemReturnContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    init {
        val route = savedStateHandle.toRoute<RequestItemReturnRoute>()
        with(route) {
            init(
                orderId = orderId,
                orderItemId = orderItemId
            )
        }
    }

    override fun setInitialState(): RequestItemReturnContract.State =
        RequestItemReturnContract.State()

    override fun handleEvents(event: RequestItemReturnContract.Event) {
        when (event) {
            RequestItemReturnContract.Event.OnClose -> onClose()
            RequestItemReturnContract.Event.OnBack -> onBack()
            is RequestItemReturnContract.Event.OnReturnReasonItemSelected -> onReturnReasonDropdownItemSelected(
                event.item
            )

            is RequestItemReturnContract.Event.OnReturnReasonOtherTextChanged -> onReturnReasonOtherTextChanged(
                event.text
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

            is RequestItemReturnContract.Event.OnReturnConditionItemSelected -> onItemConditionSelected(
                event.itemCondition
            )

            is RequestItemReturnContract.Event.OnReturnMethodItemSelected -> onReturnMethodItemSelected(
                event.item
            )

            RequestItemReturnContract.Event.OnAgreeToReturnPolicyCheckboxClicked -> onAgreeToReturnPolicyCheckboxClicked()
            RequestItemReturnContract.Event.OnReturnPolicyTextClicked -> onReturnPolicyTextClicked()

            RequestItemReturnContract.Event.Dialog.OnNegativeButtonClicked -> onDialogNegativeButtonClicked()
            RequestItemReturnContract.Event.Dialog.OnPositiveButtonClicked -> onDialogPositiveButtonClicked()

            RequestItemReturnContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()

            RequestItemReturnContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()
            RequestItemReturnContract.Event.OnBottomSheetErrorButtonClicked -> onBottomSheetErrorButtonClicked()
            RequestItemReturnContract.Event.OnBottomSheetDismissed -> onBottomSheetDismissed()

            is RequestItemReturnContract.Event.OnSellerResponseMessageChanged -> onSellerResponseMessageChanged(
                event.text
            )

            is RequestItemReturnContract.Event.OnSellerResponseActionClicked -> onSellerResponseActionClicked(
                event.action
            )
        }
    }

    // Event functions.

    private fun init(
        orderId: String,
        orderItemId: String
    ) {
        if (orderId.isBlank() || orderItemId.isBlank()) {
            setEffect {
                RequestItemReturnContract.Effect.Navigation.ExitWithError(
                    snackbarNavigationPayload = SnackbarNavigationPayload(
                        message = resourceProvider.getString(R.string.screen_request_item_return_error_loading_return_screen),
                        type = SnackbarType.ERROR
                    )
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true,
                toolbarTitle = getToolbarTitle(RequestItemReturnWizardStep.INITIAL_STEP),
                orderId = OrderIdDomain(orderId),
                orderItemId = OrderIdDomain(orderItemId),
                returnReasonOtherTextField = returnReasonOtherTextField.copy(
                    validationRules = listOf(
                        InputFieldRule.ValidateNotBlank(
                            errorMessage = resourceProvider.getString(
                                R.string.screen_request_item_return_error_return_reason_other_text_blank
                            )
                        )
                    )
                ),
                returnReasonOtherTextMaxLength = Constants.RETURN_REASON_OTHER_MAX_LENGTH,
                descriptionField = descriptionField.copy(
                    validationRules = listOf(
                        InputFieldRule.ValidateNotBlank(
                            errorMessage = resourceProvider.getString(
                                R.string.screen_request_item_return_error_description_blank
                            )
                        )
                    )
                ),
                descriptionMaxLength = Constants.DESCRIPTION_MAX_LENGTH,
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
        if (currentState.hasError) {
            setEffect { RequestItemReturnContract.Effect.Navigation.GoBack }
            return
        }

        if (currentState.dialogType != null) {
            setState {
                copy(
                    dialogType = null
                )
            }
            return
        }

        if (currentState.isBottomSheetVisible) {
            hideBottomSheet()
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
                    val previousStep = currentState.wizardStep.previousStep
                    setState {
                        copy(
                            toolbarTitle = getToolbarTitle(previousStep),
                            wizardStep = previousStep
                        )
                    }
                }
            }
        }
    }

    private fun onReturnReasonDropdownItemSelected(item: OrderItemReturnReasonUi) {
        val isOther = item == OrderItemReturnReasonUi.OTHER
        setState {
            copy(
                selectedReturnReason = item,
                returnReasonOtherTextField = if (!isOther) returnReasonOtherTextField.copy(
                    value = "",
                    validationResult = InputFieldValidationResult.Uninitialized
                ) else returnReasonOtherTextField,
                // The description field is hidden entirely for "Other" (its text lives in
                // returnReasonOtherTextField instead), so only require it otherwise.
                descriptionField = descriptionField.copy(isMandatory = !isOther)
            )
        }
    }

    private fun onReturnReasonOtherTextChanged(text: String) {
        val filteredText = text.take(currentState.returnReasonOtherTextMaxLength)
        setState {
            copy(
                returnReasonOtherTextField = returnReasonOtherTextField.copy(
                    value = filteredText,
                    validationResult = inputFieldValidator.validateInputSerially(
                        value = filteredText,
                        rules = returnReasonOtherTextField.validationRules
                    )
                )
            )
        }
    }

    private fun onRequestRefundCheckboxClicked() {
        if ((currentState.returnState as? OrderItemReturnStateUi.Available)?.isRequestRefundAvailable == false) return

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
        showErroneousSnackbar(errorMessage = resourceProvider.getString(errorStringResId))
    }

    private fun onImageSelectionFailure() {
        showErroneousSnackbar(
            errorMessage = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_failure_message),
            snackbarActionData = SnackbarActionData(
                label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_failure_button),
                actionType = SnackbarActionType.Hide
            )
        )
    }

    private fun onImageSelectionNone() {
        showInfoSnackbar(
            message = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_none_message),
            snackbarActionData = SnackbarActionData(
                label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_image_selection_none_button),
                actionType = SnackbarActionType.Hide
            )
        )
    }

    private fun onItemConditionSelected(itemCondition: OrderItemReturnConditionUi) {
        setState {
            copy(
                selectedReturnCondition = itemCondition
            )
        }
    }

    private fun onReturnMethodItemSelected(returnMethod: OrderItemReturnMethodUi) {
        setState {
            copy(
                selectedReturnMethod = returnMethod
            )
        }
    }

    private fun onAgreeToReturnPolicyCheckboxClicked() {
        setState {
            copy(
                isAgreeToReturnPolicyChecked = !isAgreeToReturnPolicyChecked
            )
        }
    }

    private fun onReturnPolicyTextClicked() {
        showBottomSheet()
        if (currentState.returnPoliciesUiState !is UiState.Success) {
            getReturnPolicies()
        }
    }

    private fun onDialogNegativeButtonClicked() {
        setState {
            copy(
                dialogType = null
            )
        }
    }

    private fun onDialogPositiveButtonClicked() {
        when (val dialogType = currentState.dialogType) {
            RequestItemReturnDialogType.Refund -> {
                setState {
                    copy(
                        dialogType = null,
                        isRequestRefundChecked = true
                    )
                }
            }

            RequestItemReturnDialogType.Exit -> {
                setState {
                    copy(
                        dialogType = null
                    )
                }
                setEffect { RequestItemReturnContract.Effect.Navigation.GoBack }
            }

            is RequestItemReturnDialogType.SellerResponse -> {
                val action = dialogType.action
                setState {
                    copy(
                        dialogType = null
                    )
                }
                submitSellerResponse(action)
            }

            null -> {}
        }
    }

    private fun onErrorButtonClicked() {
        getOrderItemReturn()
    }

    private fun onStickyBottomButtonClicked() {
        if (currentState.isLoading || currentState.hasError || currentState.wizardMode is RequestItemReturnWizardMode.Exists) return

        when (currentState.wizardStep) {
            RequestItemReturnWizardStep.RETURN_REASON,
            RequestItemReturnWizardStep.RETURN_DETAILS -> {
                val nextStep = currentState.wizardStep.nextStep
                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(nextStep),
                        wizardStep = nextStep
                    )
                }
            }

            RequestItemReturnWizardStep.OVERVIEW -> submitOrderItemReturn()
        }
    }

    private fun onBottomSheetErrorButtonClicked() {
        getReturnPolicies()
    }

    private fun onBottomSheetDismissed() {
        setState {
            copy(
                isBottomSheetVisible = false
            )
        }
    }

    // Helper functions.

    private fun getToolbarTitle(step: RequestItemReturnWizardStep): String {
        val toolbarResId = if (currentState.wizardMode == RequestItemReturnWizardMode.Create) {
            when (step) {
                RequestItemReturnWizardStep.RETURN_REASON -> R.string.screen_request_item_return_wizard_step_return_reason_toolbar_title
                RequestItemReturnWizardStep.RETURN_DETAILS -> R.string.screen_request_item_return_wizard_step_return_details_toolbar_title
                RequestItemReturnWizardStep.OVERVIEW -> R.string.screen_request_item_return_wizard_step_overview_toolbar_title
            }
        } else R.string.screen_request_item_return_toolbar_title
        return resourceProvider.getString(toolbarResId)
    }

    private fun submitOrderItemReturn() {
        val returnReason = currentState.selectedReturnReason
        val itemCondition = currentState.selectedReturnCondition
        val returnMethod = currentState.selectedReturnMethod
        if (currentState.isLoading || returnReason == null || itemCondition == null || returnMethod == null) return

        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        val description = if (returnReason == OrderItemReturnReasonUi.OTHER) {
            currentState.returnReasonOtherTextField.value
        } else {
            currentState.descriptionField.value
        }

        viewModelScope.launch {
            val result =
                orderItemReturnRepository.submitOrderItemReturn(
                    orderId = currentState.orderId.toData(),
                    orderItemId = currentState.orderItemId.toData(),
                    returnReason = returnReason.toData(),
                    requestRefund = currentState.isRequestRefundChecked,
                    attachedImages = currentState.attachedImages,
                    description = description,
                    itemCondition = itemCondition.toData(),
                    returnMethod = returnMethod.toData()
                )
            when (result) {
                is MultipartResult.GenericError,
                MultipartResult.NetworkError,
                MultipartResult.ImageProcessingError -> {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    showErroneousSnackbar(
                        errorMessage = resourceProvider.getString(R.string.screen_request_item_return_snackbar_error_submitting_item_return_message),
                        duration = SnackbarDuration.Long,
                        snackbarActionData = SnackbarActionData(
                            label = resourceProvider.getString(R.string.screen_request_item_return_snackbar_error_submitting_item_return_button),
                            actionType = SnackbarActionType.Custom(onAction = ::submitOrderItemReturn)
                        )
                    )
                }

                is MultipartResult.Success -> {
                    setEffect {
                        RequestItemReturnContract.Effect.Navigation.GoToSuccessScreen(
                            orderId = currentState.orderId.value,
                            orderItemId = currentState.orderItemId.value,
                            orderItemReturnId = result.data.value
                        )
                    }
                }
            }
        }
    }

    private fun getOrderItemReturn() {
        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        viewModelScope.launch {
            val currentUserId = userSessionRepository.getUserId()

            orderItemReturnRepository.getOrderItemReturnState(
                orderId = currentState.orderId.toData(),
                orderItemId = currentState.orderItemId.toData()
            )
                .onSuccess { returnState ->
                    returnState.toUi(configPriceLogic.priceScale)
                        .onSuccess { returnStateUi ->
                            val sellerUserId = when (returnStateUi) {
                                is OrderItemReturnStateUi.Available -> returnStateUi.orderItem.sellerUserId
                                is OrderItemReturnStateUi.Existing -> returnStateUi.orderItemReturn.orderItem.sellerUserId
                            }
                            setState {
                                copy(
                                    isLoading = false,
                                    returnState = returnStateUi,
                                    isSeller = sellerUserId == currentUserId,
                                    wizardMode = if (returnStateUi is OrderItemReturnStateUi.Available) RequestItemReturnWizardMode.Create else RequestItemReturnWizardMode.Exists,
                                    wizardStep = RequestItemReturnWizardStep.INITIAL_STEP
                                )
                            }
                        }
                        .onFailure {
                            setState {
                                copy(
                                    isLoading = false,
                                    hasError = true
                                )
                            }
                        }
                }
                .onError {
                    setState {
                        copy(
                            isLoading = false,
                            hasError = true
                        )
                    }
                }
        }
    }

    private fun onSellerResponseMessageChanged(text: String) {
        setState {
            copy(
                sellerResponseMessageField = sellerResponseMessageField.copy(value = text)
            )
        }
    }

    private fun onSellerResponseActionClicked(action: OrderItemReturnSellerActionUi) {
        if (currentState.isSubmittingSellerResponse) return

        setState {
            copy(dialogType = RequestItemReturnDialogType.SellerResponse(action))
        }
    }

    private fun submitSellerResponse(action: OrderItemReturnSellerActionUi) {
        if (currentState.isSubmittingSellerResponse) return
        val orderItemReturn =
            (currentState.returnState as? OrderItemReturnStateUi.Existing)?.orderItemReturn ?: return

        setState {
            copy(
                isSubmittingSellerResponse = true
            )
        }
        viewModelScope.launch {
            orderItemReturnRepository.respondToOrderItemReturn(
                orderItemReturnId = orderItemReturn.orderItemReturnId,
                action = action.toData(),
                sellerMessage = currentState.sellerResponseMessageField.value.ifBlank { null }
            )
                .onSuccess { updatedOrderItemReturn ->
                    updatedOrderItemReturn.toUi(configPriceLogic.priceScale)
                        .onSuccess { updatedOrderItemReturnUi ->
                            setState {
                                copy(
                                    isSubmittingSellerResponse = false,
                                    returnState = OrderItemReturnStateUi.Existing(updatedOrderItemReturnUi),
                                    sellerResponseMessageField = sellerResponseMessageField.copy(value = "")
                                )
                            }
                            showSuccessSnackbar(
                                message = resourceProvider.getString(R.string.screen_request_item_return_seller_response_snackbar_success_message)
                            )
                        }
                        .onFailure {
                            setState {
                                copy(
                                    isSubmittingSellerResponse = false
                                )
                            }
                            showErroneousSnackbar(
                                errorMessage = resourceProvider.getString(R.string.screen_request_item_return_seller_response_snackbar_error_message)
                            )
                        }
                }
                .onError {
                    setState {
                        copy(
                            isSubmittingSellerResponse = false
                        )
                    }
                    showErroneousSnackbar(
                        errorMessage = resourceProvider.getString(R.string.screen_request_item_return_seller_response_snackbar_error_message)
                    )
                }
        }
    }

    private fun getReturnPolicies() {
        setState {
            copy(
                returnPoliciesUiState = UiState.Loading
            )
        }
        viewModelScope.launch {
            marketplaceRepository.getReturnPolicies()
                .onSuccess { returnPolicies ->
                    setState {
                        copy(
                            returnPoliciesUiState = UiState.Success(returnPolicies)
                        )
                    }
                }
                .onError {
                    setState {
                        copy(
                            returnPoliciesUiState = UiState.Error(Unit)
                        )
                    }
                }
        }
    }

    private fun showBottomSheet() {
        setState {
            copy(
                isBottomSheetVisible = true
            )
        }
        setEffect { RequestItemReturnContract.Effect.ShowBottomSheet }
    }

    private fun hideBottomSheet() {
        setState {
            copy(
                isBottomSheetVisible = false
            )
        }
        setEffect { RequestItemReturnContract.Effect.HideBottomSheet }
    }

    private object Constants {
        const val RETURN_REASON_OTHER_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 500
    }
}