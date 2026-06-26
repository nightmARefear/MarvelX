package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState

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
    orderItemPriceLabel: String,
    showSnackbar: (String, SnackbarType) -> Unit
) {

    val context = LocalContext.current

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

            is RequestItemReturnContract.Effect.ShowSnackbar -> showSnackbar(
                uiEffect.message,
                uiEffect.type
            )
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
        navigableAction = ScreenNavigableAction.Backable(
            onBack = { onEvent(RequestItemReturnContract.Event.OnBack) }
        ),
        onBack = { onEvent(RequestItemReturnContract.Event.OnBack) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_request_item_return_toolbar_title),
        stickyBottom = {
            WrapButton(
                onClick = { onEvent(RequestItemReturnContract.Event.OnSubmitButtonClicked) },
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
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            AlertDialogError(
                isVisible = state.isRefundDialogVisible,
                title = stringResource(R.string.screen_request_item_return_refund_dialog_title),
                errorMessage = {
                    Text(
                        text = stringResource(R.string.screen_request_item_return_refund_dialog_error_message)
                    )
                },
                confirmButton = AlertDialogButton(
                    label = stringResource(R.string.screen_request_item_return_refund_dialog_button_positive),
                    buttonType = ErrorButtonType.Contained,
                    onClick = {
                        onEvent(RequestItemReturnContract.Event.RefundDialogEvent.OnPositiveButtonClicked)
                    }
                ),
                dismissButton = AlertDialogButton(
                    label = stringResource(R.string.screen_request_item_return_refund_dialog_button_negative),
                    onClick = {
                        onEvent(RequestItemReturnContract.Event.RefundDialogEvent.OnNegativeButtonClicked)
                    }
                )
            )

            OrderItemPreviewCard(
                title = orderItemTitle,
                coverImageUrl = orderItemCoverImageUrl,
                priceLabel = orderItemPriceLabel
            )

            ReturnReasonSection(
                returnReasons = state.returnReasons,
                selectedItem = state.selectedReturnReason,
                onItemSelected = {
                    onEvent(
                        RequestItemReturnContract.Event.OnReturnReasonItemSelected(
                            it
                        )
                    )
                },
                isRequestRefundChecked = state.isRequestRefundChecked,
                onRequestRefundCheckboxClicked = {
                    onEvent(RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked)
                }
            )

            DescriptionSection(
                description = state.descriptionField.value,
                maxDescriptionLength = state.descriptionMaxLength,
                onDescriptionChanged = {
                    onEvent(
                        RequestItemReturnContract.Event.OnDescriptionChanged(
                            it
                        )
                    )
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                MandatoryCharacter(fontStyle = FontStyle.Italic)

                Text(
                    text = stringResource(R.string.screen_request_item_return_mandatory_field_label),
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.subtitle2
                )
            }


        }
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

private fun LazyListScope.attachedImages(
    attachedImages: List<AttachedFileContentData>,
    onDeleteIconClicked: (Uri) -> Unit,
    reorderableLazyRowState: ReorderableLazyListState,
    onDragStarted: (Offset) -> Unit,
    onDragStopped: () -> Unit,
    onUploadPhotosButtonClicked: () -> Unit
) {
    itemsIndexed(
        items = attachedImages,
        key = { _, attachedImage -> attachedImage.uri }
    ) { index, attachedImage ->
        val attachedImageUri = attachedImage.uri
        ReorderableItem(
            state = reorderableLazyRowState,
            key = attachedImageUri
        ) {
            AttachedImageItem(
                imageUrl = attachedImageUri.toString(),
                title = stringResource(
                    R.string.screen_request_item_return_uploaded_image_content_description,
                    (index + 1)
                ),
                { onDeleteIconClicked(attachedImageUri) },
                modifier = Modifier
                    .draggableHandle(
                        onDragStarted = onDragStarted,
                        onDragStopped = onDragStopped
                    )
            )
        }
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

private fun LazyListScope.uploadedImages(imageUrls: List<String>) {
    itemsIndexed(
        items = imageUrls,
        key = { _, imageUrl -> imageUrl }
    ) { index, imageUrl ->
        ImageItem(
            imageUrl = imageUrl,
            title = stringResource(
                R.string.screen_request_item_return_uploaded_image_content_description,
                (index + 1)
            )
        )
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
    onDeleteIconClicked: () -> Unit,
    modifier: Modifier
) {
    val alignmentOffset = ((48.dp - SPACING_MEDIUM.dp) / 2 - SPACING_SMALL.dp).coerceAtLeast(0.dp)

    Box {
        ImageItem(
            imageUrl = imageUrl,
            title = title,
            modifier = modifier
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

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
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
            showSnackbar = { _, _ -> }
        )
    }
}