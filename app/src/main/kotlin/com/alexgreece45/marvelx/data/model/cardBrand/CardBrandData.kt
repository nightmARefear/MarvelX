package com.alexgreece45.marvelx.data.model.cardBrand

import androidx.compose.runtime.Immutable

@Immutable
enum class CardBrandData(val code: String) {
    MASTERCARD(code = "mastercard"),
    MAESTRO_CARD(code = "maestro_card"),
    VISA(code = "visa"),
    AMERICAN_EXPRESS(code = "american_express"),
    DINERS_CLUB(code = "diners_club"),
    DISCOVER(code = "discover"),
    UNKNOWN(code = "unknown");
}
