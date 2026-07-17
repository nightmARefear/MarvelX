package com.alexgreece45.marvelx.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory

@Entity(
    tableName = "listing_subcategory",
    foreignKeys = [
        ForeignKey(
            entity = ListingCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id", "category_code"], unique = true)
    ]
)
data class ListingSubcategoryEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "category_code")
    val categoryCode: String,

    @ColumnInfo(name = "label")
    val label: String
)

fun ListingSubcategory.toDatabase(): ListingSubcategoryEntity =
    ListingSubcategoryEntity(
        id = id,
        categoryId = categoryId,
        categoryCode = categoryCode,
        label = label
    )
