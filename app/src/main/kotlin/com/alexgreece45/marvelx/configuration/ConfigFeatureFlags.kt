package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import org.koin.core.annotation.Single

/**
 * Feature flags for turning features on or off. The values come from the `FEATURE_*` `buildConfigField`s
 * in `app/build.gradle.kts`, which can be overridden per build type.
 */
interface ConfigFeatureFlags {

    /**
     * Whether the Profile screen shows the Transactions section.
     */
    val isTransactionsEnabled: Boolean

    /**
     * Whether the Marketplace toolbar shows the notifications button.
     */
    val isNotificationsButtonEnabled: Boolean

    val isNetworkInspectorEnabled: Boolean
}

@Single(binds = [ConfigFeatureFlags::class])
internal class ConfigFeatureFlagsImpl : ConfigFeatureFlags {

    override val isTransactionsEnabled: Boolean
        get() = BuildConfig.FEATURE_TRANSACTIONS_ENABLED

    override val isNotificationsButtonEnabled: Boolean
        get() = BuildConfig.FEATURE_NOTIFICATIONS_BUTTON_ENABLED

    override val isNetworkInspectorEnabled: Boolean
        get() = BuildConfig.FEATURE_NETWORK_INSPECTOR_ENABLED
}
