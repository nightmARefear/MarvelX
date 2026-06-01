package com.alexgreece45.marvelx.domain.model_api.order

@JvmInline
value class OrderId(val value: String) {
    val formatted: String get() = "#$value"

    companion object {
        fun from(raw: String) = OrderId(raw)
        fun fromFormatted(formatted: String) = OrderId(formatted.removePrefix("#"))
    }

    override fun toString(): String = value
}
