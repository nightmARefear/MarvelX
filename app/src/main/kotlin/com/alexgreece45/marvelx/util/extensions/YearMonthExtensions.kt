package com.alexgreece45.marvelx.util.extensions

import java.time.YearMonth

/**
 * Returns the expiration date format of this year month.
 */
fun YearMonth.toExpirationDate(): String =
    this.month.value.toString().padStart(2, '0') + "/" + this.year.toString().takeLast(2)
