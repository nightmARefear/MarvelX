package com.alexgreece45.marvelx.ui.screens.comicScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.viewmodels.ComicEvent
import com.alexgreece45.marvelx.domain.viewmodels.ComicViewModel
import com.alexgreece45.marvelx.util.ContentScreen
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun ComicScreen(
    navigator: DestinationsNavigator,
    viewModel: ComicViewModel = hiltViewModel(),
    comicId: Int
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            ComicEvent.InitView(
                comicId = comicId
            )
        )

        viewModel.effect.collect { comicEffect ->
            when (comicEffect) {
                else -> TODO("to be implemented")
            }
        }
    }

    ContentScreen(
        onBack = { navigator.popBackStack() },
        toolBar = true,
        toolbarTitle = "...",
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                state.comic?.let { comic ->
                    ComicItem(
                        comic = comic
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicItem(comic: ComicDomain) {
    Text(
        text = comic.toString()
    )
}