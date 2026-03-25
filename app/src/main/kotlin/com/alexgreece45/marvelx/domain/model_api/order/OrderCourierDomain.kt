package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderCourierData

enum class OrderCourierDomain {
    Acs,
    Elta,
    FedEx;
}

fun OrderCourierData.toDomain(): OrderCourierDomain =
    when (this) {
        OrderCourierData.ACS -> OrderCourierDomain.Acs
        OrderCourierData.Elta -> OrderCourierDomain.Elta
        OrderCourierData.FedEx -> OrderCourierDomain.FedEx
    }
