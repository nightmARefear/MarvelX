package com.alexgreece45.marvelx.util.extensions

import java.math.BigDecimal

fun BigDecimal.formatPrice(): String {
    return "€%.2f".format(this)
}
