package com.alexgreece45.marvelx.data.model.cart

import com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartSummary

data class CartSummaryData(
    val subtotalPrice: String,
    val shippingPrice: String,
    val totalPrice: String
)

fun com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartSummary.toData(): CartSummaryData =
    CartSummaryData(
        subtotalPrice = subtotalPrice,
        shippingPrice = shippingPrice,
        totalPrice = totalPrice
    )
