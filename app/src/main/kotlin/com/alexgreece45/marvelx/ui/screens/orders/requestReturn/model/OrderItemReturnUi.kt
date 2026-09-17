package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.core.common.extensions.defaultFormat
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturn
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnCondition
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnId
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnMethod
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnReason
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class OrderItemReturnUi(
    val orderItemReturnId: OrderItemReturnId,
    val orderItem: OrderItemUi,

    val returnStatus: OrderItemReturnStatusUi,
//    val returnShipping

    val reason: OrderItemReturnReason,
    val description: String?,
    val attachedImageUrls: ImmutableList<String>,
    val condition: OrderItemReturnCondition,
    val returnMethod: OrderItemReturnMethod,

    val submittedAt: LocalDateUi,

    val refundAmount: BigDecimalUi?,
    val sellerMessage: String?,
) {
    val isRefundRequested: Boolean get() = refundAmount != null // TODO: Use  @SerializedName("refundRequested") val refundRequested: String? = null,
}

internal fun OrderItemReturn.toUi(scale: Int): Result<OrderItemReturnUi> =
    runCatching {
        OrderItemReturnUi(
            orderItemReturnId = orderItemReturnId,
            orderItem = orderItem.toDomain().toUi(scale),
            returnStatus = returnStatus.toUi(),
            reason = returnReason,
            description = description,
            attachedImageUrls = attachedImageUrls,
            condition = condition,
            returnMethod = returnMethod,
            refundAmount = refundAmount?.let {
                BigDecimalUi(
                    value = it,
                    label = it.formatPrice(scale)
                )
            },
            sellerMessage = sellerMessage,
            submittedAt = submittedAt.let { LocalDateUi(value = it, label = it.defaultFormat()) }
        )
    }