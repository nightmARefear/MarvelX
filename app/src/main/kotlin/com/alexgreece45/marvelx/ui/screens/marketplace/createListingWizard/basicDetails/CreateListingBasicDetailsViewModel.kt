package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails

import android.net.Uri
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.createListingWizard.CreateListingWizardController
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsAttachedImagesConfigurationData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.util.extensions.fromBytesToMb
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CreateListingBasicDetailsViewModel(
    private val resourceProvider: ResourceProvider,
    private val listingCategoryRepository: ListingCategoryRepository,
    private val formValidator: FormValidator,
    private val createListingWizardController: CreateListingWizardController,
    private val configLogic: ConfigLogic,
) :
    BaseViewModel<CreateListingBasicDetailsContract.Event, CreateListingBasicDetailsContract.State, CreateListingBasicDetailsContract.Effect>() {

    override fun setInitialState(): CreateListingBasicDetailsContract.State =
        CreateListingBasicDetailsContract.State()

    override fun handleEvents(event: CreateListingBasicDetailsContract.Event) {
        when (event) {
            is CreateListingBasicDetailsContract.Event.Init -> init(event.updateListingBasicDetails)
            CreateListingBasicDetailsContract.Event.OnResume -> onResume()
            CreateListingBasicDetailsContract.Event.OnBackButtonClicked -> onBackButtonClicked()
            is CreateListingBasicDetailsContract.Event.OnTitleChanged -> onTitleChanged(event.newTitle)
            is CreateListingBasicDetailsContract.Event.OnDescriptionChanged -> onDescriptionChanged(
                event.newDescription
            )

            CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnDismissed -> onCategoryDismissed()
            CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnClicked -> onCategoryClicked()
            is CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnItemSelected -> onCategoryItemSelected(
                event.listingCategory
            )

            CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnDismissed -> onSubcategoryDismissed()
            CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnClicked -> onSubcategoryClicked()
            is CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnItemSelected -> onSubCategoryItemSelected(
                event.listingSubcategory
            )

            // Image selection.
            is CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnSuccess -> onImagesSelectionSuccess(
                event.imagesFileContentData
            )

            CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnFailure -> onImagesSelectionFailure()

            CreateListingBasicDetailsContract.Event.ImageSelectionEvents.OnNoSelection -> onImagesNoSelection()

            // Upload image.
            CreateListingBasicDetailsContract.Event.OnUploadPhotosButtonClicked -> onUploadPhotosButtonClicked()

            // Attached image.
            is CreateListingBasicDetailsContract.Event.AttachedImageEvents.OnDeleteIconButtonClicked -> onAttachedImageDeleteIconButtonClicked(
                event.uri
            )

            is CreateListingBasicDetailsContract.Event.AttachedImageEvents.OnReordered -> onAttachedImageReordered(
                event.from,
                event.to
            )
        }
    }

    private fun init(updateListingBasicDetails: UpdateListingBasicDetails?) {
        if (currentState.isInitialized) return

        setState {
            copy(
                isLoading = true,
                configurationData = CreateListingBasicDetailsAttachedImagesConfigurationData(
                    maxImageCount = 5,
                    maxImageSize = configLogic.imageFileMaxSize,
                    supportedImageFileExtensions = configLogic.supportedImageFileExtensions
                ),
                title = title.copy(
                    textValidationRules = getTitleValidationRules()
                ),
                description = description.copy(
                    textValidationRules = getDescriptionValidationRules()
                )
            )
        }

        if (updateListingBasicDetails == null) {
            setState {
                copy(
                    isInitialized = true,
                    isLoading = false,
                    listingCategories = listingCategoryRepository.listingCategories
                )
            }
        } else {
            createListingWizardController.setTitle(updateListingBasicDetails.title)
            createListingWizardController.setDescription(updateListingBasicDetails.description)

            setState {
                copy(
                    isInitialized = true,
                    isLoading = false,
                    wizardMode = CreateListingWizardMode.UPDATE,
                    title = WizardInputFieldUi.Text(textValue = updateListingBasicDetails.title),
                    description = WizardInputFieldUi.Text(textValue = updateListingBasicDetails.description),
                    category = category.copy(
                        value = EnumGeneric(
                            code = "",
                            label = updateListingBasicDetails.categoryLabel
                        )
                    ),
                    subcategory = subcategory.copy(
                        value = EnumGeneric(
                            code = "",
                            label = updateListingBasicDetails.subcategoryLabel
                        )
                    ),
                    imageUrls = buildList {
                        add(updateListingBasicDetails.coverImageUrl)
                        addAll(updateListingBasicDetails.imageUrls)
                    }
                )
            }

            setEffect {
                CreateListingBasicDetailsContract.Effect.SetNextButtonEnabled(true)
            }
        }
    }

    private fun onResume() {
        if (currentState.isInitialized) {
            validateAllFields()
        }
    }

    private fun onBackButtonClicked() {
        setEffect {
            CreateListingBasicDetailsContract.Effect.GoToPreviousStep
        }
    }

    private fun onTitleChanged(newTitle: String) {
        setState {
            copy(
                title = title.copy(
                    textValue = newTitle,
                    textValidationResult = formValidator.validateForm(Form(mapOf(title.textValidationRules to newTitle)))
                )
            )
        }
        createListingWizardController.setTitle(newTitle)

        validateAllFields()
    }

    private fun onDescriptionChanged(newDescription: String) {
        setState {
            copy(
                description = description.copy(
                    textValue = newDescription,
                    textValidationResult = formValidator.validateForm(Form(mapOf(description.textValidationRules to newDescription)))
                )
            )
        }
        createListingWizardController.setDescription(newDescription)

        validateAllFields()
    }

    private fun onCategoryDismissed() {
        setState {
            copy(
                category = category.copy(isExpanded = false)
            )
        }
    }

    private fun onCategoryClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                category = category.copy(isExpanded = !category.isExpanded)
            )
        }
    }

    private fun onCategoryItemSelected(listingCategory: ListingCategory) {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        val wasDifferentCategorySelected = listingCategory.code != currentState.category.value?.code
        val newCategory = EnumGeneric(
            code = listingCategory.code,
            label = listingCategory.label
        )
        val subcategory =
            if (wasDifferentCategorySelected) WizardInputFieldUi.Enum() else currentState.subcategory

        setState {
            copy(
                category = category.copy(
                    value = newCategory,
                    isExpanded = false
                ),
                subcategory = subcategory,
                listingSubcategories = listingCategory.subcategories
            )
        }

        if (wasDifferentCategorySelected) {
            createListingWizardController.setCategory(listingCategory)
            createListingWizardController.setSubcategory(null)

            validateAllFields()
        }
    }

    private fun onSubcategoryDismissed() {
        setState {
            copy(
                subcategory = subcategory.copy(isExpanded = false)
            )
        }
    }

    private fun onSubcategoryClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setState {
            copy(
                subcategory = subcategory.copy(isExpanded = !subcategory.isExpanded)
            )
        }
    }

    private fun onSubCategoryItemSelected(listingSubcategory: ListingSubcategory) {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        val wasDifferentSubcategorySelected =
            listingSubcategory.categoryCode != currentState.subcategory.value?.code
        val newSubcategory = EnumGeneric(
            code = listingSubcategory.categoryCode,
            label = listingSubcategory.label
        )

        setState {
            copy(
                subcategory = WizardInputFieldUi.Enum(
                    value = newSubcategory,
                    isExpanded = false
                )
            )
        }

        if (wasDifferentSubcategorySelected) {
            createListingWizardController.setSubcategory(listingSubcategory)

            validateAllFields()
        }
    }

    private fun onImagesSelectionSuccess(allFilesContentData: List<AttachedFileContentData>?) {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        if (allFilesContentData == null) {
            showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_error_attaching_images_generic))
            return
        }

        if (allFilesContentData
                .map { it.name }
                .any { !it.endsWithAnySuffix(currentState.configurationData.supportedImageFileExtensions) }
        ) {
            showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_error_attaching_images_invalid_extension))
            return
        }

        if (currentState.attachedImages.size + allFilesContentData.size > currentState.configurationData.maxImageCount) {
            showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_error_attaching_images_over_max_count))
            return
        }

        val attachedImagesUris = currentState.attachedImages.map { it.uri }
        if (allFilesContentData.any { it.uri in attachedImagesUris }) {
            showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_error_attaching_images_existing_image))
            return
        }

        if (allFilesContentData.any { it.bytesCount > currentState.configurationData.maxImageSize }) {
            showToastMessage(
                resourceProvider.getString(
                    R.string.screen_create_listing_basic_details_error_attaching_images_over_max_size,
                    currentState.configurationData.maxImageSize.fromBytesToMb()
                )
            )
            return
        }

        val allImages = currentState.attachedImages + allFilesContentData
        setState {
            copy(
                attachedImages = allImages
            )
        }

        createListingWizardController.setAttachedImages(allImages)

        validateAllFields()
    }

    private fun onImagesSelectionFailure() {
        showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_error_attaching_images_generic))
    }

    private fun onImagesNoSelection() {
        showToastMessage(resourceProvider.getString(R.string.screen_create_listing_basic_details_no_selected_images))
    }

    private fun onUploadPhotosButtonClicked() {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        setEffect {
            CreateListingBasicDetailsContract.Effect.OpenPhotoPicker
        }
    }

    // Attached images.
    private fun onAttachedImageDeleteIconButtonClicked(uri: Uri) {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        val attachedImages = currentState.attachedImages.filterNot { it.uri == uri }
        setState {
            copy(
                attachedImages = attachedImages
            )
        }

        createListingWizardController.setAttachedImages(attachedImages)

        validateAllFields()
    }

    private fun onAttachedImageReordered(from: Int, to: Int) {
        if (currentState.wizardMode == CreateListingWizardMode.UPDATE) return

        val attachedImages = currentState.attachedImages.toMutableList().apply {
            add(to, removeAt(from))
        }

        setState {
            copy(
                attachedImages = attachedImages
            )
        }

        createListingWizardController.setAttachedImages(attachedImages)

        setEffect {
            CreateListingBasicDetailsContract.Effect.OnImagesReordered
        }
    }

    /**
     * Helper functions.
     */

    private fun validateAllFields() {
        val shouldEnableNextButton = with(currentState) {
            title.isValid
                    && description.isValid
                    && category.isValid
                    && subcategory.isValid
                    && if (wizardMode == CreateListingWizardMode.CREATE) attachedImages.size in 1..configurationData.maxImageCount else true
        }
        setEffect {
            CreateListingBasicDetailsContract.Effect.SetNextButtonEnabled(shouldEnableNextButton)
        }
    }

    private fun getTitleValidationRules(): List<Rule> {
        val minLength = 10
        val maxLength = 100
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_basic_details_error_field_blank_title)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            )
        )
    }

    private fun getDescriptionValidationRules(): List<Rule> {
        val minLength = 10
        val maxLength = 600
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.screen_create_listing_basic_details_error_field_blank_description)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_min_length,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.validation_rule_error_max_length,
                    maxLength
                )
            )
        )
    }

    private fun showToastMessage(message: String) {
        setEffect {
            CreateListingBasicDetailsContract.Effect.ShowToastMessage(message)
        }
    }
}
