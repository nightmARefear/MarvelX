package com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.AllFavoriteCharactersViewModel
import com.alexgreece45.marvelx.ui.BottomBarItem
import com.alexgreece45.marvelx.ui.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.ContentScreen

@Composable
fun AllFavoriteCharactersScreen(
    bottomNavigationBarData: (items: List<BottomBarItem>, backgroundColor: Color, topHorizontalColor: Color?, selectedItemTopHorizontalColor: Color?, elevation: Dp) -> BottomNavigationBarData,
    viewModel: AllFavoriteCharactersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ContentScreen(
        isLoading = false,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.all_favorite_characters_screen_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData(
            state.bottomNavigationItems,
            MaterialTheme.colors.background,
            MaterialTheme.colors.onBackground,
            MaterialTheme.colors.primaryVariant.copy(alpha = 0.8f),
            16.dp,
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