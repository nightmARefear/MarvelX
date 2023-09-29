package com.alexgreece45.marvelx.ui.screens.profileScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.ActivityState
import com.alexgreece45.marvelx.util.BottomBarAction
import com.alexgreece45.marvelx.util.BottomNavigationBarData
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer

@Composable
fun ProfileScreen(
    navController: NavController,
    activityState: State<ActivityState>
) {

    LaunchedEffect(Unit) {

    }

    ContentScreen(
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
        bottomNavigationBarData = BottomNavigationBarData(
            navController = navController,
            items = BottomBarAction.getDefaultBottomBarActions()
        )
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                // TODO: TO BE REMOVED!
                // userId
                Text(
                    text = activityState.value.userId.toString(),
                    style = MaterialTheme.typography.h4
                )

                // Profile picture.
                // todo: add call to retrieve profile picture

                Section(title = "Personal Information") {
                    Item(
                        label = "Full Name",
                        value = "John Doe"
                    )

                    VSpacer.Large()

                    Item(
                        label = "Email",
                        value = "john.doe@email.com"
                    )

                    VSpacer.Large()

                    Item(
                        label = "Gender",
                        value = "Male"
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SPACING_SMALL.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5
            )
            VSpacer.Medium()
            content()
        }
    }
}

@Composable
private fun Item(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label.
        Text(
            text = label,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = value,
            style = MaterialTheme.typography.subtitle1
        )
    }
}