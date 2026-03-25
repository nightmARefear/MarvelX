package com.alexgreece45.marvelx.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory

@Entity(tableName = "listing_category")
data class ListingCategoryEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "category_code")
    val categoryCode: String,

    @ColumnInfo(name = "label")
    val label: String
)

fun ListingCategory.toDatabase(): ListingCategoryEntity =
    ListingCategoryEntity(
        id = id,
        categoryCode = code,
        label = label
    )
