package com.alexgreece45.marvelx.domain.mappers.marketplace

import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplaceCategorySection
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplacePromoSection
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplacePromoSectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain

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
        id = listingId,
        title = title,
        price = price.toBigDecimal(),
        coverImageUrl = coverImageUrl,
        favorite = favorite
    )
