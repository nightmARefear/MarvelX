package com.alexgreece45.marvelx.core.security.controllers

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import org.koin.core.annotation.Single

internal interface RootController {
    fun isRooted(): Boolean
    fun areRootCloakingAppsInstalled(): Boolean
}

@Single
internal class RootControllerImpl(
    context: Context
) : RootController {

    private val rootBeer = RootBeer(context)

    override fun isRooted(): Boolean = rootBeer.isRooted

    override fun areRootCloakingAppsInstalled(): Boolean = rootBeer.detectRootCloakingApps(
        arrayOf("com.android.SSLTrustKiller")
    )
}