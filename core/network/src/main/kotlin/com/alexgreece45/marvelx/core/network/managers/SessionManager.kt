package com.alexgreece45.marvelx.core.network.managers

import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

// TODO: To be renamed.
interface SessionManager {

    fun triggerLogout(logoutReason: LogoutReason)
    val logoutEvents: SharedFlow<LogoutReason>
}

@Single
internal class SessionManagerImpl(
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : SessionManager {

    private val _logoutEvents: MutableSharedFlow<LogoutReason> = MutableSharedFlow()

    override fun triggerLogout(logoutReason: LogoutReason) {
        coroutineScope.launch {
            _logoutEvents.emit(logoutReason)
        }
    }

    override val logoutEvents: SharedFlow<LogoutReason> = _logoutEvents.asSharedFlow()
}
