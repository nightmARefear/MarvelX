package com.alexgreece45.marvelx.data.model.marketplace.listing

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingSubcategory
import com.alexgreece45.marvelx.database.ListingCategoryWithSubcategoriesEntity
import com.alexgreece45.marvelx.database.ListingSubcategoryEntity

data class ListingCategory(
    val id: Int,
    val code: String,
    val label: String,
    val subcategories: List<ListingSubcategory>
)

data class ListingSubcategory(
    val id: Int,
    val categoryId: Int,
    val categoryCode: String,
    val label: String
)

fun ResponseListingCategory.toData(): ListingCategory =
    ListingCategory(
        id = id,
        code = categoryCode,
        label = label,
        subcategories = subcategories.map { it.toData() }
    )

private fun ResponseListingSubcategory.toData(): ListingSubcategory =
    ListingSubcategory(
        id = id,
        categoryId = categoryId,
        categoryCode = categoryCode,
        label = label
    )

fun ListingCategoryWithSubcategoriesEntity.toData(): ListingCategory =
    ListingCategory(
        id = category.id,
        code = category.categoryCode,
        label = category.label,
        subcategories = subcategories.map { it.toData() }
    )

private fun ListingSubcategoryEntity.toData(): ListingSubcategory =
    ListingSubcategory(
        id = id,
        categoryId = categoryId,
        categoryCode = categoryCode,
        label = label
    )