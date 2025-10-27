package com.alexgreece45.marvelx.util.extensions

import coil.request.CachePolicy
import coil.request.ImageRequest

fun ImageRequest.Builder.noCache(): ImageRequest.Builder =
    this.memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
