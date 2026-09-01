package com.alexgreece45.marvelx.util.extensions.model

import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl

data class ShareContentUrlData(
    val deepLinkWebUrl: DeepLinkWebUrl,
    val urlSuffix: String,
    val title: String
) {
    val url: String get() = deepLinkWebUrl.webUrl + urlSuffix
}