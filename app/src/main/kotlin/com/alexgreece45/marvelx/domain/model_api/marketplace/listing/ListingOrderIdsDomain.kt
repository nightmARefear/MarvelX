package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain

data class ListingOrderIdsDomain(
    val orderId: OrderIdDomain,
    val orderItemId: OrderIdDomain
)