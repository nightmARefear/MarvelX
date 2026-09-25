package com.alexgreece45.marvelx.ui.screens.splash

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.util.extensions.finish
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    popUpToHomeScreen: () -> Unit,
    popUpToIntroLoadingScreen: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    OneTimeLaunchedEffect {
        viewModel.setEvent(SplashContract.Event.Init)
    }

    ObserveUiEffect(flow = viewModel.uiEffect, tag = "SplashScreen") { effect ->
        when (effect) {
            SplashContract.Effect.Navigation.NavigateToHomeScreen -> {
                popUpToHomeScreen()
            }

            SplashContract.Effect.Navigation.CloseApplication -> {
                context.finish()
            }

            SplashContract.Effect.Navigation.NavigateToIntroLoadingScreen -> {
                popUpToIntroLoadingScreen()
            }
        }
    }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    ContentScreen(isLoading = false) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
        ) {
            state.error?.let { splashScreenError ->
                AlertDialogError(
                    title = splashScreenError.title,
                    errorMessage = { Text(text = splashScreenError.errorMessage) },
                    confirmButton = AlertDialogButton(
                        label = stringResource(R.string.splash_screen_alert_dialog_confirm_button),
                        onClick = splashScreenError.onCloseClick
                    )
                )
            }

            AnimatedVisibility(visible = state.isAnimationVisible) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = rememberAsyncImagePainter(R.drawable.splash_animation, imageLoader),
                    contentDescription = stringResource(R.string.content_description_splash_loading_animation)
                )
            }
        }
    }
}