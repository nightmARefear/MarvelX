package com.alexgreece45.marvelx.ui.screens.mainActivity.model

import com.alexgreece45.marvelx.controllers.model.security.SecurityViolation
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.notifications.model.NotificationData
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction

sealed class MainActivityEffect : BaseEffect {

    data class OpenOrCacheDeepLink(val deepLinkAction: DeepLinkAction) :
        MainActivityEffect()

    sealed class Navigation : MainActivityEffect() {
        data class NavigateToHomeScreenWithSessionExpired(val showErrorDialog: Boolean) :
            Navigation()

        data class NavigateToSplashScreenWithSecurityViolation(val securityViolation: SecurityViolation) :
            Navigation()
    }

    data class ShowNotification(val notificationData: NotificationData) : MainActivityEffect()
}
