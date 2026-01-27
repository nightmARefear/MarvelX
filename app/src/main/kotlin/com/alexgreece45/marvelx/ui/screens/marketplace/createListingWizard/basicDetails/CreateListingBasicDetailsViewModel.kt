package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails

import android.net.Uri
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule

class CreateListingBasicDetailsViewModel(
    private val resourceProvider: ResourceProvider,
    private val listingCategoryRepository: ListingCategoryRepository,
    private val formValidator: FormValidator,
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

            is CreateListingBasicDetailsContract.Event.ImageEvents.OnSelectionSuccess -> onImagesSelectionSuccess(
                event.imagesFileContentData
            )

            CreateListingBasicDetailsContract.Event.ImageEvents.OnNoSelection -> onImagesNoSelection()
            is CreateListingBasicDetailsContract.Event.ImageEvents.OnDeletionClicked -> onImageDeletionClicked(
                event.uri
            )

            is CreateListingBasicDetailsContract.Event.ImageEvents.OnSetAsCoverClicked -> onImageSetAsCoverClicked(
                event.uri
            )

            CreateListingBasicDetailsContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()
        }
    }

    private fun init(updateListingBasicDetails: UpdateListingBasicDetails?) {
        if (currentState.isInitialized) return

        setState {
            copy(
                isLoading = true,
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
            setState {
                copy(
                    isInitialized = true,
                    isLoading = false,
                    wizardMode = CreateListingWizardMode.UPDATE,
                    title = CreateListingFieldUi.Text(textValue = updateListingBasicDetails.title),
                    description = CreateListingFieldUi.Text(textValue = updateListingBasicDetails.description),
                    category = category.copy(
                        value = ListingEnumGeneric(
                            code = "",
                            label = updateListingBasicDetails.categoryLabel
                        )
                    ),
                    subcategory = subcategory.copy(
                        value = ListingEnumGeneric(
                            code = "",
                            label = updateListingBasicDetails.subcategoryLabel
                        )
                    ),
                    imageUrls = updateListingBasicDetails.imageUrls,
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
        val wasDifferentCategorySelected = listingCategory.code != currentState.category.value?.code
        setState {
            copy(
                category = category.copy(
                    value = ListingEnumGeneric(
                        code = listingCategory.code,
                        label = listingCategory.label
                    ),
                    isExpanded = false
                ),
                subcategory = if (wasDifferentCategorySelected) CreateListingFieldUi.Enum() else subcategory,
                listingSubcategories = listingCategory.subcategories
            )
        }

        if (wasDifferentCategorySelected) {
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

        setState {
            copy(
                subcategory = CreateListingFieldUi.Enum(
                    value = ListingEnumGeneric(
                        code = listingSubcategory.code,
                        label = listingSubcategory.label
                    ),
                    isExpanded = false
                )
            )
        }
        validateAllFields()
    }

    private fun onImagesSelectionSuccess(allFilesContentData: List<AttachedFileContentData>?) {}
    private fun onImagesNoSelection() {}
    private fun onImageDeletionClicked(uri: Uri) {}
    private fun onImageSetAsCoverClicked(uri: Uri) {}
    private fun onStickyBottomButtonClicked() {}

    /**
     * Helper functions.
     */

    private fun validateAllFields() {
        val shouldEnableNextButton = with(currentState) {
            title.isValid && description.isValid && category.isValid && subcategory.isValid
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
}
