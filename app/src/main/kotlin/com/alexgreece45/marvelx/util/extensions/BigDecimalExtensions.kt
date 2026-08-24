package com.alexgreece45.marvelx.util.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.formatPrice(scale: Int, withEuroSymbol: Boolean = true): String {
    return this.setScale(scale, RoundingMode.DOWN).toPlainString().let { formattedPrice ->
        if (withEuroSymbol) {
            formattedPrice.withEuroSymbol()
        } else {
            formattedPrice
        }
    }
}