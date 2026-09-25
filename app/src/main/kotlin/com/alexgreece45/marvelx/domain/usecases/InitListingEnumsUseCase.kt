package com.alexgreece45.marvelx.domain.usecases

import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import org.koin.core.annotation.Factory

/**
 * Initializes the listing enum values (categories, statuses, apparel sizes, etc.), either from the
 * database cache or from the MarvelX-Backend API. It's invoked by `LoginInteractor.postLogin` on
 * success, which is the single point where every login flow completes.
 *
 * ### Note: The endpoints of these values require authorization, so this MUST only be invoked after the user's session (i.e., the access token) has been stored.
 */
@Factory
class InitListingEnumsUseCase(
    private val listingCategoryRepository: ListingCategoryRepository,
    private val listingDetailsRepository: ListingDetailsRepository
) {

    operator fun invoke() {
        listingCategoryRepository.init()
        listingDetailsRepository.init()
    }
}
