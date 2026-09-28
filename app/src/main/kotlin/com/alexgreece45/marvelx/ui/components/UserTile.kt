package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder

/**
 * A card row led by a circular avatar, with the rest of the row supplied by [content].
 * The base for friend rows, conversation rows and anything else keyed on a person.
 */
@Composable
fun UserTile(
    profileImageUrl: String,
    profileImageContentDescription: String,
    modifier: Modifier = Modifier,
    profileImageSize: Dp = ConstantsUi.GENERIC_IMAGE_TILE_SIZE,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple(onClick = onClick ?: {}, enabled = onClick != null),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WrapAsyncImage(
                imageUrl = profileImageUrl,
                contentDescription = profileImageContentDescription,
                modifier = Modifier
                    .size(profileImageSize)
                    .circularImageWithBorder(
                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                        borderColor = MaterialTheme.customColors.genericRed
                    ),
                // Same as the seller image on Listing Details: fill the circle instead of letterboxing.
                contentScale = ContentScale.Crop
            )

            content()
        }
    }
}
