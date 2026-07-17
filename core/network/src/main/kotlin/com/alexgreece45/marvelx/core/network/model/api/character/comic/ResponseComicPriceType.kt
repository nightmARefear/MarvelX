package com.alexgreece45.marvelx.core.network.model.api.character.comic

sealed class ResponseComicPriceType(open val name: String) {
    data object Print : ResponseComicPriceType(name = "printPrice")
    data object DigitalPurchase :
        ResponseComicPriceType(name = "digitalPurchasePrice")

    data class Unknown(override val name: String) :
        ResponseComicPriceType(name)

    companion object {
        private fun asList(): List<ResponseComicPriceType> = listOf(Print, DigitalPurchase)

        fun getType(name: String): ResponseComicPriceType =
            asList().firstOrNull { it.name == name } ?: Unknown(name)
    }
}