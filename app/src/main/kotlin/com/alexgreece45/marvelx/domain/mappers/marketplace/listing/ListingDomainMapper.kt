package com.alexgreece45.marvelx.domain.mappers.marketplace.listing

import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingData
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsDomainMapper
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingOrderIdsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingStatusDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.UpdateListingDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import java.time.Instant

class ListingDomainMapper(
    private val listingDetailsDomainMapper: ListingDetailsDomainMapper
) {

    fun toDomain(listing: ListingData): ListingDomain {
        val status = ListingStatusDomain.entries.first { it.code == listing.statusCode }
        if ((status == ListingStatusDomain.Reserved || status == ListingStatusDomain.Sold)
            && listing.orderIds == null
        ) throw IllegalStateException(
            "\"orderIds\" field cannot be null when status is not active."
        )

        with(listing) {
            val allImageUrls = buildList {
                add(coverImageUrl)
                addAll(imageUrls)
            }.distinct()
            val coverImageUrl = allImageUrls.first()
            val imageUrls = allImageUrls.drop(1)

            return ListingDomain(
                listingId = listingId,
                sellerUserId = sellerUserId,
                categoryCode = categoryCode,
                subcategoryCode = subcategoryCode,
                conditionCode = conditionCode,
                status = ListingStatusDomain.entries.first { it.code == statusCode },
                title = title,
                description = description,
                price = price.toBigDecimal(),
                coverImageUrl = coverImageUrl,
                imageUrls = imageUrls,
                details = listingDetailsDomainMapper.toDomain(details),
                favorite = favorite,
                cart = cart,
                postedAt = Instant.parse(postedAt),
                updatedAt = updatedAt?.let { Instant.parse(it) },
                orderIds = orderIds?.let {
                    ListingOrderIdsDomain(
                        orderId = it.orderId.toDomain(),
                        orderItemId = it.orderItemId.toDomain()
                    )
                }
            )
        }
    }
}

fun UpdateListingDomain.toRequest(): RequestUpdateListing =
    RequestUpdateListing(
        title = title,
        description = description
    )