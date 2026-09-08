package com.alexgreece45.marvelx.common_ui.snackbar.delegate

import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.receiveAsSingleObserverFlow
import kotlinx.coroutines.channels.Channel
import org.koin.core.annotation.Factory
import java.util.UUID

interface SnackbarDelegate {
    val snackbarEffects: SingleObserverFlow<SnackbarEvent>

    fun showSuccessSnackbar(
        message: String,
        maxMessageLines: Int = 2,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showErroneousSnackbar(
        errorMessage: String,
        maxErrorMessageLines: Int = 2,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showInfoSnackbar(
        message: String,
        maxMessageLines: Int = 2,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showWarningSnackbar(
        message: String,
        maxMessageLines: Int = 2,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showCustomSnackbar(
        message: String,
        maxMessageLines: Int = 2,
        type: SnackbarType,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showCustomSnackbar(snackbarNavigationPayload: SnackbarNavigationPayload)
}

@Factory(binds = [SnackbarDelegate::class])
internal class SnackbarDelegateImpl : SnackbarDelegate {

    private val _snackbarEffects: Channel<SnackbarEvent> =
        Channel(capacity = 1) // TODO: Review case where more than one snackbar is queued (hint: it's not).
    override val snackbarEffects: SingleObserverFlow<SnackbarEvent> =
        _snackbarEffects.receiveAsSingleObserverFlow()

    override fun showSuccessSnackbar(
        message: String,
        maxMessageLines: Int,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        maxMessageLines = maxMessageLines,
        type = SnackbarType.SUCCESS,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showErroneousSnackbar(
        errorMessage: String,
        maxErrorMessageLines: Int,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = errorMessage,
        maxMessageLines = maxErrorMessageLines,
        type = SnackbarType.ERROR,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showInfoSnackbar(
        message: String,
        maxMessageLines: Int,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        maxMessageLines = maxMessageLines,
        type = SnackbarType.INFO,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showWarningSnackbar(
        message: String,
        maxMessageLines: Int,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        maxMessageLines = maxMessageLines,
        type = SnackbarType.WARNING,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showCustomSnackbar(
        message: String,
        maxMessageLines: Int,
        type: SnackbarType,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        maxMessageLines = maxMessageLines,
        type = type,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showCustomSnackbar(snackbarNavigationPayload: SnackbarNavigationPayload) =
        with(snackbarNavigationPayload) {
            showCustomSnackbar(
                message = message,
                type = type,
                duration = duration,
                snackbarActionData = actionLabel?.let {
                    SnackbarActionData(
                        label = it,
                        actionType = SnackbarActionType.Hide
                    )
                }
            )
        }

    private fun showSnackbar(
        message: String,
        maxMessageLines: Int,
        type: SnackbarType,
        duration: SnackbarDuration,
        actionData: SnackbarActionData?
    ) {
        val newUuid = UUID.randomUUID()
        val snackbarStateData = SnackbarStateData(
            uuid = newUuid,
            message = message,
            maxMessageLines = maxMessageLines,
            type = type,
            duration = duration,
            actionData = actionData,
            onDismiss = { dismissSnackbar(newUuid) }
        )
        _snackbarEffects.trySend(SnackbarEvent.Show(snackbarStateData))
    }

    private fun dismissSnackbar(uuid: UUID) {
        _snackbarEffects.trySend(SnackbarEvent.OnDismissed(uuid))
    }
}