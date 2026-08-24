package com.alexgreece45.marvelx.ui.components.listing

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ListingPriceLabel(price: String) {
    Text(
        text = price,
        style = MaterialTheme.typography.subtitle1
    )
}