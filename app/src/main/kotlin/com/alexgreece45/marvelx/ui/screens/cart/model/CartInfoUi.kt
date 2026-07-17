package com.alexgreece45.marvelx.ui.screens.cart.model

import com.alexgreece45.marvelx.domain.model_api.cart.CartSummaryDomain

data class CartInfoUi(
    val items: List<CartItemUi>,
    val summary: CartSummaryDomain
)
