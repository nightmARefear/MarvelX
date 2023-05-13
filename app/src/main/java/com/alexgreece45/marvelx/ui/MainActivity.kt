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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.models.items.CharacterItem
import com.alexgreece45.marvelx.domain.models.*
import com.alexgreece45.marvelx.ui.character_screen.CharacterScreen
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val allCharactersViewModel: AllCharactersViewModel by viewModels()
//    private val characterViewModel: CharacterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarvelXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AllCharactersScreen()
                }
            }
        }
    }

    @Composable
    private fun AllCharactersScreen() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if (!allCharactersViewModel.viewState.value.errorMessage.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = allCharactersViewModel.viewState.value.errorMessage ?: "",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            allCharactersViewModel.setEvent(
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

            if (allCharactersViewModel.viewState.value.characters.isNotEmpty()) {
                if (allCharactersViewModel.viewState.value.character != null) {
                    CharacterScreen(
                        allCharactersViewModel.viewState.value.character!!
                    ) {
                        allCharactersViewModel.setCharacterToNull()
                    }
                    return@Scaffold
                }

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
                        items = allCharactersViewModel.viewState.value.characters,
                        key = { character ->
                            character.id
                        }
                    ) { character ->
                        CharacterItem(character = character) {
                            allCharactersViewModel.setEvent(
                                AllCharactersEvent.LoadCharacter(
                                    characterId = character.id
                                )
                            )
                        }
                    }
                }
                return@Scaffold
            }

            TextButton(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    allCharactersViewModel.setEvent(
                        AllCharactersEvent.GetAllCharacters
                    )
                }
            ) {
                Text(
                    text = "SHOW HEROES",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
    