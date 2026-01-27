package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplacePromoSectionDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceCategorySectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplacePromoSectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceUi


fun MarketplaceDomain.toUi(): MarketplaceUi =
    MarketplaceUi(
        promo = promo?.toUi(),
        categories = categories.map { it.toUi() }
    )

fun MarketplacePromoSectionDomain.toUi(): MarketplacePromoSectionUi =
    MarketplacePromoSectionUi(
        title = title,
        listings = listings.map { it.toUi() }
    )

fun MarketplaceCategorySectionDomain.toUi(): MarketplaceCategorySectionUi =
    MarketplaceCategorySectionUi(
        title = title,
        categoryCode = categoryCode,
        listings = listings.map { it.toUi() }
    )
