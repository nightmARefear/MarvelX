package com.alexgreece45.marvelx.util.extensions

import java.text.DecimalFormat

/**
 * Formats this integer to a human-readable string with thousands separators and optional "K" abbreviation.
 *
 * Numbers below 10,000 are formatted with commas as thousands separators (e.g., "1,000").
 * Numbers 10,000 and above are abbreviated with "K" and optionally include decimal places for values under 100K.
 *
 * @return The formatted string representation of the number.
 *
 * ### Examples
 * ```
 * 100.formatNumberWithCommas()    // Returns "100"
 * 1000.formatNumberWithCommas()    // Returns "1,000"
 * 10000.formatNumberWithCommas()   // Returns "10K"
 * 10120.formatNumberWithCommas()   // Returns "10.12K"
 * 10200.formatNumberWithCommas()   // Returns "10.2K"
 * 100000.formatNumberWithCommas()  // Returns "100K"
 * ```
 */
fun Int.formatNumberWithCommas(): String {
    if (this <= 9999) return DecimalFormat("#,###").format(this)

    val thousands = this / 1000.0
    return if (thousands < 100) {
        DecimalFormat("#.##K").format(thousands)
    } else {
        DecimalFormat("#K").format(thousands)
    }
}
