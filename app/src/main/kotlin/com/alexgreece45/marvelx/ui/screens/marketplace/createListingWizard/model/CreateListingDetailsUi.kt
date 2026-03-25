package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric

sealed interface CreateListingDetailsUi {

    data class ApparelDetailsUi(
        val size: WizardInputFieldUi.Enum,
        val color: WizardInputFieldUi.Text,

        val sizes: List<EnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean get() = size.isValid && color.isValid
    }

    data class DrinkwareDetailsUi(
        val material: WizardInputFieldUi.Enum,
        val capacityMl: WizardInputFieldUi.Integer,
        val dishwasherSafe: Boolean? = null,

        val materials: List<EnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() =
                material.isValid
                        && capacityMl.isValid
                        && dishwasherSafe != null
    }

    data class CollectibleDetailsUi(
        val scale: WizardInputFieldUi.Enum,
        val boxCondition: WizardInputFieldUi.Enum,

        val scales: List<EnumGeneric> = emptyList(),
        val boxConditions: List<EnumGeneric> = emptyList(),
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = scale.value != null
                    && boxCondition.value != null
    }

    data class ArtDetailsUi(
        val dimensions: CreateListingDimensionsUi,
        val framed: Boolean? = null
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() =
                dimensions.isValid
                        && framed != null
    }

    data class AccessoryDetailsUi(
        val material: WizardInputFieldUi.Enum,
        val color: WizardInputFieldUi.Text,

        val materials: List<EnumGeneric>
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = material.isValid && color.isValid
    }

    data class HomeDetailsUi(
        val dimensions: CreateListingDimensionsUi,
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = dimensions.isValid
    }

    data class ComicDetailsUi(
        val grade: WizardInputFieldUi.Enum,
        val publisher: WizardInputFieldUi.Text,
        val year: WizardInputFieldUi.Integer,

        val grades: List<EnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = grade.isValid && publisher.isValid && year.isValid
    }

    data class CardDetailsUi(
        val grade: WizardInputFieldUi.Enum,
        val rarity: WizardInputFieldUi.Enum,
        val variant: WizardInputFieldUi.Enum,

        val grades: List<EnumGeneric> = emptyList(),
        val rarities: List<EnumGeneric> = emptyList(),
        val variants: List<EnumGeneric> = emptyList(),
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = grade.isValid && rarity.isValid && variant.isValid
    }
}
