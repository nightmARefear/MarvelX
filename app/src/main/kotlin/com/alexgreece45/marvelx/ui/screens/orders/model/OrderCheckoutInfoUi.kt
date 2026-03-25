package com.alexgreece45.marvelx.ui.screens.orders.model

import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.util.extensions.toCustomFormat
import com.alexgreece45.marvelx.util.extensions.toFullName

data class OrderCheckoutInfoUi(
    val fullName: String,
    val streetAddress: String,
    val cityAndZipCode: String,
    val maskedCardNumber: String,
    val cardBrand: CardBrandUi,
    val paymentDate: InstantUi?,
    val transactionId: String?,
)

fun OrderCheckoutInfoDomain.toUi(): OrderCheckoutInfoUi =
    OrderCheckoutInfoUi(
        fullName = Pair(firstName, lastName).toFullName(),
        streetAddress = streetAddress,
        cityAndZipCode = "$city, $zipCode",
        maskedCardNumber = maskedCardNumber,
        cardBrand = cardBrand.toUi(),
        paymentDate = paymentDate?.let {
            InstantUi(
                value = paymentDate,
                label = paymentDate.toCustomFormat("MMMM dd, YYYY")
            )
        },
        transactionId = transactionId
    )
