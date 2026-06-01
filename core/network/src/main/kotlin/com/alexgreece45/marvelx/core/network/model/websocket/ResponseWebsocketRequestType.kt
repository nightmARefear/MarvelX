package com.alexgreece45.marvelx.core.network.model.websocket

sealed class ResponseWebsocketRequestType(open val key: String, val responseType: Any) {
    companion object {
        /**
         * Returns all the websocket requests as a [List].
         */
        private fun asList(): List<ResponseWebsocketRequestType> = listOf(GetTimestamp)

        /**
         * Returns the correlated [ResponseWebsocketRequestType] based on the given [key] if it could be found,
         * otherwise [Invalid] containing the [key].
         */
        fun of(key: String): ResponseWebsocketRequestType =
            asList().firstOrNull { it.key == key } ?: Invalid(key)
    }

    data object GetTimestamp :
        ResponseWebsocketRequestType(
            key = "retrieve_timestamp",
            responseType = String::class
        )

    /**
     * Used exclusively to represent an erroneous `clientRequest` sent by the websocket server.
     */
    data class Invalid(override val key: String) :
        ResponseWebsocketRequestType(key = key, responseType = String::class)
}