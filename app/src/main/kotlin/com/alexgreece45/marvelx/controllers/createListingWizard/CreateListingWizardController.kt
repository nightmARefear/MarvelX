package com.alexgreece45.marvelx.controllers.createListingWizard

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import org.koin.core.annotation.Single
import java.math.BigDecimal

@Single
class CreateListingWizardController {

    private var _wizardMode: CreateListingWizardMode = CreateListingWizardMode.CREATE
    val wizardMode: CreateListingWizardMode get() = _wizardMode

    private var _createListingUi: CreateListingUi = CreateListingUi()
    val createListingUi: CreateListingUi get() = _createListingUi

    private var _updateListingUi: ListingUi? = null
    val updateListingUi: ListingUi? get() = _updateListingUi

    fun setWizardMode(newWizardMode: CreateListingWizardMode) {
        _wizardMode = newWizardMode
    }

    fun setAttachedImages(attachedImages: List<AttachedFileContentData>) {
        _createListingUi = _createListingUi.copy(
            attachedImages = attachedImages
        )
    }

    fun setTitle(title: String) {
        if (_wizardMode == CreateListingWizardMode.CREATE) {
            _createListingUi = _createListingUi.copy(title = title)
        } else {
            _updateListingUi = _updateListingUi?.copy(title = title)
        }
    }

    fun setDescription(description: String) {
        if (_wizardMode == CreateListingWizardMode.CREATE) {
            _createListingUi = _createListingUi.copy(description = description)
        } else {
            _updateListingUi = _updateListingUi?.copy(description = description)
        }
    }

    fun setCategory(listingCategory: ListingCategory) {
        _createListingUi = _createListingUi.copy(
            category = listingCategory
        )
    }

    fun setSubcategory(listingSubcategory: ListingSubcategory?) {
        _createListingUi = _createListingUi.copy(
            subcategory = listingSubcategory
        )
    }

    fun setListingDetails(listingDetailsUi: ListingDetailsUi) {
        _createListingUi = _createListingUi.copy(
            listingDetails = listingDetailsUi
        )
    }

    fun setCondition(condition: EnumGeneric) {
        _createListingUi = _createListingUi.copy(
            condition = condition
        )
    }

    fun setPrice(price: BigDecimal?) {
        _createListingUi = _createListingUi.copy(
            price = price ?: BigDecimal.ZERO
        )
    }

    fun setUpdateListing(listingUi: ListingUi) {
        _updateListingUi = listingUi
    }

    fun resetAllAdvancedDetailsData() {
        _createListingUi = _createListingUi.copy(
            listingDetails = null,
            condition = EnumGeneric("", ""),
            price = BigDecimal.ZERO
        )
    }

    fun resetCreateListingData() {
        _createListingUi = CreateListingUi()
    }

    fun resetUpdateListingData() {
        _updateListingUi = null
    }

    fun resetAllData() {
        resetCreateListingData()
        resetUpdateListingData()
    }
}