package com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn

import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnInfoDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi

internal data class OrderItemReturnInfoUi(
    val returnUntilDate: InstantUi,
    val returnReasonInfo: OrderItemReturnReasonInfoUi?,
    val isEligibleForReturn: Boolean
)

//fun OrderItemReturnInfoDomain.toUi(): OrderItemReturnInfoUi =
//    OrderItemReturnInfoUi(
//        returnUntilDate = InstantUi(
//            value = returnUntilDate,
//            label = returnUntilDate.defaultFormat()
//        ),
//        reason = reason?.toUi(),
//        isEligibleForReturn = isEligibleForReturn
//    )
