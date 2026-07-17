package com.alexgreece45.marvelx.ui.screens.mainActivity.model

import android.net.Uri
import com.alexgreece45.marvelx.mvi.BaseEvent

sealed interface MainActivityEvent : BaseEvent {

    /**
     * Events triggered by the application lifecycle.
     */
    sealed interface LifecycleEvent : MainActivityEvent {
        data object OnCreate : LifecycleEvent
        data object OnPause : LifecycleEvent
        data object OnResume : LifecycleEvent

        /**
         * This event is executed when the activity is destroyed. [willBeRecreated] defines whether
         * the activity will be recreated indicating that the onDestroy function was called due to a
         * configuration change, and not because the user/OS closed/killed the app.
         *
         * If [willBeRecreated] is `true`, then the user isn't logged out.
         */
        data class OnDestroy(val willBeRecreated: Boolean) : LifecycleEvent
    }

    /**
     * This event is triggered every time the user interacts within the application view.
     */
    data object OnUserInteraction : MainActivityEvent

    /**
     * This event is triggered when this application shouldn't have the ability to draw itself as an
     * overlay window over other applications, but in fact it does.
     */
    data object ApplicationDrawOverlayWindowsViolation : MainActivityEvent

    /**
     * This event is triggered when this application shouldn't have the ability to be in multi
     * window mode, but in fact it is.
     */
    data object ApplicationInMultiWindowModeViolation : MainActivityEvent

    data object OnFlowStarted : MainActivityEvent

    /**
     * Triggered when the destination changes.
     */
    data class OnDestinationChanged(val route: String) : MainActivityEvent

    /**
     * Handles deep links.
     */
    data class OnDeepLinkReceived(val uri: Uri) : MainActivityEvent

    /**
     * When the FCM token has been received.
     */
    data class OnFcmTokenReceived(val fcmToken: String?) : MainActivityEvent

    data class OnFcmDeepLinkActionReceived(val stringifiedFcmDeepLinkAction: String) :
        MainActivityEvent
}