package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal
import java.time.LocalDate

data class OrderItemReturn(
    val orderItemReturnId: OrderItemReturnId,
    val orderItem: OrderItemData,

    val returnStatus: OrderItemReturnStatus,
    val returnUntilDate: LocalDate,
    val isRequestRefundAvailable: Boolean,
    val returnShippingFee: BigDecimal,
    val refundAmount: BigDecimal?,

    val returnReason: OrderItemReturnReason,
    val description: String?,
    val attachedImageUrls: ImmutableList<String>,
    val condition: OrderItemReturnCondition,
    val returnMethod: OrderItemReturnMethod,

    val submittedAt: LocalDate,

    val sellerMessage: String?
)

fun ResponseOrderItemReturn.toData(): OrderItemReturn =
    OrderItemReturn(
        orderItemReturnId = OrderItemReturnId(orderItemReturnId),
        orderItem = orderItem.toData(),
        returnStatus = OrderItemReturnStatus.entries.first { it.key == returnStatus },
        returnUntilDate = LocalDate.parse(returnUntilDate),
        isRequestRefundAvailable = OrderItemRefundStatus.entries.first { it.key == refundStatus } == OrderItemRefundStatus.AVAILABLE,
        returnShippingFee = returnShippingFee?.toBigDecimal() ?: BigDecimal.ZERO,
        refundAmount = refundAmount?.toBigDecimal(),
        returnReason = OrderItemReturnReason.entries.first { it.key == returnReason },
        description = description,
        attachedImageUrls = attachedImageUrls.toImmutableListOrEmpty(),
        condition = OrderItemReturnCondition.entries.first { it.key == condition },
        returnMethod = OrderItemReturnMethod.entries.first { it.key == returnMethod },
        submittedAt = LocalDate.parse(submittedAt),
        sellerMessage = sellerMessage
    )