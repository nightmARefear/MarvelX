package com.alexgreece45.marvelx.core.data.model.order

@JvmInline
value class OrderId(val value: String) {

    override fun toString(): String = value

    companion object {
        val EMPTY = OrderId("")
    }
}