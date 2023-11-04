package com.alexgreece45.marvelx.domain.controllers

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.TOKEN_CLIENT_ID
import com.alexgreece45.marvelx.util.TOKEN_REDIRECT_URI
import javax.inject.Inject

interface ConfigController {
    fun getClientId(): String

    fun getClientSecret(): String

    fun getUri(): String
}

class ConfigControllerImpl @Inject constructor() : ConfigController {
    override fun getClientId(): String = TOKEN_CLIENT_ID

    override fun getClientSecret(): String = BuildConfig.TOKEN_CLIENT_SECRET

    override fun getUri(): String = TOKEN_REDIRECT_URI
}