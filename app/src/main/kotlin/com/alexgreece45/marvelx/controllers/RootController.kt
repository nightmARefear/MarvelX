package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.util.ResourceProvider
import com.scottyab.rootbeer.RootBeer

interface RootController {
    fun isRooted(): Boolean
    fun areRootCloakingAppsInstalled(): Boolean
}

class RootControllerImpl(
    resourceProvider: ResourceProvider
) : RootController {

    private val rootBeer = RootBeer(resourceProvider.getContext())

    override fun isRooted(): Boolean = rootBeer.isRooted

    override fun areRootCloakingAppsInstalled(): Boolean = rootBeer.detectRootCloakingApps(
        arrayOf("com.android.SSLTrustKiller")
    )
}