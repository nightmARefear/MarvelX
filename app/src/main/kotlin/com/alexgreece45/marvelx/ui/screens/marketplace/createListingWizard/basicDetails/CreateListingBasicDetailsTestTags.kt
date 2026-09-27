package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails

internal object CreateListingBasicDetailsTestTags {
    // CreateListingBasicDetailsContract.Event.OnUploadPhotosButtonClicked
    const val UPLOAD_PHOTOS_BUTTON = "create_listing_basic_details_upload_photos_button"

    // CreateListingBasicDetailsContract.Event.OnTitleChanged
    const val TITLE_TEXT_FIELD = "create_listing_basic_details_title_text_field"

    // CreateListingBasicDetailsContract.Event.OnDescriptionChanged
    const val DESCRIPTION_TEXT_FIELD = "create_listing_basic_details_description_text_field"

    // CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnClicked / OnDismissed
    const val CATEGORY_DROPDOWN = "create_listing_basic_details_category_dropdown"

    // CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnClicked / OnDismissed
    const val SUBCATEGORY_DROPDOWN = "create_listing_basic_details_subcategory_dropdown"

    /**
     * CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnItemSelected; one tag per
     * option, keyed by `ListingCategory.code`.
     */
    fun categoryOption(code: String) = "create_listing_basic_details_category_${code}_option"

    /**
     * CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnItemSelected; one tag per
     * option, keyed by `ListingSubcategory.id`.
     */
    fun subcategoryOption(id: Int) = "create_listing_basic_details_subcategory_${id}_option"

    /**
     * CreateListingBasicDetailsContract.Event.AttachedImageEvents.OnDeleteIconButtonClicked; one tag
     * per attached image, keyed by its 1-based position.
     */
    fun deleteImageButton(position: Int) =
        "create_listing_basic_details_delete_image_${position}_button"
}
