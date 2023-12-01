package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.controllers.SecurityController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SplashInteractor {
    fun isApplicationSecure(): Flow<Boolean>
}

class SplashInteractorImpl @Inject constructor(
    private val securityController: SecurityController
) : SplashInteractor {
    override fun isApplicationSecure(): Flow<Boolean> =
        securityController.isApplicationSecure()
}