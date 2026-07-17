package com.alexgreece45.marvelx.domain.model_api.cart

import java.math.BigDecimal

data class CartSummaryDomain(
    val subtotalPrice: BigDecimal,
    val subtotalPriceLabel: String,
    val shippingPrice: BigDecimal,
    val shippingPriceLabel: String,
    val totalPrice: BigDecimal,
    val totalPriceLabel: String
)
