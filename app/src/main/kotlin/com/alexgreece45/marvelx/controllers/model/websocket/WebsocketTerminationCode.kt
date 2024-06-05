package com.alexgreece45.marvelx.controllers.model.websocket


/**
 * This sealed class defines all the possible websocket termination codes.
 */
sealed class WebsocketTerminationCode(open val code: Int, val description: String) {
    /**
     * Used to indicate that the websocket connection was terminated from the client.
     */
    data object Client : WebsocketTerminationCode(code = 1000, description = "client_termination")

    /**
     * Used to indicate that the websocket connection was terminated from the server.
     */
    data object Server : WebsocketTerminationCode(code = 1001, description = "server_termination")

    data class Invalid(override val code: Int) : WebsocketTerminationCode(code = code, description = "invalid_termination_code")

    companion object {
        /**
         * Returns the [List] of all valid [WebsocketTerminationCode]s.
         */
        private fun asList(): List<WebsocketTerminationCode> = listOf(Client, Server)

        /**
         * Returns the corresponding [WebsocketTerminationCode] based on the given [code], or
         * [Invalid] if it wasn't found.
         */
        fun of(code: Int): WebsocketTerminationCode = asList().firstOrNull { it.code == code } ?: Invalid(code)

    }
}