package com.alexgreece45.marvelx.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ListingCategoryDao {

    @Transaction
    @Query("SELECT * FROM listing_category")
    suspend fun getAllCategoriesWithSubcategories(): List<ListingCategoryWithSubcategoriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ListingCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubcategories(subcategories: List<ListingSubcategoryEntity>)

    @Query("DELETE FROM listing_subcategory")
    suspend fun clearSubcategories()

    @Query("DELETE FROM listing_category")
    suspend fun clearCategories()

    @Transaction
    suspend fun replaceAll(
        categories: List<ListingCategoryEntity>,
        subcategories: List<ListingSubcategoryEntity>
    ) {
        clearSubcategories()
        clearCategories()
        insertCategories(categories)
        insertSubcategories(subcategories)
    }
}
