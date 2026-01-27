package com.alexgreece45.marvelx.data.model.marketplace.listing

import com.alexgreece45.marvelx.database.ListingCategoryWithSubcategoriesEntity
import com.alexgreece45.marvelx.database.ListingSubcategoryEntity
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingSubcategory

data class ListingCategory(
    val code: String,
    val label: String,
    val subcategories: List<ListingSubcategory>
)

data class ListingSubcategory(
    val code: String,
    val label: String
)

fun ResponseListingCategory.toData(): ListingCategory =
    ListingCategory(
        code = categoryCode,
        label = label,
        subcategories = subcategories.map { it.toData() }
    )

private fun ResponseListingSubcategory.toData(): ListingSubcategory =
    ListingSubcategory(
        code = subcategoryCode,
        label = label
    )

fun ListingCategoryWithSubcategoriesEntity.toData(): ListingCategory =
    ListingCategory(
        code = category.categoryCode,
        label = category.label,
        subcategories = subcategories.map { it.toData() }
    )

private fun ListingSubcategoryEntity.toData(): ListingSubcategory =
    ListingSubcategory(
        code = subcategoryCode,
        label = label
    )
