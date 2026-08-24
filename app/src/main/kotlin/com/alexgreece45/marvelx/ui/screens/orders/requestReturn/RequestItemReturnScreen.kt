package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.components.statusBox.InfoContainer
import com.alexgreece45.marvelx.common_ui.components.statusBox.StatusContainer
import com.alexgreece45.marvelx.common_ui.previewAnnotation.ThemePreview
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEffect
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.common_ui.util.safeLetComposable
import com.alexgreece45.marvelx.core.common.extensions.defaultFormat
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.data.model.order.item.ReturnPolicy
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.LoadingScreen
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapClickableText
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.WrapImage
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.components.listing.ListingPriceLabel
import com.alexgreece45.marvelx.ui.components.model.ClickableString
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderRow
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.components.textInputField.TextInputField
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderIdsLabel
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnConditionUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnMethodUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStateUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardMode
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardStep
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.formatPrice
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.showIfHidden
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import com.alexgreece45.marvelx.util.extensions.withEuroSymbol
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.random.Random

private val ATTACHED_IMAGE_SIZE = ConstantsUi.LISTING_IMAGE_BIG_SIZE
private val verticalItemPadding = 20.dp

@Composable
internal fun RequestItemReturnScreen(
    onEvent: (RequestItemReturnContract.Event) -> Unit,
    state: RequestItemReturnContract.State,
    effect: SingleObserverFlow<RequestItemReturnContract.Effect>,
    onNavigationEffect: (RequestItemReturnContract.Effect.Navigation) -> Unit,
    snackbarEffects: SingleObserverFlow<SnackbarEffect>
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isEmpty()) {
                onEvent(RequestItemReturnContract.Event.ImageSelectionEvents.OnNoSelection)
            } else {
                val allFilesContentData = mutableListOf<AttachedFileContentData>()
                uris.forEach { uri ->
                    val fileContentData = FileUtil.getFileContentData(
                        uri = uri,
                        context = context
                    )

                    if (fileContentData == null) {
                        onEvent(RequestItemReturnContract.Event.ImageSelectionEvents.OnFailure)
                        return@rememberLauncherForActivityResult
                    } else {
                        allFilesContentData.add(fileContentData)
                    }
                }

                onEvent(
                    RequestItemReturnContract.Event.ImageSelectionEvents.OnSuccess(
                        allFilesContentData
                    )
                )
            }
        }
    )

    ObserveUiEffect(
        flow = effect,
        tag = "RequestItemReturnScreen"
    ) { uiEffect ->
        when (uiEffect) {
            is RequestItemReturnContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            RequestItemReturnContract.Effect.OpenPhotoPicker -> {
                multiPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            RequestItemReturnContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.showIfHidden()
                }
            }

            RequestItemReturnContract.Effect.HideBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }
        }
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            if (state.isBottomSheetVisible) {
                ReturnPolicyBottomSheetContent(
                    returnPoliciesUiState = state.returnPoliciesUiState,
                    onErrorButtonClick = {
                        onEvent(
                            RequestItemReturnContract.Event.OnBottomSheetErrorButtonClicked
                        )
                    }
                )
            }
        },
        sheetState = modalBottomSheetState,
        onDismissed = { onEvent(RequestItemReturnContract.Event.OnBottomSheetDismissed) }
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = if (state.wizardMode is RequestItemReturnWizardMode.Exists
                || state.wizardStep != RequestItemReturnWizardStep.RETURN_REASON
            ) {
                ScreenNavigableAction.Backable(
                    onBack = { onEvent(RequestItemReturnContract.Event.OnBack) }
                )
            } else {
                ScreenNavigableAction.Closeable(
                    onClose = { onEvent(RequestItemReturnContract.Event.OnClose) }
                )
            },
            onBack = { onEvent(RequestItemReturnContract.Event.OnBack) },
            showToolbar = true,
            toolbarTitle = state.toolbarTitle,
            snackbarEffects = snackbarEffects,
            stickyBottom = {
                if (state.isStickyBottomButtonVisible) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                    ) {
                        WrapButton(
                            onClick = { onEvent(RequestItemReturnContract.Event.OnStickyBottomButtonClicked) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.isStickyBottomButtonEnabled
                        ) {
                            Text(text = stringResource(if (state.wizardStep != RequestItemReturnWizardStep.OVERVIEW) R.string.screen_request_item_return_sticky_bottom_button_next else R.string.screen_request_item_return_sticky_bottom_button_submit))
                        }
                    }
                }
            }
        ) { paddingValues ->
            val layoutDirection = LocalLayoutDirection.current

            Column(modifier = Modifier.fillMaxSize()) {
                if (state.isLinearProgressIndicatorVisible) {
                    LinearProgressIndicator(
                        progress = state.progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.customColors.genericRed
                    )
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            top = paddingValues.calculateTopPadding(),
                            end = paddingValues.calculateEndPadding(layoutDirection)
                        )
                ) {
                    val maxHeight = maxHeight
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .heightIn(min = maxHeight),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                        ) {
                            state.dialogType?.let { dialogType ->
                                AlertDialogError(
                                    isVisible = true,
                                    title = stringResource(dialogType.titleResId),
                                    errorMessage = {
                                        Text(
                                            text = stringResource(dialogType.messageResId)
                                        )
                                    },
                                    confirmButton = AlertDialogButton(
                                        label = stringResource(dialogType.confirmButtonLabelResId),
                                        buttonType = ErrorButtonType.Contained,
                                        onClick = {
                                            onEvent(RequestItemReturnContract.Event.Dialog.OnPositiveButtonClicked)
                                        }
                                    ),
                                    dismissButton = AlertDialogButton(
                                        label = stringResource(dialogType.dismissButtonLabelResId),
                                        onClick = {
                                            onEvent(RequestItemReturnContract.Event.Dialog.OnNegativeButtonClicked)
                                        }
                                    )
                                )
                            }

                            when {
                                state.isLoading -> {}

                                state.hasError -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(maxHeight),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        GenericErrorScreen(
                                            errorMessage = stringResource(R.string.screen_request_item_return_generic_error_message),
                                            onErrorButtonClick = {
                                                onEvent(RequestItemReturnContract.Event.OnErrorButtonClicked)
                                            }
                                        )
                                    }
                                }

                                state.returnState != null -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        when (val returnState = state.returnState) {
                                            null -> {
                                                // Should never occur, but just in case.
                                            }

                                            is OrderItemReturnStateUi.Available -> {
                                                WizardContent(
                                                    state = state,
                                                    orderItem = returnState.orderItem,
                                                    returnUntilDate = returnState.returnUntilDate,
                                                    returnUntilRemainingDays = returnState.returnUntilRemainingDays,
                                                    isRequestRefundAvailable = returnState.isRequestRefundAvailable,
                                                    onEvent = onEvent
                                                )
                                            }

                                            is OrderItemReturnStateUi.Existing -> {
                                                ExistingReturn(returnState.orderItemReturn)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Return policy checkbox section. Always at the bottom of the screen.
                        if (state.wizardMode is RequestItemReturnWizardMode.Create
                            && state.wizardStep == RequestItemReturnWizardStep.OVERVIEW
                        ) {
                            VSpacer.Custom(verticalItemPadding)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = SPACING_SMALL.dp,
                                            vertical = SPACING_MEDIUM.dp
                                        )
                                        .clickableNoRipple(
                                            onClick = {
                                                onEvent(
                                                    RequestItemReturnContract.Event.OnAgreeToReturnPolicyCheckboxClicked
                                                )
                                            }
                                        ),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        SPACING_SMALL.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = state.isAgreeToReturnPolicyChecked,
                                        onCheckedChange = {
                                            onEvent(
                                                RequestItemReturnContract.Event.OnAgreeToReturnPolicyCheckboxClicked
                                            )
                                        }
                                    )

                                    WrapClickableText(
                                        text = stringResource(R.string.screen_request_item_return_sticky_bottom_checkbox_label),
                                        textStyle = LocalTextStyle.current,
                                        clickableStrings = listOf(
                                            ClickableString(
                                                text = stringResource(R.string.screen_request_item_return_sticky_bottom_checkbox_label_clickable_text),
                                                color = MaterialTheme.customColors.info,
                                                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                                fontWeight = FontWeight.Medium,
                                                onClick = {
                                                    onEvent(
                                                        RequestItemReturnContract.Event.OnReturnPolicyTextClicked
                                                    )
                                                }
                                            )
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//region Wizard Composables

@Composable
private fun WizardContent(
    state: RequestItemReturnContract.State,
    orderItem: OrderItemUi,
    returnUntilDate: LocalDateUi,
    returnUntilRemainingDays: Long,
    isRequestRefundAvailable: Boolean,
    onEvent: (RequestItemReturnContract.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(verticalItemPadding)
    ) {
        when (state.wizardStep) {
            RequestItemReturnWizardStep.RETURN_REASON -> {
                WizardScreenReturnReason(
                    orderId = state.orderId,
                    orderItem = orderItem,
                    returnUntilRemainingDays = returnUntilRemainingDays,
                    returnUntilDate = returnUntilDate,
                    returnReasons = state.returnReasons,
                    selectedReturnReason = state.selectedReturnReason,
                    returnReasonOtherTextField = state.returnReasonOtherTextField,
                    returnReasonOtherTextMaxLength = state.returnReasonOtherTextMaxLength,
                    isRequestRefundAvailable = isRequestRefundAvailable,
                    isRequestRefundChecked = state.isRequestRefundChecked,
                    onReturnReasonItemSelect = {
                        onEvent(
                            RequestItemReturnContract.Event.OnReturnReasonItemSelected(
                                it
                            )
                        )
                    },
                    onReturnReasonOtherTextChange = {
                        onEvent(
                            RequestItemReturnContract.Event.OnReturnReasonOtherTextChanged(
                                it
                            )
                        )
                    },
                    onRequestRefundCheckboxClick = {
                        onEvent(
                            RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked
                        )
                    }
                )
            }

            RequestItemReturnWizardStep.RETURN_DETAILS -> {
                WizardScreenReturnDetails(
                    isReturnReasonOther = state.selectedReturnReason == OrderItemReturnReasonUi.OTHER,
                    descriptionField = state.descriptionField,
                    maxDescriptionLength = state.descriptionMaxLength,
                    attachedImages = state.attachedImages,
                    maxImageCount = state.imageValidationConfig.maxCount,
                    maxImageSizeMb = state.imageValidationConfig.maxSizeMB,
                    returnConditions = state.returnConditions,
                    selectedReturnCondition = state.selectedReturnCondition,
                    onDescriptionChange = {
                        onEvent(
                            RequestItemReturnContract.Event.OnDescriptionChanged(
                                it
                            )
                        )
                    },
                    onDeleteImageButtonClick = {
                        onEvent(
                            RequestItemReturnContract.Event.OnImageDeleteButtonClicked(
                                it
                            )
                        )
                    },
                    onUploadPhotosButtonClick = { onEvent(RequestItemReturnContract.Event.OnUploadPhotosButtonClicked) },
                    onReturnConditionSelect = {
                        onEvent(
                            RequestItemReturnContract.Event.OnReturnConditionItemSelected(
                                it
                            )
                        )
                    }
                )
            }

            RequestItemReturnWizardStep.OVERVIEW -> {
                safeLetComposable(
                    state.selectedReturnReason,
                    state.selectedReturnCondition
                ) { returnReason, returnCondition ->
                    WizardScreenOverview(
                        orderId = state.orderId,
                        orderItem = orderItem,
                        returnReason = returnReason,
                        isRefundRequested = state.isRequestRefundChecked,
                        returnCondition = returnCondition,
                        attachedImagesCount = state.attachedImages.size,
                        returnMethods = state.returnMethods,
                        selectedReturnMethod = state.selectedReturnMethod,
                        onReturnMethodSelect = {
                            onEvent(
                                RequestItemReturnContract.Event.OnReturnMethodItemSelected(
                                    it
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WizardScreenReturnReason(
    orderId: OrderIdDomain,
    orderItem: OrderItemUi,
    returnUntilRemainingDays: Long,
    returnUntilDate: LocalDateUi,
    returnReasons: List<OrderItemReturnReasonUi>,
    selectedReturnReason: OrderItemReturnReasonUi?,
    returnReasonOtherTextField: InputFieldUi.Text,
    returnReasonOtherTextMaxLength: Int,
    isRequestRefundAvailable: Boolean,
    isRequestRefundChecked: Boolean,
    onReturnReasonItemSelect: (OrderItemReturnReasonUi) -> Unit,
    onReturnReasonOtherTextChange: (String) -> Unit,
    onRequestRefundCheckboxClick: () -> Unit
) {
    OrderIdsLabel(
        orderId = orderId,
        orderItemId = orderItem.orderItemId
    )

    OrderItemPreviewCard(
        title = orderItem.title,
        coverImageUrl = orderItem.coverImageUrl,
        priceLabel = orderItem.price.label
    )

    StatusContainer(
        title = stringResource(
            R.string.screen_request_item_return_return_reason_warning_box_title,
            returnUntilRemainingDays
        ),
        titleColor = MaterialTheme.customColors.warningStatusContainerColors.contentColor,
        backgroundColor = MaterialTheme.customColors.warningStatusContainerColors.backgroundColor,
        modifier = Modifier.fillMaxWidth(),
        outlineColor = MaterialTheme.customColors.warningStatusContainerColors.outlineColor,
        icon = {
            WrapImage(
                painter = painterResource(R.drawable.ic_alarm),
                contentDescription = null,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
            )
        },
        extraContent = {
            Text(
                text = stringResource(
                    R.string.screen_request_item_return_return_reason_warning_box_description,
                    returnUntilDate.label
                ),
                style = MaterialTheme.typography.subtitle1
            )
        }
    )

    ReturnItemsSection(
        sectionTitle = stringResource(R.string.screen_request_item_return_return_reason_label),
        items = returnReasons,
        itemLabel = { item -> stringResource(item.titleResId) },
        descriptionLabel = { item -> stringResource(item.descriptionResId) },
        selectedItem = selectedReturnReason,
        shouldShowExtraItemContent = { item -> item == OrderItemReturnReasonUi.OTHER },
        onItemSelect = onReturnReasonItemSelect,
        extraItemContent = {
            ReturnReasonDescriptionTextField(
                inputField = returnReasonOtherTextField,
                maxTextLength = returnReasonOtherTextMaxLength,
                labelContent = {
                    Text(
                        text = stringResource(R.string.screen_request_item_return_return_reason_other_textfield_label),
                        style = MaterialTheme.typography.subtitle1
                    )
                },
                onTextChange = onReturnReasonOtherTextChange
            )
        },
        extraContent = {
            Column {
                RequestRefundCheckbox(
                    checked = isRequestRefundChecked,
                    enabled = isRequestRefundAvailable,
                    onClick = onRequestRefundCheckboxClick
                )

                if (!isRequestRefundAvailable) {
                    InfoContainer(
                        title = stringResource(R.string.screen_request_item_return_checkbox_request_refund_not_available_message),
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
private fun WizardScreenReturnDetails(
    isReturnReasonOther: Boolean,
    descriptionField: InputFieldUi.Text,
    maxDescriptionLength: Int,
    attachedImages: List<AttachedFileContentData>,
    maxImageCount: Int,
    maxImageSizeMb: Int,
    returnConditions: ImmutableList<OrderItemReturnConditionUi>,
    selectedReturnCondition: OrderItemReturnConditionUi?,
    onDescriptionChange: (String) -> Unit,
    onDeleteImageButtonClick: (Uri) -> Unit,
    onUploadPhotosButtonClick: () -> Unit,
    onReturnConditionSelect: (OrderItemReturnConditionUi) -> Unit
) {
    if (!isReturnReasonOther) {
        DescriptionSection(
            descriptionField = descriptionField,
            maxDescriptionLength = maxDescriptionLength,
            labelContent = {
                SectionHeaderTitle(
                    title = stringResource(R.string.screen_request_item_return_description_title)
                )
            },
            onDescriptionChange = onDescriptionChange
        )
    }

    UploadImagesSection(
        attachedImages = attachedImages,
        maxImageCount = maxImageCount,
        maxImageSizeMb = maxImageSizeMb,
        onDeleteImageButtonClick = onDeleteImageButtonClick,
        onUploadPhotosButtonClick = onUploadPhotosButtonClick
    )

    // Return conditions.
    ReturnItemsSection(
        sectionTitle = stringResource(R.string.screen_request_item_return_return_condition_label),
        items = returnConditions,
        itemLabel = { item -> stringResource(item.titleResId) },
        descriptionLabel = { item -> stringResource(item.descriptionResId) },
        selectedItem = selectedReturnCondition,
        onItemSelect = onReturnConditionSelect
    )

    // Return Requirements
    StatusContainer(
        title = stringResource(R.string.screen_request_item_return_return_details_requirements_title),
        titleColor = MaterialTheme.customColors.errorStatusContainerColors.contentColor,
        backgroundColor = MaterialTheme.customColors.errorStatusContainerColors.backgroundColor,
        modifier = Modifier.fillMaxWidth(),
        outlineColor = MaterialTheme.customColors.errorStatusContainerColors.outlineColor,
        icon = {
            WrapImage(
                painter = painterResource(UiThemeDrawable.ic_warning),
                contentDescription = null
            )
        },
        verticalAlignment = Alignment.Bottom,
        extraContent = {
            Column {
                BulletListItem(label = stringResource(R.string.screen_request_item_return_return_details_requirements_label_1))
                BulletListItem(label = stringResource(R.string.screen_request_item_return_return_details_requirements_label_2))
                BulletListItem(label = stringResource(R.string.screen_request_item_return_return_details_requirements_label_3))
            }
        }
    )

    MandatoryCharacterLabel()
}

@Composable
private fun WizardScreenOverview(
    orderId: OrderIdDomain,
    orderItem: OrderItemUi,
    returnReason: OrderItemReturnReasonUi,
    isRefundRequested: Boolean,
    returnCondition: OrderItemReturnConditionUi,
    attachedImagesCount: Int,
    returnMethods: ImmutableList<OrderItemReturnMethodUi>,
    selectedReturnMethod: OrderItemReturnMethodUi?,
    onReturnMethodSelect: (OrderItemReturnMethodUi) -> Unit
) {
    OrderIdsLabel(
        orderId = orderId,
        orderItemId = orderItem.orderItemId
    )

    ListingCard(
        title = orderItem.title,
        coverImageUrl = orderItem.coverImageUrl,
        additionalTitleContent = { ListingPriceLabel(orderItem.price.label) },
        additionalContent = {
            VSpacer.Medium()

            WrapHorizontalDivider()

            VSpacer.Medium()

            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_overview_choice_return),
                    endLabel = stringResource(returnReason.titleResId)
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_overview_choice_condition),
                    endLabel = stringResource(returnCondition.titleResId)
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_overview_choice_attached_images),
                    endLabel = stringResource(
                        R.string.screen_request_item_return_overview_choice_attached_images_count,
                        attachedImagesCount
                    )
                )
            }
        },
        trailingIcon = null,
        enabled = false,
        onClicked = {}
    )

    ReturnItemsSection(
        sectionTitle = stringResource(R.string.screen_request_item_return_method_label),
        items = returnMethods,
        itemLabel = { item -> stringResource(item.titleResId) },
        descriptionLabel = { item -> stringResource(item.descriptionResId) },
        selectedItem = selectedReturnMethod,
        onItemSelect = onReturnMethodSelect,
    )

    // Expected Refund
    if (isRefundRequested) {
        SectionHeader(
            title = stringResource(R.string.screen_request_item_overview_refund_box_label),
            additionalTitleContent = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    OverviewSectionExpectedRefundBoxRowItem(
                        title = stringResource(R.string.screen_request_item_overview_refund_box_subtotal_label),
                        description = "130.58".withEuroSymbol()
                    )

                    OverviewSectionExpectedRefundBoxRowItem(
                        title = stringResource(R.string.screen_request_item_overview_refund_box_return_shipping_label),
                        description = "-2.7".withEuroSymbol()
                    )

                    WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.screen_request_item_overview_refund_box_total_refund_label),
                            style = MaterialTheme.typography.h5
                        )

                        Text(
                            text = "133.28".withEuroSymbol(),
                            style = MaterialTheme.typography.h5
                        )
                    }
                }
            },
            additionalContent = {
                VSpacer.Medium()

                Text(text = stringResource(R.string.screen_request_item_overview_refund_box_total_refund_description))
            },
            enabled = false,
            onClick = {}
        )
    }
}

@Composable
private fun OrderItemPreviewCard(
    title: String,
    coverImageUrl: String,
    priceLabel: String,
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = { ListingPriceLabel(priceLabel) },
        trailingIcon = null,
        additionalContent = null,
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun <T> ReturnItemsSection(
    sectionTitle: String,
    items: List<T>,
    itemLabel: @Composable ((item: T) -> String),
    descriptionLabel: @Composable ((item: T) -> String),
    selectedItem: T?,
    shouldShowExtraItemContent: (item: T) -> Boolean = { false },
    onItemSelect: (T) -> Unit,
    extraItemContent: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeaderTitle(sectionTitle)
            MandatoryCharacter()
        }

        items.forEach { item ->
            ReturnReasonItem(
                label = itemLabel.invoke(item),
                description = descriptionLabel.invoke(item),
                isSelected = item == selectedItem,
                onSelect = { onItemSelect(item) },
                extraContent = if (shouldShowExtraItemContent(item)) extraItemContent else null
            )
        }

        extraContent?.invoke()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReturnReasonItem(
    label: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    extraContent: (@Composable () -> Unit)? = null
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACING_SMALL.dp),
                horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = isSelected, onClick = onSelect)

                Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                    SectionHeaderTitle(title = label)

                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            extraContent?.let {
                AnimatedVisibility(
                    visible = isSelected,
                    modifier = Modifier.padding(
                        start = SPACING_MEDIUM.dp,
                        end = SPACING_MEDIUM.dp,
                        bottom = SPACING_MEDIUM.dp
                    ),
                    content = { extraContent() }
                )
            }
        }
    }
}

@Composable
private fun RequestRefundCheckbox(checked: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickableNoRipple(
            role = Role.Checkbox,
            onClick = onClick
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { onClick() },
        )

        Text(
            text = stringResource(R.string.screen_request_item_return_checkbox_request_refund),
            color = LocalContentColor.current.copy(alpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled),
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun MandatoryCharacterLabel() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MandatoryCharacter(fontStyle = FontStyle.Italic)

        Text(
            text = stringResource(R.string.screen_request_item_return_mandatory_field_label),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Composable
private fun MandatoryCharacter(
    fontStyle: FontStyle? = null,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = "*",
        color = MaterialTheme.customColors.genericRed,
        fontStyle = fontStyle,
        style = style
    )
}

@Composable
private fun DescriptionSection(
    descriptionField: InputFieldUi.Text,
    maxDescriptionLength: Int,
    labelContent: @Composable () -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    ReturnReasonDescriptionTextField(
        inputField = descriptionField,
        maxTextLength = maxDescriptionLength,
        labelContent = labelContent,
        onTextChange = onDescriptionChange
    )
}

@Composable
private fun ReturnReasonDescriptionTextField(
    inputField: InputFieldUi.Text,
    maxTextLength: Int,
    labelContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
) {
    Column {
        Box(
            modifier = Modifier.padding(
                start = SPACING_SMALL.dp,
                top = SPACING_SMALL.dp,
                end = SPACING_SMALL.dp
            ),
            content = { labelContent() }
        )

        TextInputField(
            inputField = inputField,
            label = "",
            placeholder = stringResource(R.string.screen_request_item_return_description_placeholder),
            helperText = stringResource(
                R.string.screen_request_item_return_description_remaining_characters_label,
                inputField.value.length,
                maxTextLength
            ),
            modifier = modifier,
            onValueChanged = onTextChange,
            minLines = 4,
            maxLines = 8
        )
    }
}

@Composable
private fun UploadImagesSection(
    attachedImages: List<AttachedFileContentData>,
    maxImageCount: Int,
    maxImageSizeMb: Int,
    onDeleteImageButtonClick: (Uri) -> Unit,
    onUploadPhotosButtonClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
            SectionHeaderTitle(stringResource(R.string.screen_request_item_return_upload_images_title))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ATTACHED_IMAGE_SIZE),
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                itemsIndexed(
                    items = attachedImages,
                    key = { _, attachedImage -> attachedImage.uri }
                ) { index, attachedImage ->
                    AttachedImageItem(
                        imageUrl = attachedImage.uri.toString(),
                        title = stringResource(
                            R.string.screen_request_item_return_uploaded_image_content_description,
                            (index + 1)
                        ),
                        onDeleteIconClick = { onDeleteImageButtonClick(attachedImage.uri) }
                    )
                }

                item(key = "uploadPhotosButton") {
                    Box(
                        modifier = Modifier
                            .size(size = ATTACHED_IMAGE_SIZE)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.customColors.genericRed,
                                shape = MaterialTheme.shapes.medium
                            )
                            .throttledClickable(
                                view = LocalView.current,
                                onClick = onUploadPhotosButtonClick,
                                showRipple = false
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = stringResource(R.string.screen_request_item_return_upload_image_button_content_description)
                            )

                            Text(
                                text = stringResource(R.string.screen_request_item_return_upload_images_button),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.customColors.info) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(UiThemeDrawable.ic_info),
                    contentDescription = null
                )

                Text(
                    text = stringResource(
                        R.string.screen_request_item_return_upload_images_helper_text,
                        maxImageCount,
                        maxImageSizeMb
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AttachedImageItem(
    imageUrl: String,
    title: String,
    onDeleteIconClick: () -> Unit
) {
    val alignmentOffset = ((48.dp - SPACING_MEDIUM.dp) / 2 - SPACING_SMALL.dp).coerceAtLeast(0.dp)

    Box(modifier = Modifier.size(ATTACHED_IMAGE_SIZE)) {
        WrapAsyncImage(
            imageUrl = imageUrl,
            contentDescription = title,
            modifier = Modifier.size(ATTACHED_IMAGE_SIZE),
            boxModifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.customColors.white),
            contentScale = ContentScale.Crop
        )

        ThrottledIconButton(
            onClick = onDeleteIconClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = alignmentOffset, y = -alignmentOffset)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.screen_request_item_return_remove_image_button_content_description),
                modifier = Modifier.background(
                    color = MaterialTheme.colors.background,
                    shape = CircleShape
                )
            )
        }
    }
}

@Composable
private fun OverviewSectionExpectedRefundBoxRowItem(title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)

        Text(text = description)
    }
}

@Composable
private fun BulletListItem(label: String) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = MaterialTheme.customColors.white,
            style = MaterialTheme.typography.subtitle1.copy(fontSize = 20.sp)
        )

        Text(
            text = label,
            modifier = Modifier.fillMaxHeight(),
            color = MaterialTheme.customColors.white,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun <T> ReturnPolicyBottomSheetContent(
    returnPoliciesUiState: UiState<ImmutableList<ReturnPolicy>, T>,
    onErrorButtonClick: () -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Swallow any leftover drag so scrolling the policy list never
                // triggers the bottom sheet's own swipe-to-dismiss gesture.
                return available
            }
        }
    }

    ModalBottomSheetContent {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_bottom_sheet_title))

            WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

            when (returnPoliciesUiState) {
                UiState.Loading -> Box(modifier = Modifier.height(IntrinsicSize.Min)) {
                    LoadingScreen()
                }

                is UiState.Error -> GenericErrorScreen(
                    errorMessage = stringResource(R.string.screen_request_item_return_bottom_sheet_error_loading),
                    modifier = Modifier.height(IntrinsicSize.Min),
                    onErrorButtonClick = onErrorButtonClick
                )

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nestedScroll(nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                    ) {
                        items(items = returnPoliciesUiState.data) { returnPolicy ->
                            ReturnPolicyItem(returnPolicy)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReturnPolicyItem(returnPolicy: ReturnPolicy) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        Text(
            text = returnPolicy.header,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = returnPolicy.description,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
        )

        if (returnPolicy.items.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(start = SPACING_SMALL.dp, top = SPACING_TINY.dp),
                verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
            ) {
                returnPolicy.items.forEachIndexed { index, item ->
                    Row(horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                        )

                        Text(
                            text = item,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
//endregion

//region Existing Return Composables

@Composable
private fun ExistingReturn(orderItemReturn: OrderItemReturnUi) {

}

//endregion

@ThemePreview
@Composable
private fun ScreenPreview() {
    MarvelXTheme {
        RequestItemReturnScreen(
            onEvent = {},
            state = RequestItemReturnContract.State(
                isLoading = false,
                returnState = OrderItemReturnStateUi.Available(
                    orderItem = OrderItemUi(
                        orderItemId = OrderIdDomain("100"),
                        sellerUserId = "1",
                        title = "Wow cup",
                        coverImageUrl = "",
                        status = OrderItemStatusUi.Delivered,
                        price = BigDecimal.valueOf(Random.nextDouble(5.0, 100.0)).let {
                            BigDecimalUi(
                                value = it,
                                label = it.formatPrice(2)
                            )
                        },
                    ),
                    returnUntilDate = DateUtil.getCurrentLocalDate()
                        .let { LocalDateUi(value = it, label = it.defaultFormat()) },
                    isRequestRefundAvailable = true
                ),
                wizardMode = RequestItemReturnWizardMode.Create,
                wizardStep = RequestItemReturnWizardStep.OVERVIEW,
                toolbarTitle = stringResource(R.string.screen_request_item_return_wizard_step_overview_toolbar_title),
                orderId = OrderIdDomain("1"),
                orderItemId = OrderIdDomain("100"),
                selectedReturnReason = OrderItemReturnReasonUi.NOT_AS_DESCRIBED,
                isRequestRefundChecked = true,
                attachedImages = listOf(
                    AttachedFileContentData(
                        uri = "".toUri(),
                        name = "Wow cup",
                        bytesCount = 2000
                    )
                ),
                selectedReturnCondition = OrderItemReturnConditionUi.LIGHTLY_USED,
                returnPoliciesUiState = UiState.Success(
                    persistentListOf(
                        ReturnPolicy(
                            header = "return requirements",
                            description = "Detailed return requirements.",
                            items = persistentListOf()
                        )
                    )
                ),
                isBottomSheetVisible = true
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {},
            snackbarEffects = SingleObserverFlow(emptyFlow())
        )
    }
}