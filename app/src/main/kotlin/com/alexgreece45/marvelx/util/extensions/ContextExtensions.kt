package com.alexgreece45.marvelx.util.extensions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.screens.mainActivity.MainActivity

/**
 * Detects if this device has internet connection and returns `true` if it has otherwise `false`.
 */
fun Context.hasInternetConnectivity(): Boolean {
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        activeNetwork?.let { network ->
            getNetworkCapabilities(network)?.let { networkCapabilities ->
                return (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
                        && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            }
        }
    }
    return false
}

/**
 * Attempts to retrieve the pending deep link action. If it wasn't found, `null` is returned.
 */
private fun Context.getPendingDeepLinkAction(): DeepLinkAction? =
    this.getActivity()?.pendingDeepLinkAction

/**
 * Sets the given [deepLinkAction].
 */
fun Context.setPendingDeepLinkAction(deepLinkAction: DeepLinkAction) {
    this.getActivity()?.pendingDeepLinkAction = deepLinkAction
}

/**
 * Clears the pending deep link action.
 */
private fun Context.clearPendingDeepLinkAction() {
    this.getActivity()?.pendingDeepLinkAction = null
}

/**
 * Retrieves the pending deep link action and removes it from cache as well. If no deep link was
 * found, `null` is returned instead.
 */
fun Context.getAndRemovePendingDeepLinkAction(): DeepLinkAction? {
    val pendingDeepLinkAction = this.getActivity()?.getPendingDeepLinkAction()
    clearPendingDeepLinkAction()
    return pendingDeepLinkAction
}

/**
 * Attempts to retrieve [MainActivity] from the [Context]. If it fails to do so, `null` is returned.
 */
internal fun Context.getActivity(): MainActivity? = (this as? MainActivity)

/**
 * Closes the [MainActivity].
 */
fun Context.finish() {
    this.getActivity()?.finish()
}

/**
 * Opens the application settings for this app.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

/**
 * Shares a specific content's link.
 */
fun Context.shareContentUrl(url: String, title: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"

        putExtra(Intent.EXTRA_TEXT, url)
        putExtra(Intent.EXTRA_TITLE, title)
    }

    val chooser = Intent.createChooser(intent, title).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}

/**
 * shows a toast message.
 *
 * @param message The message to display.
 * @param shortDuration Whether this message should be shown for a short period. Defaults to `true`.
 */
fun Context.showToast(message: String, shortDuration: Boolean = true) {
    Toast.makeText(
        this,
        message,
        if (shortDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}