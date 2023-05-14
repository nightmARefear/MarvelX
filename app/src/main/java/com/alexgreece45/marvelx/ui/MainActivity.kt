package com.alexgreece45.marvelx.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.alexgreece45.marvelx.domain.viewmodels.*
import com.alexgreece45.marvelx.ui.screens.NavGraphs
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

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

/*@RootNavGraph(start = true)
@Destination(*//*"allCharacters"*//*)
@Composable
fun AllCharactersScreen(
    navigator: DestinationsNavigator,
    viewModel: AllCharactersViewModel = hiltViewModel(),
) {
    //viewModel.setEvent(AllCharactersEvent.LoadAllCharacters)
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        if (viewModel.uiState.value.errorMessage.isNullOrBlank().not()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.uiState.value.errorMessage ?: "",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        viewModel.setEvent(
                            AllCharactersEvent.LoadAllCharacters
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

        if (viewModel.uiState.value.characters.isNotEmpty()) {
            *//*if (viewModel.viewState.value.character != null) {
                CharacterScreen(
                    viewModel.viewState.value.character!!
                ) {
                    viewModel.setCharacterToNull()
                }
                return@Scaffold
            }*//*

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
                    items = viewModel.uiState.value.characters,
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
                is AllCharactersEffect.NavigateToCharacterDetails -> {
                    navigator.navigate(CharacterScreenDestination(characterId = effect.characterId))
                }
            }
        }.collect()
    }
}*/

    