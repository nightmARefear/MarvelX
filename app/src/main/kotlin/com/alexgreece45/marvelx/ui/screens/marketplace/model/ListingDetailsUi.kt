package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDimensions
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi

sealed interface ListingDetailsUi {
    val isValid: Boolean

    data class ApparelDetailsUi(
        val size: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val color: CreateListingFieldUi.Text = CreateListingFieldUi.Text(),

        val sizes: List<ListingEnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = size.isValid
                    && color.isValid
    }

    data class DrinkwareDetailsUi(
        val material: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val capacityMl: CreateListingFieldUi.Integer = CreateListingFieldUi.Integer(),
        val dishwasherSafe: Boolean = false,

        val materials: List<ListingEnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = material.isValid
                    && capacityMl.isValid
    }

    data class CollectibleDetailsUi(
        val scale: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val boxCondition: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),

        val scales: List<ListingEnumGeneric> = emptyList(),
        val boxConditions: List<ListingEnumGeneric> = emptyList()
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
        val material: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val color: CreateListingFieldUi.Text = CreateListingFieldUi.Text(),

        val materials: List<ListingEnumGeneric> = emptyList()
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
        val grade: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val publisher: CreateListingFieldUi.Text = CreateListingFieldUi.Text(),
        val year: CreateListingFieldUi.Integer = CreateListingFieldUi.Integer(),

        val grades: List<ListingEnumGeneric> = emptyList()
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = grade.isValid
                    && publisher.isValid
                    && year.isValid
    }

    data class CardDetailsUi(
        val grade: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val rarity: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val variant: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),

        val grades: List<ListingEnumGeneric> = emptyList(),
        val rarities: List<ListingEnumGeneric> = emptyList(),
        val variants: List<ListingEnumGeneric> = emptyList(),
    ) : ListingDetailsUi {

        override val isValid: Boolean
            get() = grade.isValid
                    && rarity.isValid
                    && variant.isValid
    }
}
