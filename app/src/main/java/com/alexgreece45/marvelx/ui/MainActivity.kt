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
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.alexgreece45.marvelx.viewModels.ActivityEvent
import com.alexgreece45.marvelx.viewModels.ActivityViewModel
import com.alexgreece45.marvelx.ui.navigation.Navigation
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.chuckerteam.chucker.api.Chucker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityViewModel: ActivityViewModel by viewModels()

        setContent {
            MarvelXTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(
                        activityState = activityViewModel.uiState.collectAsState(),
                        setUserId = { userId ->
                            activityViewModel.setEvent(
                                ActivityEvent.SetUserId(
                                    userId = userId
                                )
                            )
                        }
                    )
                    ChuckerPermissions()
                }

                // used for debugging purposes; todo: to be removed
                LaunchedEffect(Unit) {
                    activityViewModel.setEvent(ActivityEvent.InitView)
                }
            }
        }
    }

    /**
     * Checks if the user has the "POST_NOTIFICATIONS" permission, and if not, requests it,
     * so that [Chucker] can be installed if the app is debuggable.
     *
     *### Note: Without this composable, [Chucker] cannot be installed as it is necessary that the app has the "POST_NOTIFICATIONS" permission.
     * */
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