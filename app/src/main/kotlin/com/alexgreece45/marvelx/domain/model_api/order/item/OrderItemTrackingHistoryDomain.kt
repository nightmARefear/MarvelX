package com.alexgreece45.marvelx.domain.model_api.order.item

import com.alexgreece45.marvelx.data.model.order.item.OrderItemTrackingHistoryData
import com.alexgreece45.marvelx.domain.model_api.order.OrderCourierDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.refund.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingHistoryItemDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.toDomain
import java.time.Instant

data class OrderItemTrackingHistoryDomain(
    val trackingId: String,
    val courier: OrderCourierDomain,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeDomain,
    val status: OrderItemStatusDomain,
    val shipped: OrderTrackingHistoryItemDomain?,
    val departedSortingFacility: OrderTrackingHistoryItemDomain?,
    val outForDelivery: OrderTrackingHistoryItemDomain?,
    val delivered: OrderTrackingHistoryItemDomain?,
    val returnStatus: OrderItemReturnStatusDomain?,
    val returnUntilDate: Instant?,
    val refundStatus: OrderItemRefundStatusDomain?,
    val refundUntilDate: Instant?,
)

fun OrderItemTrackingHistoryData.toDomain(): OrderItemTrackingHistoryDomain {
    if (delivered != null
        && (returnStatus == null || returnUntilDate == null)
        && (refundStatus == null || refundUntilDate == null)
    ) throw IllegalStateException("Return and/or refund states cannot be 'null' when the delivery status is 'delivered'.")

    return OrderItemTrackingHistoryDomain(
        trackingId = trackingId,
        courier = courier.toDomain(),
        estimatedDelivery = estimatedDelivery.toDomain(),
        status = status.toDomain(),
        shipped = shipped?.toDomain(),
        departedSortingFacility = departedSortingFacility?.toDomain(),
        outForDelivery = outForDelivery?.toDomain(),
        delivered = delivered?.toDomain(),
        returnStatus = returnStatus?.toDomain(),
        returnUntilDate = Instant.parse(returnUntilDate),
        refundStatus = refundStatus?.toDomain(),
        refundUntilDate = Instant.parse(refundUntilDate)
    )
}
