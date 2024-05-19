package com.alexgreece45.marvelx.controllers

/**
 * Provides various functions for cryptography-related operations such as creating a session
 * signature for API calls, and many more.
 */
interface CryptoController {
    /**
     * Creates a session signature.
     */
    fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        key: String,
        algorithm: String
    ): String
}

class CryptoControllerImpl(

) : CryptoController {
    override fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        key: String,
        algorithm: String
    ): String {
        // TODO: TO BE IMPLEMENTED

        return ""
    }
}