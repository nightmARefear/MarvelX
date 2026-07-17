package com.alexgreece45.marvelx.ui.screens.cart.model

import java.math.BigDecimal

data class CartItemUi(
    val listingId: String,
    val title: String,
    val price: BigDecimal,
    val priceLabel: String,
    val coverImageUrl: String,
    val favorite: Boolean,
    val isFavoriteProcessing: Boolean = false,
    val isDeleteProcessing: Boolean = false
)