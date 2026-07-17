package com.alexgreece45.marvelx.ui.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.navOptions
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavItem
import com.alexgreece45.marvelx.ui.screens.mainActivity.MainActivity

/**
 * Opens the given deep link action and if the deep link action type is view then the backstack
 * is preserved, otherwise not.
 */
fun NavController.openDeepLinkAction(deepLinkAction: DeepLinkAction) {
    val deepLinkRequest = NavDeepLinkRequest.Builder
        .fromUri(
            generateComposableDeepLinkUri(
                deepLinkAction.screenRoute,
                generateComposableArguments(deepLinkAction.data)
            )
        )
        .build()

    val navOptions = navOptions {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
    navigate(deepLinkRequest, navOptions)
}

/**
 * Generates a new [Intent] based on the given data.
 */
private fun generateNewTaskDeepLink(
    context: Context,
    screenRoute: String,
    arguments: String = ""
): Intent =
    Intent(
        Intent.ACTION_VIEW,
        generateComposableDeepLinkUri(screenRoute, arguments),
        context,
        MainActivity::class.java
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

/**
 * Generates composable - friendly deep link arguments.
 */
fun generateComposableArguments(arguments: Map<String, Any>): String {
    if (arguments.isEmpty()) return ""
    return buildString {
        arguments.forEach { append("/${it.value}") }
    }
}

/**
 * Generates and returns the [Uri] needed by the [Intent].
 */
private fun generateComposableDeepLinkUri(screenRoute: String, arguments: String): Uri =
    buildString {
        append("${BuildConfig.DEEPLINK_SCHEME}://")
        append("${BuildConfig.MARVELX_BACKEND_DOMAIN}/")
        append(screenRoute)
        append(arguments)
    }.toUri()

/**
 * Creates the URI pattern for a deeplink based on the given [baseRoute] and [deepLinkArgs].
 */
fun createUriPatternForDeepLink(baseRoute: String, deepLinkArgs: List<NavItem>): String =
    buildString {
        append("${BuildConfig.DEEPLINK_SCHEME}://")
        append("${BuildConfig.MARVELX_BACKEND_DOMAIN}/")
        append(baseRoute)
        deepLinkArgs.forEach { navDeepLinkItem ->
            append("/{${navDeepLinkItem.key}}")
        }
    }
