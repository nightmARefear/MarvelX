package com.alexgreece45.marvelx.ui.screens.mainActivity.model

import com.alexgreece45.marvelx.controllers.model.security.SecurityViolation
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction

sealed class MainActivityEffect : BaseEffect {

    data class OpenOrCacheDeepLink(val deepLinkAction: DeepLinkAction) :
        MainActivityEffect()

    sealed class Navigation : MainActivityEffect() {
        data class NavigateToHomeScreenWithSessionExpired(val showErrorDialog: Boolean) :
            Navigation()

        data class NavigateToSplashScreenWithSecurityViolation(val securityViolation: SecurityViolation) :
            Navigation()
    }
}
