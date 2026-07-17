package com.alexgreece45.marvelx.database

import androidx.room.Embedded
import androidx.room.Relation

data class ListingCategoryWithSubcategoriesEntity(

    @Embedded
    val category: ListingCategoryEntity,

    @Relation(
        parentColumn = "category_code",
        entityColumn = "category_code"
    )
    val subcategories: List<ListingSubcategoryEntity>
)
