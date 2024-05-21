package com.alexgreece45.marvelx.util.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService

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