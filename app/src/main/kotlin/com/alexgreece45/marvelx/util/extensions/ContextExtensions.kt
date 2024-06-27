package com.alexgreece45.marvelx.util.extensions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkActionType
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
fun Context.getPendingDeepLinkAction(): DeepLinkAction? =
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
fun Context.clearPendingDeepLinkAction() {
    this.getActivity()?.pendingDeepLinkAction = null
}

/**
 * Attempts to retrieve [MainActivity] from the [Context]. If it fails to do so, `null` is returned.
 */
fun Context.getActivity(): MainActivity? = (this as? MainActivity)

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
