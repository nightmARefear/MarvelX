package com.alexgreece45.marvelx.core.network.provider

import com.alexgreece45.marvelx.core.network.di.model.BackendOkHttpClient
import okhttp3.OkHttpClient
import org.koin.core.annotation.Single

interface AuthenticatedImageClientProvider {
    fun getClient(): OkHttpClient
}

@Single
internal class AuthenticatedImageClientProviderImpl(
    @BackendOkHttpClient private val okHttpClient: OkHttpClient
) : AuthenticatedImageClientProvider {

    override fun getClient(): OkHttpClient = okHttpClient
}