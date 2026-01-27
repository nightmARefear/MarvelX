package com.alexgreece45.marvelx.domain.mappers.marketplace

import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplacePromoSectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplaceCategorySection
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplacePromoSection
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview

fun ResponseMarketplace.toDomain(): MarketplaceDomain =
    MarketplaceDomain(
        promo = promo?.toDomain(),
        categories = categories.map { it.toDomain() }
    )

fun ResponseMarketplacePromoSection.toDomain(): MarketplacePromoSectionDomain =
    MarketplacePromoSectionDomain(
        title = title,
        listings = listings.map { it.toDomain() }
    )

fun ResponseMarketplaceCategorySection.toDomain(): MarketplaceCategorySectionDomain =
    MarketplaceCategorySectionDomain(
        title = title,
        categoryCode = categoryCode,
        listings = listings.map { it.toDomain() }
    )

fun ResponseListingPreview.toDomain(): ListingPreviewDomain =
    ListingPreviewDomain(
        id = id,
        sellerUserId = sellerUserId,
        title = title,
        description = description,
        price = price.toBigDecimal(),
        coverImageUrl = coverImageUrl
    )
