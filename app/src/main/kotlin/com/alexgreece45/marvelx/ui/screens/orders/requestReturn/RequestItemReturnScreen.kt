package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.hideSnackbar
import com.alexgreece45.marvelx.common_ui.extensions.showSnackbarAndHandleResult
import com.alexgreece45.marvelx.common_ui.previewAnnotation.ThemePreview
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarConfig
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnWizardStep
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private val ATTACHED_IMAGE_SIZE = ConstantsUi.LISTING_IMAGE_BIG_SIZE

@Composable
internal fun RequestItemReturnScreen(
    onEvent: (RequestItemReturnContract.Event) -> Unit,
    state: RequestItemReturnContract.State,
    effect: Flow<RequestItemReturnContract.Effect>,
    onNavigationEffect: (RequestItemReturnContract.Effect.Navigation) -> Unit,
    orderId: String,
    orderItemId: String,
    orderItemTitle: String,
    orderItemCoverImageUrl: String,
    orderItemPriceLabel: String
) {

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarStateData by remember { mutableStateOf<SnackbarStateData?>(null) }

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

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is RequestItemReturnContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            RequestItemReturnContract.Effect.OpenPhotoPicker -> {
                multiPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            is RequestItemReturnContract.Effect.ShowSnackbar -> {
                coroutineScope.launch {
                    snackbarStateData = uiEffect.snackbarStateData
                    snackbarHostState.showSnackbarAndHandleResult(uiEffect.snackbarStateData)
                }
            }

            RequestItemReturnContract.Effect.HideSnackbar -> {
                snackbarHostState.hideSnackbar()
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(
            RequestItemReturnContract.Event.Init(
                orderId = orderId,
                orderItemId = orderItemId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = orderItemCoverImageUrl,
                orderItemPriceLabel = orderItemPriceLabel
            )
        )
    }

    ContentScreen(
        isLoading = state.uiState.isLoading,
        navigableAction = if (state.wizardStep == RequestItemReturnWizardStep.RETURN_REASON) {
            ScreenNavigableAction.Closeable(
                onClose = { onEvent(RequestItemReturnContract.Event.OnClose) }
            )
        } else {
            ScreenNavigableAction.Backable(
                onBack = { onEvent(RequestItemReturnContract.Event.OnBack) }
            )
        },
        onBack = { onEvent(RequestItemReturnContract.Event.OnBack) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_request_item_return_toolbar_title),
        snackbarConfig = SnackbarConfig(
            snackbarHostState = snackbarHostState,
            snackbarStateData = snackbarStateData
        ),
        stickyBottom = {
            WrapButton(
                onClick = { onEvent(RequestItemReturnContract.Event.OnStickyBottomButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSubmitButtonEnabled
            ) {
                Text(text = stringResource(R.string.screen_request_return_submit_button))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
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
                            onEvent(RequestItemReturnContract.Event.RefundDialogEvent.OnPositiveButtonClicked)
                        }
                    ),
                    dismissButton = AlertDialogButton(
                        label = stringResource(dialogType.dismissButtonLabelResId),
                        onClick = {
                            onEvent(RequestItemReturnContract.Event.RefundDialogEvent.OnNegativeButtonClicked)
                        }
                    )
                )
            }

            when (val uiStateData = state.uiState) {
                UiState.Loading -> {}

                is UiState.Error -> {
                    GenericErrorScreen(
                        errorMessage = stringResource(R.string.screen_request_item_return_generic_error_message),
                        modifier = Modifier.fillMaxSize(),
                        buttonLabel = stringResource(R.string.screen_request_item_return_generic_error_button),
                        onErrorButtonClicked = {
                            onEvent(RequestItemReturnContract.Event.OnErrorButtonClicked)
                        }
                    )
                }

                is UiState.Success -> {
                    when (val returnState = uiStateData.data) {
                        OrderItemReturnState.Available -> {
                            WizardContent(
                                state = state,
                                onEvent = onEvent,
                            )
                        }

                        is OrderItemReturnState.Existing -> {

                        }

                        is OrderItemReturnState.Unavailable -> {

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardContent(
    state: RequestItemReturnContract.State,
    onEvent: (RequestItemReturnContract.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        when (state.wizardStep) {
            RequestItemReturnWizardStep.RETURN_REASON -> {
                WizardScreenReturnReason(
                    orderItemTitle = state.orderItemDetails.orderItemTitle,
                    orderItemCoverImageUrl = state.orderItemDetails.orderItemCoverImageUrl,
                    orderItemPriceLabel = state.orderItemDetails.orderItemPriceLabel,
                    returnReasons = state.returnReasons,
                    selectedReturnReason = state.selectedReturnReason,
                    isRequestRefundChecked = state.isRequestRefundChecked,
                    onReturnReasonItemSelected = {
                        onEvent(
                            RequestItemReturnContract.Event.OnReturnReasonItemSelected(
                                it
                            )
                        )
                    },
                    onRequestRefundCheckboxClicked = {
                        onEvent(
                            RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked
                        )
                    }
                )
            }

            RequestItemReturnWizardStep.RETURN_DETAILS -> {
                WizardScreenReturnDetails(
                    descriptionValue = state.descriptionField.value,
                    maxDescriptionLength = state.descriptionMaxLength,
                    attachedImages = state.attachedImages,
                    maxImageCount = state.imageValidationConfig.maxCount,
                    maxImageSizeMb = state.imageValidationConfig.maxSizeMB,
                    onDescriptionChanged = {
                        onEvent(
                            RequestItemReturnContract.Event.OnDescriptionChanged(
                                it
                            )
                        )
                    },
                    onDeleteImageButtonClicked = {
                        onEvent(
                            RequestItemReturnContract.Event.OnImageDeleteButtonClicked(
                                it
                            )
                        )
                    },
                    onUploadPhotosButtonClicked = { onEvent(RequestItemReturnContract.Event.OnUploadPhotosButtonClicked) }
                )
            }

            RequestItemReturnWizardStep.OVERVIEW -> {
                WizardScreenOverview(
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun WizardScreenReturnReason(
    orderItemTitle: String,
    orderItemCoverImageUrl: String,
    orderItemPriceLabel: String,
    returnReasons: List<OrderItemReturnReasonUi>,
    selectedReturnReason: OrderItemReturnReasonUi?,
    isRequestRefundChecked: Boolean,
    onReturnReasonItemSelected: (OrderItemReturnReasonUi) -> Unit,
    onRequestRefundCheckboxClicked: () -> Unit
) {
    OrderItemPreviewCard(
        title = orderItemTitle,
        coverImageUrl = orderItemCoverImageUrl,
        priceLabel = orderItemPriceLabel
    )

    ReturnReasonSection(
        returnReasons = returnReasons,
        selectedItem = selectedReturnReason,
        onItemSelected = onReturnReasonItemSelected,
        isRequestRefundChecked = isRequestRefundChecked,
        onRequestRefundCheckboxClicked = onRequestRefundCheckboxClicked
    )
}

@Composable
private fun WizardScreenReturnDetails(
    descriptionValue: String,
    maxDescriptionLength: Int,
    attachedImages: List<AttachedFileContentData>,
    maxImageCount: Int,
    maxImageSizeMb: Int,
    onDescriptionChanged: (String) -> Unit,
    onDeleteImageButtonClicked: (Uri) -> Unit,
    onUploadPhotosButtonClicked: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    DescriptionSection(
        description = descriptionValue,
        maxDescriptionLength = maxDescriptionLength,
        onDescriptionChanged = onDescriptionChanged
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(ATTACHED_IMAGE_SIZE),
        state = lazyListState
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
                onDeleteIconClicked = { onDeleteImageButtonClicked(attachedImage.uri) }
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
                        onClick = onUploadPhotosButtonClicked
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

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.customColors.info) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
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

    MandatoryCharacterLabel()
}

@Composable
private fun WizardScreenOverview(
    state: RequestItemReturnContract.State,
    onEvent: (RequestItemReturnContract.Event) -> Unit
) {
//    ListingCard(
//        title = ,
//        titleMaxLines = TODO(),
//        coverImageUrl = TODO(),
//        additionalTitleContent = TODO(),
//        titleContentAlignment = TODO(),
//        trailingIcon = TODO(),
//        additionalContent = TODO(),
//        enabled = TODO()
//    ) { }
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
        additionalTitleContent = {
            Text(
                text = priceLabel,
                style = MaterialTheme.typography.subtitle1
            )
        },
        trailingIcon = null,
        additionalContent = null,
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun ReturnReasonSection(
    returnReasons: List<OrderItemReturnReasonUi>,
    selectedItem: OrderItemReturnReasonUi?,
    onItemSelected: (OrderItemReturnReasonUi) -> Unit,
    isRequestRefundChecked: Boolean,
    onRequestRefundCheckboxClicked: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeaderTitle(stringResource(R.string.screen_request_return_reason_label))
            MandatoryCharacter()
        }

        returnReasons.forEach { returnReason ->
            ReturnReasonItem(
                label = stringResource(returnReason.labelResId),
                isSelected = returnReason == selectedItem,
                onSelected = { onItemSelected(returnReason) }
            )
        }

        RequestRefundCheckbox(
            checked = isRequestRefundChecked,
            onClicked = onRequestRefundCheckboxClicked
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReturnReasonItem(
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelected)

            SectionHeaderTitle(title = label)
        }
    }
}

@Composable
private fun RequestRefundCheckbox(checked: Boolean, onClicked: () -> Unit) {
    Row(
        modifier = Modifier.clickableNoRipple(
            role = Role.Checkbox,
            onClick = onClicked
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClicked() },
        )

        Text(
            text = stringResource(R.string.screen_request_item_return_checkbox_request_refund),
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
    description: String,
    maxDescriptionLength: Int,
    onDescriptionChanged: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_description_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            OutlinedTextFieldValidation(
                value = description,
                onValueChange = onDescriptionChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.screen_request_item_return_description_placeholder),
                minLines = 4,
                maxLines = 8
            )

            Text(
                text = stringResource(
                    R.string.screen_request_item_return_description_remaining_characters_label,
                    description.length,
                    maxDescriptionLength
                ),
                modifier = Modifier.padding(start = SPACING_SMALL.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ImageItem(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    ListingImage(
        imageUrl = imageUrl,
        title = title,
        modifier = Modifier
            .size(ATTACHED_IMAGE_SIZE)
            .then(modifier)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AttachedImageItem(
    imageUrl: String,
    title: String,
    onDeleteIconClicked: () -> Unit
) {
    val alignmentOffset = ((48.dp - SPACING_MEDIUM.dp) / 2 - SPACING_SMALL.dp).coerceAtLeast(0.dp)

    Box {
        ImageItem(
            imageUrl = imageUrl,
            title = title,
        )

        ThrottledIconButton(
            onClick = onDeleteIconClicked,
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

@ThemePreview
@Composable
private fun ScreenPreview() {
    MarvelXTheme {
        RequestItemReturnScreen(
            onEvent = {},
            state = RequestItemReturnContract.State(),
            effect = emptyFlow(),
            onNavigationEffect = {},
            orderId = "1",
            orderItemId = "100",
            orderItemTitle = "Wow mug!!!!",
            orderItemCoverImageUrl = "....",
            orderItemPriceLabel = "150.20€",
        )
    }
}