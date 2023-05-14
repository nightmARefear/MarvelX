package com.alexgreece45.marvelx.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.models.items.CharacterItem
import com.alexgreece45.marvelx.domain.models.*
import com.alexgreece45.marvelx.ui.character_screen.CharacterScreen
import com.alexgreece45.marvelx.ui.destinations.CharacterScreenDestination
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarvelXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}

@RootNavGraph(start = true)
@Destination("allCharacters")
@Composable
fun AllCharactersScreen(
    navigator: DestinationsNavigator,
    viewModel: AllCharactersViewModel = hiltViewModel(),
) {
    viewModel.setEvent(AllCharactersEvent.GetAllCharacters)
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        if (viewModel.viewState.value.errorMessage.isNullOrBlank().not()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.viewState.value.errorMessage ?: "",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        viewModel.setEvent(
                            AllCharactersEvent.GetAllCharacters
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.retry_message).uppercase()
                    )
                }
            }
            return@Scaffold
        }

        if (viewModel.viewState.value.characters.isNotEmpty()) {
            /*if (viewModel.viewState.value.character != null) {
                CharacterScreen(
                    viewModel.viewState.value.character!!
                ) {
                    viewModel.setCharacterToNull()
                }
                return@Scaffold
            }*/

            LazyColumn(
//                    contentPadding = it,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = it.calculateTopPadding() + 16.dp,
                    end = 16.dp,
                    bottom = it.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = viewModel.viewState.value.characters,
                    key = { character ->
                        character.id
                    }
                ) { character ->
                    CharacterItem(character = character) {
                        viewModel.setEvent(
                            AllCharactersEvent.LoadCharacter(
                                characterId = character.id
                            )
                        )
                    }
                }
            }
            return@Scaffold
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is AllCharactersEffect.ShowCharacterDetails -> {
                    navigator.navigate(CharacterScreenDestination(characterId = effect.characterId))
                }
            }
        }
    }
}

    