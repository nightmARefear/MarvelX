package com.alexgreece45.marvelx.ui.screens.splashScreen

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.viewModels.SplashEffect
import com.alexgreece45.marvelx.viewModels.SplashEvent
import com.alexgreece45.marvelx.viewModels.SplashViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(SplashEvent.InitView)

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                SplashEffect.Navigation.NavigateToHomeScreen -> {
                    navController.navigate(Screen.RegisterScreens.HomeScreen.route) {
                        popUpTo(Screen.RegisterScreens.HomeScreen.route)
                    }
                }

                SplashEffect.Navigation.CloseApplication -> {
                    (context as Activity).finish()
                }
            }
        }
    }

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

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
                    errorMessage = splashScreenError.errorMessage,
                    confirmButton = AlertDialogButton(
                        value = stringResource(R.string.splash_screen_alert_dialog_confirm_button),
                        onClick = { viewModel.setEvent(splashScreenError.onCloseClick) }
                    )
                )
            }

            AnimatedVisibility(visible = state.isAnimationVisible) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = rememberAsyncImagePainter(R.drawable.splash_animation, imageLoader),
                    contentDescription = null,
                )
            }
        }
    }
}