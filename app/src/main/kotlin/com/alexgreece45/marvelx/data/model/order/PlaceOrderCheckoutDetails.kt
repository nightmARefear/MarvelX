package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.core.network.model.api.order.RequestPlaceOrderCheckoutDetails

data class PlaceOrderCheckoutDetails(
    // Shipping
    val firstName: String = "",
    val lastName: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val zipCode: String = "",
    val floorNumber: String = "",
    val locationType: String = "",

    // Payment
    val cardholderName: String = "",
    val cardNumber: String = "",
    val expirationDate: String = "",
    val cvv: String = ""
)

fun PlaceOrderCheckoutDetails.toRequest(): RequestPlaceOrderCheckoutDetails =
    RequestPlaceOrderCheckoutDetails(
        firstName = firstName,
        lastName = lastName,
        streetAddress = streetAddress,
        city = city,
        zipCode = zipCode,
        floorNumber = floorNumber,
        locationType = locationType,
        cardholderName = cardholderName,
        cardNumber = cardNumber,
        expirationDate = expirationDate,
        cvv = cvv
    )