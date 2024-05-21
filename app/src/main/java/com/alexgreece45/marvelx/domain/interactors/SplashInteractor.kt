package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.controllers.SecurityController
import kotlinx.coroutines.flow.Flow

interface SplashInteractor {
    fun isApplicationSecure(): Flow<Boolean>
}

class SplashInteractorImpl(
    private val securityController: SecurityController
) : SplashInteractor {
    override fun isApplicationSecure(): Flow<Boolean> =
        securityController.isApplicationSecure()
}