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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.models.items.CharacterItem
import com.alexgreece45.marvelx.domain.models.CharacterEvents
import com.alexgreece45.marvelx.domain.models.CharacterViewModel
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: CharacterViewModel by viewModels()

        setContent {
            MarvelXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CharacterScreen(viewModel)
                }
            }
        }
    }

    @Composable
    private fun CharacterScreen(
        viewModel: CharacterViewModel
    ) {
        if (!viewModel.viewState.value.errorMessage.isNullOrBlank()) {
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
                            CharacterEvents.GetAllCharacters
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.retry_message).uppercase()
                    )
                }
            }
            return
        }

        if (viewModel.viewState.value.characters.isNotEmpty()) {
            Scaffold {
                LazyColumn(
                    contentPadding = it
                ) {
                    items(
                        items = viewModel.viewState.value.characters,
                        key = { character ->
                            character.id
                        }
                    ) { character ->
                        CharacterItem(character = character) {

                        }
                    }
                }
            }
            return
        }

        TextButton(
            modifier = Modifier.wrapContentSize(),
            onClick = {
                viewModel.setEvent(
                    CharacterEvents.GetAllCharacters
                )
            }
        ) {
            Text(
                text = "SHOW HEROES"
            )
        }
    }
}
    