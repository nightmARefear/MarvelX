package com.alexgreece45.marvelx.data.models.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.format
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.ThemeTypography
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun CharacterItem(
    character: CharacterDomain,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp
            )
            .clickable {
                onUserClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = character.name,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
            AsyncImage(
                modifier = Modifier
                    .height(225.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.thumbnail.path + "." + character.thumbnail.extension)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name.ifBlank { null },
                error = painterResource(R.drawable.ic_broken_image),
//                placeholder = painterResource(R.drawable.loading_img)
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
            )
            if (character.description.isNullOrBlank()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.no_character_description),
                    style = MaterialTheme.typography.body1,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = character.description,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}