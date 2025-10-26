package com.alexgreece45.marvelx.data.repository.imageLoader

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import okhttp3.OkHttpClient

class AuthenticatedImageLoader(
    private val context: Context,
    private val okHttpClient: OkHttpClient,
) : ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }
}
