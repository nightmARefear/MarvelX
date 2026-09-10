package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceSearchCriteriaDomain
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi

internal data class MarketplaceSearchCriteriaUi(
    val categories: List<SelectableOption<EnumGeneric>> = emptyList(),
    val conditions: List<SelectableOption<EnumGeneric>> = emptyList(),
    val priceRangeMin: InputFieldUi.Price = InputFieldUi.Price(isMandatory = false),
    val priceRangeMax: InputFieldUi.Price = InputFieldUi.Price(isMandatory = false),

    val datePosted: ListingDateRangeOptionUi = ListingDateRangeOptionUi.All,
    val datedPostedCustomRange: ListingDatePostedCustomRangeUi? = null,

    val finalSale: Boolean = false
) {
    /**
     * The total number of valid filters currently applied.
     */
    val validFiltersCount: Int
        get() {
            var count = 0

            if (categories.any { it.isSelected }) count++
            if (conditions.any { it.isSelected }) count++

            if (priceRangeMin.value.isNotBlank() && !priceRangeMax.validationResult.isError
                || priceRangeMax.value.isNotBlank() && !priceRangeMin.validationResult.isError
            ) count++

            if (datePosted != ListingDateRangeOptionUi.All) count++
            if (finalSale) count++

            return count
        }

    /**
     * The total number of active filters, regardless of their current validation state.
     */
    val activeFiltersCount: Int
        get() {
            var count = 0

            if (categories.any { it.isSelected }) count++
            if (conditions.any { it.isSelected }) count++

            if (priceRangeMin.value.isNotBlank() || priceRangeMax.value.isNotBlank()) count++

            if (datePosted != ListingDateRangeOptionUi.All) count++
            if (finalSale) count++

            return count
        }

    val areAllFiltersValid: Boolean
        get() = priceRangeMin.isValid
                && priceRangeMax.isValid
                && if (datePosted is ListingDateRangeOptionUi.Custom) datedPostedCustomRange != null else true

}

internal fun MarketplaceSearchCriteriaUi.toDomain(): Result<MarketplaceSearchCriteriaDomain> =
    runCatching {
        if (datePosted == ListingDateRangeOptionUi.Custom && datedPostedCustomRange != null) {
            throw IllegalStateException("Custom range cannot be empty.")
        }

        MarketplaceSearchCriteriaDomain(
            categoryCodes = categories.filter { it.isSelected }.map { it.option.code },
            conditionCodes = conditions.filter { it.isSelected }.map { it.option.code },
            minPrice = priceRangeMin.parsedValue,
            maxPrice = priceRangeMax.parsedValue,
            datePosted = datePosted.toData(),
            datedPostedCustomRange = datedPostedCustomRange?.toDomain(),
            finalSale = finalSale
        )
    }