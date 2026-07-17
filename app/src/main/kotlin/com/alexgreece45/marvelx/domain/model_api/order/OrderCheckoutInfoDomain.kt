package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.cardBrand.CardBrandData
import com.alexgreece45.marvelx.data.model.order.OrderCheckoutInfoData
import java.time.Instant

data class OrderCheckoutInfoDomain(
    val firstName: String,
    val lastName: String,
    val streetAddress: String,
    val city: String,
    val zipCode: String,
    val maskedCardNumber: String,
    val cardBrand: CardBrandData,
    val paymentDate: Instant?,
    val transactionId: String?,
)

fun OrderCheckoutInfoData.toDomain(): OrderCheckoutInfoDomain =
    OrderCheckoutInfoDomain(
        firstName = firstName,
        lastName = lastName,
        streetAddress = streetAddress,
        city = city,
        zipCode = zipCode,
        maskedCardNumber = maskedCardNumber,
        cardBrand = cardBrand,
        paymentDate = Instant.parse(paymentDate),
        transactionId = transactionId
    )
