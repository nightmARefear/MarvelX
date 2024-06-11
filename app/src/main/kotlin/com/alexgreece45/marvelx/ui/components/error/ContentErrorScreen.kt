package com.alexgreece45.marvelx.ui.components.error

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer

/**
 * This [Composable] represents an erroneous screen. The default navigation icon is `Close`.
 *
 * @param errorTitle            The error title. It has a default value of [R.string.generic_error].
 * @param errorSubtitle         The error subtitle. It has a default value of
 * [R.string.generic_unknown_error].
 * @param showErrorAnimation    Whether the error animation should be displayed. Set to `true` by
 * default.
 * @param onClose               The action(s) to be invoked when the `Close` button in the toolbar
 * is clicked. This button is optional.
 * @param errorButtonLayout     The layout of the error buttons. Either `Single` or `Double`.
 */
@Composable
fun ContentErrorScreen(
    errorTitle: String = stringResource(R.string.generic_error),
    errorSubtitle: String = stringResource(id = R.string.generic_unknown_error),
    showErrorAnimation: Boolean = true,
    onClose: (() -> Unit)? = null,
    errorButtonLayout: ErrorButtonLayout
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = R.raw.lottie_generic_error
        )
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    ContentScreen(
        isLoading = false,
        navigableAction = onClose?.let {
            ScreenNavigableAction.Closeable(
                onClose = onClose
            )
        } ?: ScreenNavigableAction.None,
        showToolbar = true,
        stickyBottom = {
            Column(verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)) {
                when (errorButtonLayout) {
                    is ErrorButtonLayout.Single -> {
                        WrapButton(
                            onClick = errorButtonLayout.onPrimaryButtonClicked,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true
                        ) {
                            Text(
                                text = errorButtonLayout.primaryButtonLabel.ifBlank {
                                    stringResource(R.string.generic_retry_button)
                                }
                            )
                        }
                    }

                    is ErrorButtonLayout.Double -> {
                        WrapButton(
                            onClick = errorButtonLayout.onPrimaryButtonClicked,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true
                        ) {
                            Text(
                                text = errorButtonLayout.primaryButtonLabel.ifBlank {
                                    stringResource(R.string.generic_retry_button)
                                }
                            )
                        }

                        WrapOutlinedButton(onClick = errorButtonLayout.onSecondaryButtonClicked) {
                            Text(
                                text = errorButtonLayout.secondaryButtonLabel.ifBlank {
                                    stringResource(R.string.generic_retry_button)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContentErrorScreenHeader(title = errorTitle, subtitle = errorSubtitle)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (showErrorAnimation) {
                    LottieAnimation(
                        modifier = Modifier.size(524.dp),
                        composition = composition,
                        progress = { progress }
                    )
                }
            }
            VSpacer.Medium()
        }

        // Override native back handling logic.
        BackHandler(enabled = true) {
            onClose?.invoke()
        }
    }
}

@Composable
private fun ContentErrorScreenHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SPACING_MEDIUM.dp)
    ) {
        // Title.
        Text(
            text = title,
            style = MaterialTheme.typography.h5
        )

        // Subtitle.
        Text(
            text = subtitle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 5,
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(
    apiLevel = 34,
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ContentErrorScreenPreview() {
    ContentErrorScreen(
        errorSubtitle = stringResource(R.string.no_internet_connection_error_message),
        onClose = {},
        errorButtonLayout = ErrorButtonLayout.Single(onPrimaryButtonClicked = {})
    )
}