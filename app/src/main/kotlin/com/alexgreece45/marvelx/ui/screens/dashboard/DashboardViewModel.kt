package com.alexgreece45.marvelx.ui.screens.dashboard

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.dashboard.model.DashboardContract
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel :
    BaseViewModel<DashboardContract.Event, DashboardContract.State, DashboardContract.Effect>() {

    override fun setInitialState(): DashboardContract.State = DashboardContract.State()

    override fun handleEvents(event: DashboardContract.Event) {
        when (event) {
            is DashboardContract.Event.Init -> init(event.deepLinkAction)
        }
    }

    private fun init(deepLinkAction: DeepLinkAction?) {
        if (deepLinkAction != null) {
            setEffect {
                DashboardContract.Effect.OpenDeepLinkAction(deepLinkAction)
            }
        }
    }
}