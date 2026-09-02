package com.alexgreece45.marvelx.core.common.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.formatPrice(
    scale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    withEuroSymbol: Boolean = true
): String {
    return this.setScale(scale, roundingMode).toPlainString().let { formattedPrice ->
        if (withEuroSymbol) {
            formattedPrice.withEuroSymbol()
        } else {
            formattedPrice
        }
    }
}