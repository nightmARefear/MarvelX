package com.alexgreece45.marvelx.domain.mappers.order.item

import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnEligibilityData
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnEligibilityDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.toDomain

fun OrderItemReturnEligibilityData.toDomain(): OrderItemReturnEligibilityDomain =
    OrderItemReturnEligibilityDomain(
        returnStatus = returnStatus?.toDomain(),
        returnUntilDate = returnUntilDate,
        refundUntilDate = refundUntilDate,
        refundStatus = refundStatus?.toDomain(),
        refundRequested = refundRequested
    )