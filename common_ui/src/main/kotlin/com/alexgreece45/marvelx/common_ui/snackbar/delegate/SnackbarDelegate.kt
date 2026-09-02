package com.alexgreece45.marvelx.common_ui.snackbar.delegate

import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEffect
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.receiveAsSingleObserverFlow
import kotlinx.coroutines.channels.Channel
import org.koin.core.annotation.Factory
import java.util.UUID

interface SnackbarDelegate {
    val snackbarEffects: SingleObserverFlow<SnackbarEffect>

    fun showSuccessSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showErroneousSnackbar(
        errorMessage: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showInfoSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showWarningSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showCustomSnackbar(
        message: String,
        type: SnackbarType,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarActionData: SnackbarActionData? = null
    )

    fun showCustomSnackbar(snackbarNavigationPayload: SnackbarNavigationPayload)
}

@Factory(binds = [SnackbarDelegate::class])
internal class SnackbarDelegateImpl : SnackbarDelegate {

    private val _snackbarEffects: Channel<SnackbarEffect> =
        Channel(capacity = 1) // TODO: Review case where more than one snackbar is queued (hint: it's not).
    override val snackbarEffects: SingleObserverFlow<SnackbarEffect> =
        _snackbarEffects.receiveAsSingleObserverFlow()

    override fun showSuccessSnackbar(
        message: String,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        type = SnackbarType.SUCCESS,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showErroneousSnackbar(
        errorMessage: String,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = errorMessage,
        type = SnackbarType.ERROR,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showInfoSnackbar(
        message: String,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        type = SnackbarType.INFO,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showWarningSnackbar(
        message: String,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
        type = SnackbarType.WARNING,
        duration = duration,
        actionData = snackbarActionData
    )

    override fun showCustomSnackbar(
        message: String,
        type: SnackbarType,
        duration: SnackbarDuration,
        snackbarActionData: SnackbarActionData?
    ) = showSnackbar(
        message = message,
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
        type: SnackbarType,
        duration: SnackbarDuration,
        actionData: SnackbarActionData?
    ) {
        val newUuid = UUID.randomUUID()
        val snackbarStateData = SnackbarStateData(
            uuid = newUuid,
            message = message,
            type = type,
            duration = duration,
            actionData = actionData,
            onDismiss = { dismissSnackbar(newUuid) }
        )
        _snackbarEffects.trySend(SnackbarEffect.Show(snackbarStateData))
    }

    private fun dismissSnackbar(uuid: UUID) {
        _snackbarEffects.trySend(SnackbarEffect.OnDismissed(uuid))
    }
}