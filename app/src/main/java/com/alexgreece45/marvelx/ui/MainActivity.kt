package com.alexgreece45.marvelx.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.alexgreece45.marvelx.domain.viewmodels.ActivityEvent
import com.alexgreece45.marvelx.domain.viewmodels.ActivityViewModel
import com.alexgreece45.marvelx.navigation.Navigation
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityViewModel: ActivityViewModel by viewModels()

        setContent {
            MarvelXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Navigation(
                        activityState = activityViewModel.uiState.collectAsState(),
                        onSuccessNavigate = { userId ->
                            activityViewModel.setEvent(
                                ActivityEvent.SetUserId(
                                    userId = userId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}