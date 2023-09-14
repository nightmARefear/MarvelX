package com.alexgreece45.marvelx.ui.screens.profileScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.screens.destinations.AllCharactersScreenDestination
import com.alexgreece45.marvelx.ui.screens.destinations.AllFavoriteCharactersScreenDestination
import com.alexgreece45.marvelx.ui.screens.destinations.ProfileScreenDestination
import com.alexgreece45.marvelx.util.BottomBarAction
import com.alexgreece45.marvelx.util.BottomNavigationBarData
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.IconData
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ProfileScreen(
    navController: NavController
) {
    ContentScreen(
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
        bottomNavigationBarData = BottomNavigationBarData(
            navController = navController,
            items = listOf(
                BottomBarAction(
                    route = AllCharactersScreenDestination,
                    label = stringResource(R.string.bottom_navigation_label_characters),
                    iconData = IconData(
                        iconResource = R.drawable.ic_close_32,
                        contentDescription = stringResource(R.string.bottom_navigation_label_characters),
                        colorTint = null
                    )
                ),
                BottomBarAction(
                    route = AllFavoriteCharactersScreenDestination,
                    label = stringResource(R.string.bottom_navigation_label_favorites),
                    iconData = IconData(
                        iconResource = R.drawable.ic_favorite_filled_24,
                        contentDescription = stringResource(R.string.bottom_navigation_label_favorites),
                        colorTint = null
                    )
                ),
                BottomBarAction(
                    route = ProfileScreenDestination,
                    label = stringResource(R.string.bottom_navigation_label_profile),
                    iconData = IconData(
                        iconResource = R.drawable.ic_person_32,
                        contentDescription = stringResource(R.string.bottom_navigation_label_profile),
                        colorTint = null
                    )
                )
            )
        )
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