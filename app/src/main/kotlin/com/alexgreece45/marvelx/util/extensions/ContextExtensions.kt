package com.alexgreece45.marvelx.util.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
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
    (this as? MainActivity)?.pendingDeepLinkAction

/**
 * Clears the pending deep link action.
 */
fun Context.clearPendingDeepLinkAction() {
    (this as? MainActivity)?.pendingDeepLinkAction = null
}

/**
 * Closes the [MainActivity].
 */
fun Context.finish() {
    (this as? MainActivity)?.finish()
}
