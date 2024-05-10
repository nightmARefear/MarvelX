package com.alexgreece45.marvelx.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.Navigation
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.alexgreece45.marvelx.viewModels.ActivityEffect
import com.alexgreece45.marvelx.viewModels.ActivityEvent
import com.alexgreece45.marvelx.viewModels.ActivityViewModel
import com.chuckerteam.chucker.api.Chucker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var logger: AppLoggerController

    @Inject
    lateinit var prefKeys: PrefKeys

    private val activityViewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarvelXTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val state by activityViewModel.uiState.collectAsStateWithLifecycle()
                    val navController: NavController = rememberNavController()

                    Navigation(navController = navController)
                    ChuckerPermissions()

                    state.mainActivityErrorDialog?.let { mainActivityErrorDialog ->
                        AlertDialogError(
                            isVisible = true,
                            title = mainActivityErrorDialog.title,
                            errorMessage = mainActivityErrorDialog.errorMessage,
                            confirmButton = AlertDialogButton(
                                value = stringResource(R.string.generic_ok_button),
                                buttonType = ErrorButtonType.Text,
                                onClick = { activityViewModel.setEvent(mainActivityErrorDialog.onOkClick) }
                            )
                        )
                    }

                    OneTimeLaunchedEffect {
                        activityViewModel.setEvent(ActivityEvent.Init)
                    }

                    LaunchedEffect(Unit) {
                        activityViewModel.uiEffect.collect { activityEffect ->
                            when (activityEffect) {
                                ActivityEffect.Navigation.NavigateToHomeScreen -> {
                                    navController.navigate(Screen.RegisterScreens.HomeScreen.route) {
                                        popUpTo(Screen.BottomNavigationScreens.getNestedGraphRootRoute()) {
                                            inclusive = true
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

    override fun onResume() {
        super.onResume()

        if (!prefKeys.isUserLoggedIn()) {
            return
        }

        if (!prefKeys.shouldRememberUserSession()) {
            activityViewModel.setEvent(ActivityEvent.LogoutUser)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d { "MainActivity's onDestroy called." }

        // Cancel worker when the app closes.
        WorkManager.getInstance(this).cancelUniqueWork(ConstantsWorker.RefreshToken.WORKER_NAME)
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