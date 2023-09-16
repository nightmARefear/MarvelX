package com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.BottomBarAction
import com.alexgreece45.marvelx.util.BottomNavigationBarData
import com.alexgreece45.marvelx.util.ContentScreen

@Composable
fun AllFavoriteCharactersScreen(
    navController: NavController
) {
    ContentScreen(
        showToolbar = true,
        toolbarTitle = stringResource(R.string.all_favorite_characters_screen_toolbar_title),
        bottomNavigationBarData = BottomNavigationBarData(
            navController = navController,
            items = BottomBarAction.getDefaultBottomBarActions()
        )
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Text(
                    text = stringResource(R.string.all_favorite_characters_screen_toolbar_title),
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}