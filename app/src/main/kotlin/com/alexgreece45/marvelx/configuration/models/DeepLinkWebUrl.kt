package com.alexgreece45.marvelx.configuration.models

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.ui.navigation.Screen

/**
 * Defines all the deep link web URLs.
 *
 * @param suffix            The suffix of the web URL.
 * @param webUrl            The web URL.
 * @param screenBaseRoute   The corresponding screen base route.
 */
sealed class DeepLinkWebUrl(
    val suffix: String,
    val webUrl: String = WEB_BASE_URL + suffix,
    val screenBaseRoute: String
) {
    companion object {
        /**
         * The base URL for generating a web url that redirects to a specific deep link.
         */
        const val WEB_BASE_URL: String = BuildConfig.DEEPLINK_BASE_URL

        /**
         * Returns all the [DeepLinkWebUrl]s.
         */
        fun asList(): List<DeepLinkWebUrl> =
            DeepLinkWebUrl::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .toList()
    }

    /**
     * The web URL that redirects to the `Profile Details` deep link.
     */
    data object ProfileDetails : DeepLinkWebUrl(
        suffix = "/user/profile_details/",
        screenBaseRoute = Screen.ProfileDetailsScreen.baseRoute
    )

    /**
     * The web URL that redirects to the `ViewListing` deep link.
     */
    data object ViewListing :
        DeepLinkWebUrl(suffix = "/listing/", screenBaseRoute = "to be added")
}
