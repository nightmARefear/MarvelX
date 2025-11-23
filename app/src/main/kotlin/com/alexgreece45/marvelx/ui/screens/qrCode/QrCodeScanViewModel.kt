package com.alexgreece45.marvelx.ui.screens.qrCode

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.CreateDeepLinkActionResult
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.HandleQrCodeWebUrlResult
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.NavDeepLinkItem
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.qrCode.model.QrCodeErrorData
import com.alexgreece45.marvelx.ui.screens.qrCode.model.QrCodeProcessingData
import com.alexgreece45.marvelx.ui.screens.qrCode.model.QrCodeSuccessData
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed interface QrCodeScanContract {
    sealed interface Event : BaseEvent {
        data class Init(val previousScreenRoute: String) : Event
        data object OnBack : Event
        data class OnQrCodeScanned(val qrCode: String) : Event
        data class OnQrCodeScannedFailed(val failureMessage: String) : Event
        data class OnCameraBindFailed(val failureMessage: String) : Event
        data class OnPermissionRequestedResult(val permissionGranted: Boolean) : Event
        data object OnPermissionGranted : Event
        data object OnOpenAppSettingsButtonClicked : Event
        data object OnShowPermissionRationale : Event
    }

    data class State(
        val previousScreen: Screen? = null,

        val hasCameraPermission: Boolean = false,
        val shouldShowPermissionRationale: Boolean = false,

        val processingData: QrCodeProcessingData? = null,
        val errorData: QrCodeErrorData? = null,
        val successData: QrCodeSuccessData? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        data class ShowToastAndNavigateUp(val message: String) : Effect

        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
            data object OpenAppSettings : Navigation
        }

        /**
         * Caches the given [deepLinkAction]. This effect is particularly useful when the previous
         * screen is the one that the deep link is intended to navigate to.
         */
        data class CacheDeepLinkAction(val deepLinkAction: DeepLinkAction) : Effect

        /**
         * Opens the given [deepLinkAction].
         */
        data class NavigateUpAndOpenDeepLinkAction(val deepLinkAction: DeepLinkAction) : Effect
    }
}

class QrCodeScanViewModel :
    BaseViewModel<QrCodeScanContract.Event, QrCodeScanContract.State, QrCodeScanContract.Effect>() {

    private val deepLinkController: DeepLinkController by inject()
    private val resourceProvider: ResourceProvider by inject()

    /**
     * The QR code processing job. When this job isn't `null` it is an indication that a
     * successfully scanned QR code is currently being processed and another QR code __must__ not be
     * processed at the same time. When the processing finishes and an error has occurred that
     * _allows_ the re-scan of a new QR, this job __must__ be set to `null`.
     */
    private var qrCodeProcessingJob: Job? = null

    override fun setInitialState(): QrCodeScanContract.State =
        QrCodeScanContract.State()

    override fun handleEvents(event: QrCodeScanContract.Event) {
        when (event) {
            is QrCodeScanContract.Event.Init -> {
                setState {
                    copy(
                        previousScreen = Screen.fromRoute(event.previousScreenRoute),
                        errorData = QrCodeErrorData(resourceProvider.getString(R.string.qr_code_screen_label_error_invalid_qr_code_type))
                    )
                }
            }

            QrCodeScanContract.Event.OnPermissionGranted -> {
                setState {
                    copy(
                        hasCameraPermission = true
                    )
                }
            }

            is QrCodeScanContract.Event.OnQrCodeScanned -> {
                appLogger.d("QrCodeScanViewModel") { "(OnQrCodeScanned) scanned value: ${event.qrCode}" }

                if (qrCodeProcessingJob != null) return

                qrCodeProcessingJob = viewModelScope.launch {
                    setState {
                        copy(
                            errorData = null,
                            processingData = QrCodeProcessingData(
                                processingLabel = resourceProvider.getString(
                                    R.string.qr_code_screen_label_processing
                                )
                            )
                        )
                    }
                    delay(800L) // Fake delay; its only use is to display the `processing` lottie.
                    handleQrCodeWebUrl(event.qrCode).collect { shouldCancelJob ->
                        if (shouldCancelJob) {
                            qrCodeProcessingJob = null
                        }
                    }
                }
            }

            is QrCodeScanContract.Event.OnQrCodeScannedFailed -> {
                // Reserved for future use.

                /*appLogger.e("QrCodeScanViewModel") { "(OnQrCodeScannedFailed) failure message: ${event.failureMessage}" }

                viewModelScope.launch {
                    // This delay is intentional as without it, this event will be triggered which will
                    // cause a re-composition affecting the display of the `show rationale screen`.
                    delay(1000L)
                    setState {
                        copy(
                            errorData = QrCodeErrorData(resourceProvider.getString(R.string.qr_code_screen_label_error_invalid_qr_code_type))
                        )
                    }
                }*/
            }

            QrCodeScanContract.Event.OnShowPermissionRationale -> {
                setState {
                    copy(
                        shouldShowPermissionRationale = true
                    )
                }
            }

            QrCodeScanContract.Event.OnBack -> {
                setEffect {
                    QrCodeScanContract.Effect.Navigation.NavigateUp
                }
            }

            is QrCodeScanContract.Event.OnPermissionRequestedResult -> {
                appLogger.d("QrCodeScanViewModel") { "(OnPermissionRequestedResult) permission granted: ${event.permissionGranted}" }
                setState {
                    copy(
                        hasCameraPermission = event.permissionGranted
                    )
                }
            }

            is QrCodeScanContract.Event.OnCameraBindFailed -> {
                appLogger.e("QrCodeScanViewModel") { "(OnCameraBindFailed) failure message: ${event.failureMessage}" }
                viewModelScope.launch {
                    when (
                        val result = deepLinkController.createDeepLinkAction(
                            screenRoute = currentState.previousScreen?.route.orEmpty(),
                            deepLinkMap = mapOf(
                                Pair(
                                    first = NavDeepLinkItem.QrCodeScanErrorMessage,
                                    second = resourceProvider.getString(R.string.qr_code_screen_label_error_camera_binding_failed)
                                )
                            )
                        )
                    ) {
                        CreateDeepLinkActionResult.Failed -> {
                            setEffect {
                                QrCodeScanContract.Effect.ShowToastAndNavigateUp(
                                    message = resourceProvider.getGenericErrorMessage()
                                )
                            }
                        }

                        is CreateDeepLinkActionResult.Success -> {
                            setEffect {
                                QrCodeScanContract.Effect.CacheDeepLinkAction(
                                    deepLinkAction = result.deepLinkAction
                                )
                            }
                            setEffect {
                                QrCodeScanContract.Effect.Navigation.NavigateUp
                            }
                        }
                    }
                }
            }

            QrCodeScanContract.Event.OnOpenAppSettingsButtonClicked -> {
                setEffect {
                    QrCodeScanContract.Effect.Navigation.OpenAppSettings
                }
            }
        }
    }

    /**
     * Handles the given QR Code webURL and emits whether the QR code processing job should be
     * stopped.
     */
    private fun handleQrCodeWebUrl(qrCode: String): Flow<Boolean> = flow {
        deepLinkController.handleQrCodeWebUrl(qrCode)
            .collect { handleQrCodeWebUrlResult ->
                when (handleQrCodeWebUrlResult) {
                    HandleQrCodeWebUrlResult.ErrorCreatingDeepLinkAction -> {
                        appLogger.d("QrCodeScanViewModel") { "Error handling the scanned QR code web url." }
                        setEffect {
                            QrCodeScanContract.Effect.ShowToastAndNavigateUp(
                                message = resourceProvider.getGenericErrorMessage()
                            )
                        }
                    }

                    HandleQrCodeWebUrlResult.InvalidUrl -> {
                        setState {
                            copy(
                                errorData = QrCodeErrorData(resourceProvider.getString(R.string.qr_code_screen_label_error_invalid_qr_code_type)),
                                processingData = null
                            )
                        }
                        emit(true)
                    }

                    is HandleQrCodeWebUrlResult.Success -> {
                        appLogger.d("QrCodeScanViewModel") { "(handleQrCodeWebUrl) Successfully handled the scanned QR code web url." }

                        setState {
                            copy(
                                successData = QrCodeSuccessData(
                                    resourceProvider.getString(R.string.qr_code_screen_label_success_qr_code_scan)
                                )
                            )
                        }
                        delay(1600L) // Fake delay; its only use is to display the success lottie.
                        setEffect {
                            QrCodeScanContract.Effect.NavigateUpAndOpenDeepLinkAction(
                                handleQrCodeWebUrlResult.deepLinkAction
                            )
                        }
                    }
                }
            }
    }
}
