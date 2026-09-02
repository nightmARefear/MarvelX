package com.alexgreece45.marvelx.ui.screens.marketplace.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

/**
 * An image of a listing.
 */
@Composable
fun ListingImage(
    imageUrl: String,
    title: String,
    @SuppressLint("ModifierParameter") boxModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    WrapAsyncImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(
            id = R.string.screen_marketplace_content_description_listing,
            title
        ),
        modifier = modifier,
        boxModifier = boxModifier
            .clip(shape = MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.customColors.white),
        contentScale = contentScale
    )
}