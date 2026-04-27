package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnInfoData
import com.alexgreece45.marvelx.util.DateUtil
import java.time.Instant

data class OrderItemReturnInfoDomain(
    val returnUntilDate: Instant,
    val reason: OrderItemReturnReasonDomain?,
    val isEligibleForReturn: Boolean
)

fun OrderItemReturnInfoData.toDomain(): OrderItemReturnInfoDomain {
    val returnUntilDate = Instant.parse(returnUntilDate)
    return OrderItemReturnInfoDomain(
        returnUntilDate = returnUntilDate,
        reason = reason?.let { OrderItemReturnReasonDomain.entries.first { reason -> reason.key == it } },
        isEligibleForReturn = returnUntilDate <= DateUtil.getUTCInstant()
    )
}
