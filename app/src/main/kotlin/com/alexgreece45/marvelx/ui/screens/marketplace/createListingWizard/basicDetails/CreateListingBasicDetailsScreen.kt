package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.chips.WrapChip
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.marketplace.components.WizardTextInputField
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.emptyFlow
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

private val LISTING_IMAGE_SIZE = ConstantsUi.LISTING_IMAGE_BIG_SIZE

@Composable
internal fun CreateListingBasicDetailsScreen(
    onEvent: (CreateListingBasicDetailsContract.Event) -> Unit,
    state: CreateListingBasicDetailsContract.State,
    effect: SingleObserverFlow<CreateListingBasicDetailsContract.Effect>,
    showSnackbar: (message: String, snackbarType: SnackbarType) -> Unit,
    goToPreviousStep: () -> Unit,
    setNextButtonEnabled: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val lazyListState = rememberLazyListState()
    val reorderableLazyRowState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onEvent(
            CreateListingBasicDetailsContract.Event.AttachedImageEvents.OnReordered(
                from = from.index,
                to = to.index
            )
        )

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isEmpty()) {
                onEvent(CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnNoSelection)
            } else {
                val allFilesContentData = mutableListOf<AttachedFileContentData>()
                uris.forEach { uri ->
                    val fileContentData = FileUtil.getFileContentData(
                        uri = uri,
                        context = context
                    )

                    if (fileContentData == null) {
                        onEvent(CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnFailure)
                        return@rememberLauncherForActivityResult
                    } else {
                        allFilesContentData.add(fileContentData)
                    }
                }

                onEvent(
                    CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnSuccess(
                        allFilesContentData
                    )
                )
            }
        }
    )

    ObserveUiEffect(flow = effect, tag = "CreateListingBasicDetailsScreen") { uiEffect ->
        when (uiEffect) {
            is CreateListingBasicDetailsContract.Effect.GoToPreviousStep -> goToPreviousStep()
            is CreateListingBasicDetailsContract.Effect.SetNextButtonEnabled -> setNextButtonEnabled(
                uiEffect.enabled
            )

            CreateListingBasicDetailsContract.Effect.OpenPhotoPicker -> {
                multiPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            is CreateListingBasicDetailsContract.Effect.ShowSnackbar -> showSnackbar(
                uiEffect.message,
                uiEffect.snackbarType
            )

            CreateListingBasicDetailsContract.Effect.OnImagesReordered -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CreateListingBasicDetailsContract.Event.OnResume)
    }

    ContentScreen(
        isLoading = state.isLoading,
        isInnerScreen = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            // Images & helper texts.
            Column {
                // Images.
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = LISTING_IMAGE_SIZE),
                    state = lazyListState,
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    if (state.wizardMode == CreateListingWizardMode.UPDATE) {
                        uploadedImages(imageUrls = state.imageUrls)
                    } else {
                        attachedImages(
                            attachedImages = state.attachedImages,
                            onDeleteIconClicked = { attachedImageUri ->
                                onEvent(
                                    CreateListingBasicDetailsContract.Event.AttachedImageEvents.OnDeleteIconButtonClicked(
                                        uri = attachedImageUri
                                    )
                                )
                            },
                            reorderableLazyRowState = reorderableLazyRowState,
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            },
                            onUploadPhotosButtonClicked = {
                                onEvent(
                                    CreateListingBasicDetailsContract.Event.OnUploadPhotosButtonClicked
                                )
                            }
                        )
                    }
                }

                // Helper texts.
                if (state.wizardMode == CreateListingWizardMode.CREATE) {
                    VSpacer.Medium()

                    AnimatedVisibility(visible = state.attachedImages.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.screen_create_listing_basic_details_images_helper_text_rearranging),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        VSpacer.Small()
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

                            Column {
                                Text(
                                    text = stringResource(
                                        R.string.screen_create_listing_basic_details_images_helper_text_limits,
                                        state.configurationData.maxImageCount,
                                        state.configurationData.maxImageSizeMegaBytes
                                    )
                                )

                                Text(
                                    text = stringResource(
                                        R.string.screen_create_listing_basic_details_images_helper_text_cover_image
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Title & Description.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                WizardTextInputField(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_title),
                    placeholder = stringResource(R.string.screen_create_listing_basic_details_placeholder_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CreateListingBasicDetailsTestTags.TITLE_TEXT_FIELD),
                    wizardInputField = state.title,
                    onValueChanged = {
                        onEvent(CreateListingBasicDetailsContract.Event.OnTitleChanged(it))
                    },
                    maxLines = 3
                )

                WizardTextInputField(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_description),
                    placeholder = stringResource(R.string.screen_create_listing_basic_details_placeholder_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CreateListingBasicDetailsTestTags.DESCRIPTION_TEXT_FIELD),
                    wizardInputField = state.description,
                    onValueChanged = {
                        onEvent(CreateListingBasicDetailsContract.Event.OnDescriptionChanged(it))
                    },
                    minLines = 5,
                    maxLines = 10
                )
            }

            // Category.
            ExposedDropdownMenu(
                label = stringResource(R.string.screen_create_listing_basic_details_label_category),
                expanded = state.category.isExpanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingBasicDetailsTestTags.CATEGORY_DROPDOWN),
                value = state.category.value?.label.orEmpty(),
                enabled = state.wizardMode.canEdit,
                onDismissRequest = { onEvent(CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnDismissed) },
                onExpandedChange = { onEvent(CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnClicked) }
            ) {
                state.listingCategories.forEach { listingCategory ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateListingBasicDetailsTestTags.categoryOption(listingCategory.code)),
                        onClick = {
                            onEvent(
                                CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnItemSelected(
                                    listingCategory = listingCategory
                                )
                            )
                        }
                    ) {
                        Text(
                            text = listingCategory.label
                        )
                    }
                }
            }

            // Subcategory.
            ExposedDropdownMenu(
                label = stringResource(R.string.screen_create_listing_basic_details_label_subcategory),
                expanded = state.subcategory.isExpanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingBasicDetailsTestTags.SUBCATEGORY_DROPDOWN),
                value = state.subcategory.value?.label.orEmpty(),
                enabled = state.wizardMode.canEdit,
                onDismissRequest = { onEvent(CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnDismissed) },
                onExpandedChange = { onEvent(CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnClicked) }
            ) {
                state.listingSubcategories.forEach { listingSubcategory ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateListingBasicDetailsTestTags.subcategoryOption(listingSubcategory.id)),
                        onClick = {
                            onEvent(
                                CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnItemSelected(
                                    listingSubcategory = listingSubcategory
                                )
                            )
                        }
                    ) {
                        Text(
                            text = listingSubcategory.label
                        )
                    }
                }
            }
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
                    R.string.screen_create_listing_basic_details_attached_listing_image_title,
                    (index + 1)
                ),
                isCover = index == 0,
                deleteButtonTestTag = CreateListingBasicDetailsTestTags.deleteImageButton(index + 1),
                onDeleteIconClicked = { onDeleteIconClicked(attachedImageUri) },
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
                .size(size = LISTING_IMAGE_SIZE)
                .testTag(CreateListingBasicDetailsTestTags.UPLOAD_PHOTOS_BUTTON)
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
                    contentDescription = stringResource(R.string.screen_create_listing_basic_details_content_description_upload_listing_image)
                )

                Text(
                    text = stringResource(R.string.screen_create_listing_basic_details_button_upload_photos),
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
                R.string.screen_create_listing_basic_details_uploaded_listing_image_title,
                (index + 1)
            ),
            isCover = index == 0
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ImageItem(
    imageUrl: String,
    title: String,
    isCover: Boolean,
    modifier: Modifier = Modifier
) {
    Box {
        ListingImage(
            imageUrl = imageUrl,
            title = title,
            modifier = Modifier
                .size(LISTING_IMAGE_SIZE)
                .then(modifier)
        )

        AnimatedVisibility(
            visible = isCover,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = SPACING_TINY.dp)
        ) {
            WrapChip(onClick = {}, content = {
                Text(
                    text = stringResource(R.string.screen_create_listing_basic_details_cover_listing_image_label)
                )
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AttachedImageItem(
    imageUrl: String,
    title: String,
    isCover: Boolean,
    deleteButtonTestTag: String,
    onDeleteIconClicked: () -> Unit,
    modifier: Modifier
) {
    val alignmentOffset = ((48.dp - SPACING_MEDIUM.dp) / 2 - SPACING_SMALL.dp).coerceAtLeast(0.dp)

    Box {
        ImageItem(
            imageUrl = imageUrl,
            title = title,
            modifier = modifier,
            isCover = isCover
        )

        ThrottledIconButton(
            onClick = onDeleteIconClicked,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = alignmentOffset, y = -alignmentOffset)
                .testTag(deleteButtonTestTag)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.screen_create_listing_basic_details_content_description_delete_listing_image),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ImageItemPreview() {
    MarvelXTheme {
        ImageItem(
            imageUrl = "",
            title = "cover image",
            isCover = true,
            modifier = Modifier
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CreateListingBasicDetailsScreenPreview() {
    MarvelXTheme {
        CreateListingBasicDetailsScreen(
            onEvent = {},
            state = CreateListingBasicDetailsContract.State(
                isLoading = false,
                title = WizardInputFieldUi.Text(textValue = "Super cool mug!"),
                attachedImages = listOf(
                    AttachedFileContentData(
                        uri = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image".toUri(),
                        name = "Super cool mug!",
                        bytesCount = 80000
                    )
                )
            ),
            effect = SingleObserverFlow(emptyFlow()),
            showSnackbar = { _, _ -> },
            goToPreviousStep = {},
            setNextButtonEnabled = {}
        )
    }
}