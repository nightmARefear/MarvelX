package com.alexgreece45.marvelx.data.models.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.format

@Composable
fun CharacterItem(
    character: CharacterDomain,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .clickable {
                onUserClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = character.name
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.thumbnail.path + "." + character.thumbnail.extension)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                error = painterResource(R.drawable.ic_broken_image),
//                placeholder = painterResource(R.drawable.loading_img)
            )
            if (character.description.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.no_character_description),
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Text(
                    text = character.description,
                )
            }
            Text(
                text = "last modified: ${character.modified.format()}"
            )
        }
    }
}