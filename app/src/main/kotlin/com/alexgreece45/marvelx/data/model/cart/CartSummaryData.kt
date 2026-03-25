package com.alexgreece45.marvelx.data.model.cart

import com.alexgreece45.marvelx.network_logic.models_api.cart.ResponseCartSummary

data class CartSummaryData(
    val subtotalPrice: String,
    val shippingPrice: String,
    val totalPrice: String
)

fun ResponseCartSummary.toData(): CartSummaryData =
    CartSummaryData(
        subtotalPrice = subtotalPrice,
        shippingPrice = shippingPrice,
        totalPrice = totalPrice
    )
