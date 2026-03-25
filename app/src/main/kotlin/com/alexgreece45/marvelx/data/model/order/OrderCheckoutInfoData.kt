package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.data.model.cardBrand.CardBrandData
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderCheckoutInfo

data class OrderCheckoutInfoData(
    val firstName: String,
    val lastName: String,
    val streetAddress: String,
    val city: String,
    val zipCode: String,
    val maskedCardNumber: String,
    val cardBrand: CardBrandData,
    val paymentDate: String,
    val transactionId: String,
)

fun ResponseOrderCheckoutInfo.toData(): OrderCheckoutInfoData =
    OrderCheckoutInfoData(
        firstName = firstName,
        lastName = lastName,
        streetAddress = streetAddress,
        city = city,
        zipCode = zipCode,
        maskedCardNumber = maskedCardNumber,
        cardBrand = CardBrandData.entries.first { it.code == cardBrand },
        paymentDate = paymentDate,
        transactionId = transactionId
    )
