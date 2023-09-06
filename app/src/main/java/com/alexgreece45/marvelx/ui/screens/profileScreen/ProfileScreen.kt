package com.alexgreece45.marvelx.ui.screens.profileScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.alexgreece45.marvelx.util.ContentScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator
) {
    ContentScreen(
        onBack = {
            navigator.popBackStack()
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Text(
                    text = "ProfileScreen",
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}