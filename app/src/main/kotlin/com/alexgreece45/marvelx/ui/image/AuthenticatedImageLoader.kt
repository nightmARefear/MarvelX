package com.alexgreece45.marvelx.ui.image

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.alexgreece45.marvelx.core.network.provider.AuthenticatedImageClientProvider
import org.koin.core.annotation.Single

@Single
internal class AuthenticatedImageLoader(
    private val context: Context,
    private val clientProvider: AuthenticatedImageClientProvider
) : ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(clientProvider.getClient())
            .crossfade(true)
            .build()
    }
}