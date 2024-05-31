package com.alexgreece45.marvelx.controllers.model.websocket

/**
 * Contains all the possible websocket request types that can be made by the client -- i.e., the
 * application.
 */
sealed class WebsocketRequestType(open val key: String, val responseType: Any) {
    companion object {
        /**
         * Returns all the websocket requests as a [List].
         */
        private fun asList(): List<WebsocketRequestType> = listOf(GetTimestamp)

        /**
         * Returns the correlated [WebsocketRequestType] based on the given [key] if it could be found,
         * otherwise [Invalid] containing the [key].
         */
        fun of(key: String): WebsocketRequestType =
            asList().firstOrNull { it.key == key } ?: Invalid(key)
    }

    data object GetTimestamp :
        WebsocketRequestType(
            key = "retrieve_timestamp",
            responseType = String::class
        )

    /**
     * Used exclusively to represent an erroneous `clientRequest` sent by the websocket server.
     */
    data class Invalid(override val key: String) :
        WebsocketRequestType(key = key, responseType = String::class)
}