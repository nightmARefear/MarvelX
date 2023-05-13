package com.alexgreece45.marvelx.ui.character_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain

@Composable
fun CharacterScreen(
    character: CharacterDomain,
    onBackClicked: () -> Unit // to be removed
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // todo: to be removed
        TextButton(
            onClick = {
                onBackClicked()
            }
        ) {
            Text(text = "back")
        }

        // basic info
        Text(
            modifier = Modifier.padding(0.dp),
            text = character.name,
//            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(character.thumbnail.path + "." + character.thumbnail.extension)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_broken_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
        )
        if (character.description.isNullOrEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(R.string.no_character_description),
                style = MaterialTheme.typography.body1,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
        } else {
            Text(
                text = character.description,
                textAlign = TextAlign.Justify,
            )
        }

        // Comics

        // Stories

        // Events

        // Series
    }
}