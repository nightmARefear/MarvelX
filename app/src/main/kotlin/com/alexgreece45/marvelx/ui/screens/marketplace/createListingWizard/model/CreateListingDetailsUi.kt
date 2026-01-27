package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric

sealed interface CreateListingDetailsUi {

    data class ApparelDetailsUi(
        val size: CreateListingFieldUi.Enum,
        val color: CreateListingFieldUi.Text,

        val sizes: List<ListingEnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean get() = size.isValid && color.isValid
    }

    data class DrinkwareDetailsUi(
        val material: CreateListingFieldUi.Enum,
        val capacityMl: CreateListingFieldUi.Integer,
        val dishwasherSafe: Boolean? = null,

        val materials: List<ListingEnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() =
                material.isValid
                        && capacityMl.isValid
                        && dishwasherSafe != null
    }

    data class CollectibleDetailsUi(
        val scale: CreateListingFieldUi.Enum,
        val boxCondition: CreateListingFieldUi.Enum,

        val scales: List<ListingEnumGeneric> = emptyList(),
        val boxConditions: List<ListingEnumGeneric> = emptyList(),
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
        val material: CreateListingFieldUi.Enum,
        val color: CreateListingFieldUi.Text,

        val materials: List<ListingEnumGeneric>
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
        val grade: CreateListingFieldUi.Enum,
        val publisher: CreateListingFieldUi.Text,
        val year: CreateListingFieldUi.Integer,

        val grades: List<ListingEnumGeneric> = emptyList()
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = grade.isValid && publisher.isValid && year.isValid
    }

    data class CardDetailsUi(
        val grade: CreateListingFieldUi.Enum,
        val rarity: CreateListingFieldUi.Enum,
        val variant: CreateListingFieldUi.Enum,

        val grades: List<ListingEnumGeneric> = emptyList(),
        val rarities: List<ListingEnumGeneric> = emptyList(),
        val variants: List<ListingEnumGeneric> = emptyList(),
    ) : CreateListingDetailsUi {

        val isValid: Boolean
            get() = grade.isValid && rarity.isValid && variant.isValid
    }
}
