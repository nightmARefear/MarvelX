package com.alexgreece45.marvelx.ui.screens.qrCode

import android.Manifest
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LottieComposable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.QrCodeAnalyzer
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.setPendingDeepLinkAction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun QrCodeScanScreen(
    onEventSent: (QrCodeScanContract.Event) -> Unit,
    state: QrCodeScanContract.State,
    effect: SingleObserverFlow<QrCodeScanContract.Effect>,
    previousScreenRoute: String,
    onNavigation: (QrCodeScanContract.Effect.Navigation) -> Unit,
    navigateUpAndOpenDeepLinkAction: (DeepLinkAction) -> Unit,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current

    OneTimeLaunchedEffect {
        QrCodeScanContract.Event.Init(previousScreenRoute)
    }

    ObserveUiEffect(flow = effect, tag = "QrCodeScanScreen") { uiEffect ->
        when (uiEffect) {
            is QrCodeScanContract.Effect.Navigation -> onNavigation(uiEffect)

            is QrCodeScanContract.Effect.CacheDeepLinkAction -> {
                context.setPendingDeepLinkAction(uiEffect.deepLinkAction)
            }

            is QrCodeScanContract.Effect.NavigateUpAndOpenDeepLinkAction -> navigateUpAndOpenDeepLinkAction(
                uiEffect.deepLinkAction
            )

            is QrCodeScanContract.Effect.ShowToastAndNavigateUp -> {
                Toast.makeText(context, uiEffect.message, Toast.LENGTH_LONG).show()
                navigateUp()
            }
        }
    }

    ContentScreen(
        isLoading = false,
        showToolbar = true,
        onBack = { onEventSent(QrCodeScanContract.Event.OnBack) },
        navigableAction = ScreenNavigableAction.Closeable(testTag = QrCodeScanTestTags.CLOSE_BUTTON) {
            onEventSent(QrCodeScanContract.Event.OnBack)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                SPACING_SMALL.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.successData != null) {
                SuccessScreen(successLabel = state.successData.successLabel)
            } else {
                if (state.processingData != null) {
                    ProcessingScreen(processingLabel = state.processingData.processingLabel)
                } else {
                    CameraScreen(
                        onEventSent = onEventSent,
                        state = state
                    )

                    // Error message.
                    Text(
                        text = state.errorData?.errorLabel.orEmpty(),
                        color = MaterialTheme.customColors.error
                    )
                }
            }
        }
    }
}

/**
 * The success screen.
 */
@Composable
private fun SuccessScreen(successLabel: String) {
    LottieComposable(
        resourceId = R.raw.lottie_success,
        size = ConstantsUi.QrCodeScreen.LOTTIE_ANIMATION_SIZE,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = successLabel,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )
    }
}

/**
 * The processing screen.
 */
@Composable
private fun ProcessingScreen(processingLabel: String) {
    LottieComposable(
        resourceId = R.raw.lottie_processing_request,
        size = ConstantsUi.QrCodeScreen.LOTTIE_ANIMATION_SIZE,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = processingLabel,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraScreen(
    onEventSent: (QrCodeScanContract.Event) -> Unit,
    state: QrCodeScanContract.State
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cameraSize = screenWidth * 0.875f
    val cameraShape = MaterialTheme.shapes.medium
    val archColor = MaterialTheme.customColors.genericRed
    val archStrokeWidth = SPACING_TINY.dp

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    when {
        permissionState.status.isGranted -> onEventSent(QrCodeScanContract.Event.OnPermissionGranted)
        permissionState.status.shouldShowRationale -> onEventSent(QrCodeScanContract.Event.OnShowPermissionRationale)
        else -> {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    Column(
        modifier = Modifier.size(cameraSize),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { factoryContext ->
                        val previewView = PreviewView(factoryContext)
                        val preview = Preview.Builder().build()
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        preview.surfaceProvider = previewView.surfaceProvider

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(factoryContext),
                            QrCodeAnalyzer(
                                onQrCodeScanned = {
                                    onEventSent(
                                        QrCodeScanContract.Event.OnQrCodeScanned(
                                            it
                                        )
                                    )
                                },
                                onQrCodeScannedFailed = {
                                    onEventSent(
                                        QrCodeScanContract.Event.OnQrCodeScannedFailed(
                                            it
                                        )
                                    )
                                }
                            )
                        )

                        try {
                            cameraProviderFuture.get().unbindAll()
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            onEventSent(QrCodeScanContract.Event.OnCameraBindFailed(e.localizedMessage.orEmpty()))
                        }
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(cameraShape)
                )

                // Border.
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val archSize = size.minDimension * 0.05f
                    val path = drawRoundedCornerShapeWithArches(
                        size = Size(size.width, size.height),
                        archSize = archSize
                    )
                    drawPath(
                        path = path,
                        color = archColor,
                        style = Stroke(width = archStrokeWidth.toPx())
                    )
                }
            }
        } else if (state.shouldShowPermissionRationale) {
            PermissionRationaleScreen { onEventSent(QrCodeScanContract.Event.OnOpenAppSettingsButtonClicked) }
        } else {
            // Should not occur, but just in case.
            PermissionRationaleScreen { onEventSent(QrCodeScanContract.Event.OnOpenAppSettingsButtonClicked) }
        }
    }
}

@Composable
private fun PermissionRationaleScreen(openAppSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.qr_code_screen_label_camera_permission_not_granted),
            style = MaterialTheme.typography.h6
        )
        WrapButton(
            onClick = openAppSettings,
            modifier = Modifier.testTag(QrCodeScanTestTags.OPEN_APP_SETTINGS_BUTTON)
        ) {
            Text(text = stringResource(R.string.qr_code_screen_label_button_open_app_settings))
        }
    }
}

/**
 * Creates a Path object that represents a rounded corner shape with arches at each corner.
 *
 * @param size      The overall size of the shape.
 * @param archSize  The size of the arches at each corner.
 * @return          A Path object representing the shape.
 */
private fun drawRoundedCornerShapeWithArches(size: Size, archSize: Float): Path {
    return Path().apply {
        // Top left arch
        moveTo(0f, archSize)
        quadraticBezierTo(
            0f, 0f,
            archSize / 2, 0f
        )

        // Top right arch
        lineTo(size.width - archSize, 0f)
        quadraticBezierTo(size.width, 0f, size.width, archSize)

        // Bottom right arch
        lineTo(size.width, size.height - archSize)
        quadraticBezierTo(size.width, size.height, size.width - archSize, size.height)

        // Bottom left arch
        lineTo(archSize, size.height)
        quadraticBezierTo(0f, size.height, 0f, size.height - archSize)

        close()
    }
}
