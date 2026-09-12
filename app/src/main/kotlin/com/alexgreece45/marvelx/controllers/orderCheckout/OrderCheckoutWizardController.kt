package com.alexgreece45.marvelx.controllers.orderCheckout

import com.alexgreece45.marvelx.data.model.order.PlaceOrderCheckoutDetails
import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.ui.screens.orders.model.CardBrandUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCheckoutInfoUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toData
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import org.koin.core.annotation.Single

@Single
class OrderCheckoutController {

    private var isInitialized: Boolean = false

    private var _placeOrderCheckoutDetails: PlaceOrderCheckoutDetails = PlaceOrderCheckoutDetails()
    val placeOrderCheckoutDetails: PlaceOrderCheckoutDetails? get() = if (isInitialized) _placeOrderCheckoutDetails else null

    val orderCheckoutInfo: OrderCheckoutInfoUi?
        get() = placeOrderCheckoutDetails?.let { data ->
            OrderCheckoutInfoDomain(
                firstName = data.firstName,
                lastName = data.lastName,
                streetAddress = data.streetAddress,
                city = data.city,
                zipCode = data.zipCode,
                maskedCardNumber = data.cardNumber.takeLast(4),
                cardBrand = CardBrandUi.getCardBrandUi(data.cardNumber).toData(),
                paymentDate = null,
                transactionId = null
            ).toUi()
        }

    fun setFirstName(firstName: String) {
        isInitialized =
            true // We put this assignment here since it is certain that the first name has been set.

        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(firstName = firstName)
    }

    fun setLastName(lastName: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(lastName = lastName)
    }

    fun setStreetAddress(streetAddress: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(streetAddress = streetAddress)
    }

    fun setCity(city: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(city = city)
    }

    fun setZipCode(zipCode: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(zipCode = zipCode)
    }

    fun setFloorNumber(floorNumber: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(floorNumber = floorNumber)
    }

    fun setLocationType(locationType: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(locationType = locationType)
    }

    fun setCardholderName(cardholderName: String) {
        _placeOrderCheckoutDetails =
            _placeOrderCheckoutDetails.copy(cardholderName = cardholderName)
    }

    fun setCardNumber(cardNumber: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(cardNumber = cardNumber)
    }

    fun setExpirationDate(expirationDate: String) {
        _placeOrderCheckoutDetails =
            _placeOrderCheckoutDetails.copy(expirationDate = expirationDate)
    }

    fun setCvv(cvv: String) {
        _placeOrderCheckoutDetails = _placeOrderCheckoutDetails.copy(cvv = cvv)
    }

    fun resetData() {
        isInitialized = false
        _placeOrderCheckoutDetails = PlaceOrderCheckoutDetails()
    }
}