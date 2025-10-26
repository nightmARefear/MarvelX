package com.alexgreece45.marvelx.ui.screens.mainActivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.screens.home.models.HomeAlertDialogErrorType
import com.alexgreece45.marvelx.ui.screens.splash.SplashContract
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import com.chuckerteam.chucker.api.Chucker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    private val logger: AppLogger by inject()
    private val routerHost: RouterHost by inject()
    private val securityController: SecurityController by inject()
    private val viewModel: ActivityViewModel by viewModels()
    private val prefKeys: PrefKeys by inject()

    var pendingDeepLinkAction: DeepLinkAction? = null

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.d("MainActivity") { "onCreate called." }

        setWindowSecurityFlags()
        setContent {
            MarvelXTheme {
                Surface(color = MaterialTheme.colors.background) {
                    routerHost.StartFlow()

                    ShouldAppDrawOverlayWindows()
                    ChuckerPermissions()

                    OneTimeLaunchedEffect {
                        viewModel.setEvent(ActivityEvent.LifecycleEvent.OnCreate)
                    }

                    LaunchedEffect(Unit) {
                        viewModel.uiEffect.collect { effect ->
                            when (effect) {
                                is ActivityEffect.Navigation.NavigateToHomeScreenWithSessionExpired -> {
                                    NavArgHelper.setArgument(
                                        NavArg.HomeAlertDialogErrorType,
                                        HomeAlertDialogErrorType.SessionExpired
                                    )
                                    routerHost.logout()
                                }

                                is ActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation -> {
                                    NavArgHelper.setArgument(
                                        NavArg.SplashAlertDialogErrorType,
                                        SplashContract.AlertDialogErrorType.InsecureApplication(
                                            securityViolation = effect.securityViolation
                                        )
                                    )
                                    routerHost.getNavController().let { navController ->
                                        navController.navigate(Screen.SplashScreen.route) {
                                            popUpToTop(navController)
                                        }
                                    }
                                }

                                is ActivityEffect.OpenOrCacheDeepLink -> {
                                    logger.d("MainActivity") { "opening or caching deep link..." }
                                    if (routerHost.canCurrentRouteRunDeepLink()) {
                                        openDeepLinkAction(
                                            navController = routerHost.getNavController(),
                                            deepLinkAction = effect.deepLinkAction
                                        )
                                    } else {
                                        pendingDeepLinkAction = effect.deepLinkAction
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        handleDeepLink(intent)
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
        viewModel.setEvent(ActivityEvent.LifecycleEvent.OnDestroy(willBeRecreated = !isFinishing))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        logger.d("MainActivity") { "onUserInteraction called." }
        if (prefKeys.isUserLoggedIn() && prefKeys.shouldRememberUserSession()) {
            viewModel.setEvent(ActivityEvent.OnUserInteraction)
        }
    }

    override fun isInMultiWindowMode(): Boolean {
        logger.d("MainActivity") { "isInMultiWindowMode called." }

        val isInMultiWindowMode = super.isInMultiWindowMode()
        if (isInMultiWindowMode && !securityController.shouldApplicationBeInMultiWindowMode()) {
            viewModel.setEvent(ActivityEvent.ApplicationInMultiWindowModeViolation)
        }

        return isInMultiWindowMode
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (routerHost.flowStarted) {
            handleDeepLink(intent)
        } else {
            delayDeepLinkHandling(intent)
        }
    }

    /**
     * Delays the handling of the deep link if the navigation graph has not been set yet.
     */
    private fun delayDeepLinkHandling(intent: Intent) {
        lifecycleScope.launch {
            val maxCount = 20
            var count = 0
            while (!routerHost.flowStarted && count <= maxCount) {
                count++
                delay(250L)
            }

            // Ensures that no more than 5 seconds have elapsed.
            if (count <= maxCount) {
                handleDeepLink(intent)
            }
        }
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
    private fun ShouldAppDrawOverlayWindows() {
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
    private fun ChuckerPermissions() {
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

    /**
     * Handles the received deep link.
     */
    private fun handleDeepLink(intent: Intent?) {
        logger.d("MainActivity") { "(handleDeepLink) triggered." }
        intent?.data?.let { uri ->
            viewModel.setEvent(ActivityEvent.OnDeepLinkReceived(uri))
        }
    }
}
