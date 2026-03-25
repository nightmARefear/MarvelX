package com.alexgreece45.marvelx.domain.mappers.cart

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.data.model.cart.CartSummaryData
import com.alexgreece45.marvelx.domain.model_api.cart.CartSummaryDomain
import com.alexgreece45.marvelx.util.extensions.formatPrice

class CartSummaryDomainMapper(
    private val configPriceLogic: ConfigPriceLogic
) {

    fun toDomain(cartSummaryData: CartSummaryData): CartSummaryDomain {
        return with(cartSummaryData) {
            val subtotalPriceBigDecimal = subtotalPrice.toBigDecimal()
            val shippingPriceBigDecimal = shippingPrice.toBigDecimal()
            val totalPriceBigDecimal = totalPrice.toBigDecimal()

            CartSummaryDomain(
                subtotalPrice = subtotalPriceBigDecimal,
                subtotalPriceLabel = subtotalPriceBigDecimal.formatPrice(configPriceLogic.priceScale),
                shippingPrice = shippingPriceBigDecimal,
                shippingPriceLabel = shippingPriceBigDecimal.formatPrice(configPriceLogic.priceScale),
                totalPrice = totalPriceBigDecimal,
                totalPriceLabel = totalPriceBigDecimal.formatPrice(configPriceLogic.priceScale)
            )
        }
    }
}
