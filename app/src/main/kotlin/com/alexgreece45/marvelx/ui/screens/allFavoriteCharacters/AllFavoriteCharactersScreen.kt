package com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllFavoriteCharactersScreen(
    onEventSent: (AllFavoriteCharactersEvent) -> Unit,
    state: AllFavoriteCharactersState,
    effect: Flow<AllFavoriteCharactersEffect>,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
) {
    LaunchedEffect(Unit) {
        onEventSent(AllFavoriteCharactersEvent.Init)
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.all_favorite_characters_screen_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData()
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            items(state.favoriteCharacters) { favoriteCharacter ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Green)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Image.
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(favoriteCharacter.imageDownloadUrl)
                                .crossfade(true)
                                .fallback(R.drawable.ic_broken_image)
                                .build(),
                            contentDescription = favoriteCharacter.imageContentDescription,
                            contentScale = ContentScale.FillBounds,
                            error = painterResource(R.drawable.ic_broken_image)
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SPACING_TINY.dp)
                                .basicMarquee()
                                .align(Alignment.BottomCenter),
                            text = favoriteCharacter.name,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}