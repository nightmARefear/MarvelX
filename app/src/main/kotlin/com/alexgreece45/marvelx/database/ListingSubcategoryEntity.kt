package com.alexgreece45.marvelx.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory

@Entity(
    tableName = "listing_subcategory",
    primaryKeys = ["category_code", "subcategory_code"],
    foreignKeys = [
        ForeignKey(
            entity = ListingCategoryEntity::class,
            parentColumns = ["category_code"],
            childColumns = ["category_code"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_code"])
    ]
)
data class ListingSubcategoryEntity(

    @ColumnInfo(name = "category_code")
    val categoryCode: String,

    @ColumnInfo(name = "subcategory_code")
    val subcategoryCode: String,

    @ColumnInfo(name = "label")
    val label: String
)

fun ListingSubcategory.toDatabase(categoryCode: String): ListingSubcategoryEntity =
    ListingSubcategoryEntity(
        categoryCode = categoryCode,
        subcategoryCode = code,
        label = label
    )
