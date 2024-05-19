package com.alexgreece45.marvelx.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.NavItem
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import com.alexgreece45.marvelx.viewModels.ActivityEffect
import com.alexgreece45.marvelx.viewModels.ActivityEvent
import com.alexgreece45.marvelx.viewModels.ActivityViewModel
import com.chuckerteam.chucker.api.Chucker
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    private val logger: AppLoggerController by inject()
    private val routerHost: RouterHost by inject()
    private val securityController: SecurityController by inject()
    private val viewModel: ActivityViewModel by viewModels()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowSecurityFlags()
        setContent {
            KoinAndroidContext {
                MarvelXTheme {
                    Surface(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures {
                                viewModel.setEvent(ActivityEvent.OnTapGestureDetected)
                            }
                        },
                        color = MaterialTheme.colors.background
                    ) {
                        val state by viewModel.uiState.collectAsStateWithLifecycle()

                        routerHost.StartFlow()

                        ShouldAppDrawOverlayWindows()
                        ChuckerPermissions()

                        state.mainActivityErrorDialog?.let { mainActivityErrorDialog ->
                            AlertDialogError(
                                isVisible = true,
                                title = mainActivityErrorDialog.title,
                                errorMessage = mainActivityErrorDialog.errorMessage,
                                confirmButton = AlertDialogButton(
                                    value = stringResource(R.string.generic_ok_button),
                                    onClick = mainActivityErrorDialog.onOkClick
                                )
                            )
                        }

                        OneTimeLaunchedEffect {
                            viewModel.setEvent(ActivityEvent.LifecycleEvent.OnCreate)
                        }

                        LaunchedEffect(Unit) {
                            viewModel.uiEffect.collect { activityEffect ->
                                when (activityEffect) {
                                    is ActivityEffect.Navigation.NavigateToHomeScreenWithSessionExpired -> {
                                        NavArgHelper.setArgument(
                                            NavItem.HasSessionExpired,
                                            true
                                        )
                                        NavArgHelper.setArgument(NavItem.ShowErrorDialog, true)
                                        routerHost.logout()
                                    }

                                    ActivityEffect.Navigation.NavigateToSplashScreenWithApplicationInsecureError -> {
                                        NavArgHelper.setArgument(
                                            NavItem.IsApplicationInsecure,
                                            true
                                        )
                                        routerHost.getNavController().let { navController ->
                                            navController.navigate(Screen.SplashScreen.route) {
                                                popUpToTop(navController)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        logger.d("MainActivity") { "onPause called." }
        viewModel.setEvent(ActivityEvent.LifecycleEvent.OnPause)
    }

    override fun onResume() {
        super.onResume()

        logger.d("MainActivity") { "onResume called." }
        if (routerHost.flowStarted) {
            viewModel.setEvent(ActivityEvent.LifecycleEvent.OnResume)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        logger.d("MainActivity") { "onDestroy called." }
        viewModel.setEvent(ActivityEvent.LifecycleEvent.OnDestroy)
    }

    /**
     * Sets security flags for the [Window] of [MainActivity].
     */
    private fun setWindowSecurityFlags() {
        if (securityController.isScreenCapturingBlocked()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        @SuppressLint("MissingPermission", "NewApi")
        if (securityController.areOverlayWindowsHidden()) {
            window.setHideOverlayWindows(true)
        }
    }

    /**
     * Checks whether this application, through [SecurityController.shouldApplicationDrawOverlayWindows]
     * should have the ability to draw overlay windows over other applications, and if it doesn't it
     * checks if the application can has this ability. If it has it, then
     * [ActivityEvent.ApplicationDrawOverlayWindowsViolation] is set.
     */
    @Composable
    fun ShouldAppDrawOverlayWindows() {
        if (securityController.shouldApplicationDrawOverlayWindows().not()) {
            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (Settings.canDrawOverlays(this@MainActivity)) {
                            viewModel.setEvent(ActivityEvent.ApplicationDrawOverlayWindowsViolation)
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the user has the "POST_NOTIFICATIONS" permission, and if not, requests it, so that
     * [Chucker] can be installed if the app is debuggable.
     *
     * ### Note: Without this composable, [Chucker] cannot be installed as it is necessary that the app has the "POST_NOTIFICATIONS" permission.
     */
    @Composable
    fun ChuckerPermissions() {
        if (Chucker.isOp
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
}