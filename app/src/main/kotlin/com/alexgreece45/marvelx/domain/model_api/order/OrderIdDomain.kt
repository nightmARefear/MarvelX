package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.core.data.model.order.OrderId

@JvmInline
value class OrderIdDomain(val value: String) {
    val formatted: String get() = "#$value"

    override fun toString(): String = value

    companion object {
        val EMPTY = OrderIdDomain("")

        fun from(raw: String) = OrderIdDomain(raw)
        fun fromFormatted(formatted: String) = OrderIdDomain(formatted.removePrefix("#"))
    }
}

internal fun OrderId.toDomain(): OrderIdDomain = OrderIdDomain(this.value)

internal fun OrderIdDomain.toData(): OrderId = OrderId(value)