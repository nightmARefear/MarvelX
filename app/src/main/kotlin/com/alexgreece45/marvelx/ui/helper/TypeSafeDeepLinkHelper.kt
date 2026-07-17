package com.alexgreece45.marvelx.ui.helper

import com.alexgreece45.marvelx.BuildConfig

fun getDeepLinkBasePath(screenName: String): String =
    buildString {
        append("${BuildConfig.DEEPLINK_SCHEME}://")
        append("${BuildConfig.MARVELX_BACKEND_DOMAIN}/")
        append(screenName)
    }