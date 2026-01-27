package com.alexgreece45.marvelx.domain.mappers.marketplace.listing

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingData
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsDomainMapper
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.CreateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.UpdateListingDomain
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestCreateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestUpdateListing
import java.time.Instant

class ListingDomainMapper(
    private val listingDetailsDomainMapper: ListingDetailsDomainMapper
) {

    fun toDomain(listing: ListingData): ListingDomain {
        with(listing) {
            return ListingDomain(
                listingId = listingId,
                sellerUserId = sellerUserId,
                categoryCode = categoryCode,
                subcategoryCode = subcategoryCode,
                conditionCode = conditionCode,
                statusCode = statusCode,
                title = title,
                description = description,
                price = price.toBigDecimal(),
                coverImageUrl = coverImageUrl,
                imageUrls = imageUrls,
                details = listingDetailsDomainMapper.toDomain(details),
                favorite = favorite,
                postedAt = Instant.parse(postedAt),
                updatedAt = updatedAt?.let { Instant.parse(it) }
            )
        }
    }
}

fun CreateListingDomain.toRequest(): RequestCreateListing =
    RequestCreateListing(
        categoryCode = categoryCode,
        subcategoryCode = subcategoryCode,
        conditionCode = conditionCode,
        title = title,
        description = description,
        price = price.toPlainString(),
        coverImageUrl = coverImageUrl,
        imageUrls = imageUrls
    )

fun UpdateListingDomain.toRequest(): RequestUpdateListing =
    RequestUpdateListing(
        title = title,
        description = description
    )
