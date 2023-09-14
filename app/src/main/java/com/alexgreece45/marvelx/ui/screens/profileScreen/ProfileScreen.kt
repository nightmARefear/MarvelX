package com.alexgreece45.marvelx.ui.screens.profileScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.ContentScreen

@Composable
fun ProfileScreen(
    navController: NavController
) {
    ContentScreen(
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Text(
                    text = "Profile Screen",
                    style = MaterialTheme.typography.h2
                )
            }
        }
    }
}