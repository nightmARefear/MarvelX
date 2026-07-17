package com.alexgreece45.marvelx.configuration

import org.koin.core.annotation.Single
import java.math.BigDecimal

/**
 * All configuration for prices.
 */
interface ConfigPriceLogic {

    /**
     * The scale for prices.
     */
    val priceScale: Int

    /**
     * The minimum price.
     */
    val minimumPrice: BigDecimal

    /**
     * The maximum price.
     */
    val maximumPrice: BigDecimal
}

@Single(binds = [ConfigPriceLogic::class])
internal class ConfigPriceLogicImpl : ConfigPriceLogic {

    override val priceScale: Int
        get() = 2

    override val minimumPrice: BigDecimal
        get() = "0.99".toBigDecimalOrNull() ?: BigDecimal.ZERO

    override val maximumPrice: BigDecimal
        get() = "9999.99".toBigDecimalOrNull() ?: BigDecimal.ZERO
}