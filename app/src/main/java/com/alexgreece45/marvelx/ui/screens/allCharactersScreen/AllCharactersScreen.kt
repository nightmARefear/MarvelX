package com.alexgreece45.marvelx.ui.screens.allCharactersScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersEffect
import com.alexgreece45.marvelx.domain.viewmodels.AllCharactersViewModel
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.GeneralErrorScreen
import com.alexgreece45.marvelx.util.MAX_CHARACTER_IMAGE_HEIGHT
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun AllCharacterScreen(
    navigator: DestinationsNavigator,
    viewModel: AllCharactersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AllCharactersEffect.NavigateToCharacterDetails -> TODO()
            }
        }
    }

    ContentScreen(
        toolBar = true,
        toolbarTitle = R.string.all_characters_screen_toolbar_title
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                state.errorMessage?.let {
                    GeneralErrorScreen(errorMessage = it)
                }
            }

            if (state.characters.isNotEmpty()) {
                items(state.characters,
                    key = { character ->
                        character.id
                    }
                ) { character ->
                    CharacterItem(character = character)
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: CharacterDomain
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Name.
        Text(
            text = character.name
        )

        // Image.
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(MAX_CHARACTER_IMAGE_HEIGHT.dp),
            model = ImageRequest.Builder(context)
                .data(character.thumbnail.path + "." + character.thumbnail.extension)
                .crossfade(true)
                .build(),
            contentDescription = "${character.name} image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_broken_image)
        )

        if (character.description.isBlank().not()) {
            Text(
                text = character.description,
                style = MaterialTheme.typography.body1.copy(
                    textAlign = TextAlign.Justify
                )
            )
        } else {
            Text(
                text = stringResource(R.string.no_character_description),
                style = MaterialTheme.typography.body1.copy(
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}