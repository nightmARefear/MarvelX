package com.alexgreece45.marvelx.core.network.storage

import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode

/**
 * Network inspector (Chucker) preferences, read by the network layer for every request.
 */
interface NetworkInspectorSettings {
    fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode
}
