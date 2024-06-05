package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.model.security.ApplicationSecurity
import kotlinx.coroutines.flow.Flow

interface SplashInteractor {
    fun isApplicationSecure(): Flow<ApplicationSecurity>
}

class SplashInteractorImpl(
    private val securityController: SecurityController
) : SplashInteractor {
    override fun isApplicationSecure(): Flow<ApplicationSecurity> =
        securityController.isApplicationSecure()
}