package com.alexgreece45.marvelx.ui.screens.mainActivity.model

import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.notifications.model.NotificationData

sealed class MainActivityEffect : BaseEffect {

    data class OpenOrCacheDeepLink(val deepLinkAction: DeepLinkAction) :
        MainActivityEffect()

    sealed class Navigation : MainActivityEffect() {
        data class NavigateToHomeScreen(val logoutReason: LogoutReason) :
            Navigation()

        data class NavigateToSplashScreenWithSecurityViolation(val securityViolation: SecurityViolation) :
            Navigation()

        /**
         * Restarts the startup flow (splash → intro loading), e.g., so that a login whose redirect
         * arrived after process death is completed even though a restored screen is showing.
         */
        data object NavigateToSplashScreen : Navigation()
    }

    data class ShowNotification(val notificationData: NotificationData) : MainActivityEffect()
}