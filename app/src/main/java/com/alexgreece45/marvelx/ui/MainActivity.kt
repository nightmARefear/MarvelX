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
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.viewModels.ActivityEffect
import com.alexgreece45.marvelx.viewModels.ActivityEvent
import com.alexgreece45.marvelx.viewModels.ActivityViewModel
import com.chuckerteam.chucker.api.Chucker
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {

    private val logger: AppLoggerController by inject()
    private val prefKeys: PrefKeys by inject()
    private val routerHost: RouterHost by inject()
    private val viewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinContext {
                MarvelXTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        val state by viewModel.uiState.collectAsStateWithLifecycle()

                        routerHost.StartFlow()
                        ChuckerPermissions()

                        state.mainActivityErrorDialog?.let { mainActivityErrorDialog ->
                            AlertDialogError(
                                isVisible = true,
                                title = mainActivityErrorDialog.title,
                                errorMessage = mainActivityErrorDialog.errorMessage,
                                confirmButton = AlertDialogButton(
                                    value = stringResource(R.string.generic_ok_button),
                                    buttonType = ErrorButtonType.Text,
                                    onClick = mainActivityErrorDialog.onOkClick
                                )
                            )
                        }

                        OneTimeLaunchedEffect {
                            viewModel.setEvent(ActivityEvent.Init)
                        }

                        LaunchedEffect(Unit) {
                            viewModel.uiEffect.collect { activityEffect ->
                                when (activityEffect) {
                                    ActivityEffect.Navigation.NavigateToHomeScreen -> {
                                        routerHost.logout()
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

        logger.d("MainActivity") { "onResume called." }
        if (prefKeys.isUserLoggedIn() && prefKeys.shouldRememberUserSession().not()) {
            viewModel.setEvent(ActivityEvent.LogoutUser)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        logger.d("MainActivity") { "onDestroy called." }
        viewModel.setEvent(ActivityEvent.OnDestroy)
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