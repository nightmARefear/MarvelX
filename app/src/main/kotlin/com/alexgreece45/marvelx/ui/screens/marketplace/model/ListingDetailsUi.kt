package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDimensions
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

sealed interface ListingDetailsUi {
    val isValid: Boolean

    data class ApparelDetailsUi(
        val size: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val color: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),

        val sizes: List<EnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = size.isValid
                    && color.isValid
    }

    data class DrinkwareDetailsUi(
        val material: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val capacityMl: WizardInputFieldUi.Integer = WizardInputFieldUi.Integer(),
        val dishwasherSafe: Boolean = false,

        val materials: List<EnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = material.isValid
                    && capacityMl.isValid
    }

    data class CollectibleDetailsUi(
        val scale: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val boxCondition: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),

        val scales: List<EnumGeneric> = emptyList(),
        val boxConditions: List<EnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = scale.isValid
                    && boxCondition.isValid
    }

    data class ArtDetailsUi(
        val dimensions: ListingDimensions = ListingDimensions(),
        val framed: Boolean = false,
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = dimensions.isValid
    }

    data class AccessoryDetailsUi(
        val material: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val color: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),

        val materials: List<EnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = material.isValid
                    && color.isValid
    }

    data class HomeDetailsUi(
        val dimensions: ListingDimensions = ListingDimensions()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = dimensions.isValid
    }

    data class ComicDetailsUi(
        val grade: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val publisher: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val year: WizardInputFieldUi.Integer = WizardInputFieldUi.Integer(),

        val grades: List<EnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = grade.isValid
                    && publisher.isValid
                    && year.isValid
    }

    data class CardDetailsUi(
        val grade: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val rarity: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val variant: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),

        val grades: List<EnumGeneric> = emptyList(),
        val rarities: List<EnumGeneric> = emptyList(),
        val variants: List<EnumGeneric> = emptyList(),
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = grade.isValid
                    && rarity.isValid
                    && variant.isValid
    }
}
