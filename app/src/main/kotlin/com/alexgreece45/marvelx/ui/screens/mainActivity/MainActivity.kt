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
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.screens.home.models.HomeAlertDialogErrorType
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEvent
import com.alexgreece45.marvelx.ui.screens.splash.SplashContract
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import com.chuckerteam.chucker.api.Chucker
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    private val appLogger: AppLogger by inject()
    private val routerHost: RouterHost by inject()
    private val securityController: SecurityController by inject()
    private val viewModel: MainActivityViewModel by viewModels()
    private val prefKeys: PrefKeys by inject()
    private val userRepository: UserRepository by inject()

    var pendingDeepLinkAction: DeepLinkAction? = null

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appLogger.d("MainActivity") { "onCreate called." }

        setWindowSecurityFlags()
        setContent {
            MarvelXTheme {
                Surface(color = MaterialTheme.colors.background) {
                    routerHost.StartFlow()

                    ShouldAppDrawOverlayWindows()
                    ChuckerPermissions()

                    OneTimeLaunchedEffect {
                        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnCreate)
                    }

                    LaunchedEffect(Unit) {
                        viewModel.uiEffect.collect { effect ->
                            when (effect) {
                                is MainActivityEffect.Navigation.NavigateToHomeScreenWithSessionExpired -> {
                                    NavArgHelper.setArgument(
                                        NavArg.HomeAlertDialogErrorType,
                                        HomeAlertDialogErrorType.SessionExpired
                                    )
                                    routerHost.logout()
                                }

                                is MainActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation -> {
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

                                is MainActivityEffect.OpenOrCacheDeepLink -> {
                                    appLogger.d("MainActivity") { "opening or caching deep link..." }
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

                    RetrieveFcmToken(
                        onFcmTokenReceived = {
                            viewModel.setEvent(MainActivityEvent.OnFcmTokenReceived(it))
                        }
                    )
                }
            }
        }
        handleDeepLink(intent)
    }

    override fun onPause() {
        super.onPause()

        appLogger.d("MainActivity") { "onPause called." }
        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnPause)
    }

    override fun onResume() {
        super.onResume()

        appLogger.d("MainActivity") { "onResume called." }
        if (routerHost.flowStarted) {
            viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnResume)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        appLogger.d("MainActivity") { "onDestroy called." }
        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnDestroy(willBeRecreated = !isFinishing))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        appLogger.d("MainActivity") { "onUserInteraction called." }
        if (userRepository.isUserLoggedIn && prefKeys.shouldRememberUserSession()) {
            viewModel.setEvent(MainActivityEvent.OnUserInteraction)
        }
    }

    override fun isInMultiWindowMode(): Boolean {
        appLogger.d("MainActivity") { "isInMultiWindowMode called." }

        val isInMultiWindowMode = super.isInMultiWindowMode()
        if (isInMultiWindowMode && !securityController.shouldApplicationBeInMultiWindowMode()) {
            viewModel.setEvent(MainActivityEvent.ApplicationInMultiWindowModeViolation)
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

    @Composable
    private fun RetrieveFcmToken(onFcmTokenReceived: (String?) -> Unit) {
        OneTimeLaunchedEffect {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    try {
                        appLogger.d("MainActivity") { "Retrieved FCM token: ${task.result}." }

                        onFcmTokenReceived(task.result)
                    } catch (e: Exception) {
                        appLogger.e("MainActivity") { "Failed to get FCM token:\n" + e.message.toString() }
                    }
                }.addOnFailureListener { _ ->
                    onFcmTokenReceived(null)
                }
            } catch (e: Exception) {
                appLogger.e("MainActivity") { "Failed to get FCM token:\n" + e.message.toString() }
                onFcmTokenReceived(null)
            }
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
     * [MainActivityEvent.ApplicationDrawOverlayWindowsViolation] is set.
     */
    @Composable
    private fun ShouldAppDrawOverlayWindows() {
        if (securityController.shouldApplicationDrawOverlayWindows().not()) {
            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (Settings.canDrawOverlays(this@MainActivity)) {
                            viewModel.setEvent(MainActivityEvent.ApplicationDrawOverlayWindowsViolation)
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
        appLogger.d("MainActivity") { "(handleDeepLink) triggered." }
        intent?.data?.let { uri ->
            viewModel.setEvent(MainActivityEvent.OnDeepLinkReceived(uri))
        }
    }
}
