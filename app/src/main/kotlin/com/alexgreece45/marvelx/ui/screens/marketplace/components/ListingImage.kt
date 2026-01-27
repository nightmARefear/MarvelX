package com.alexgreece45.marvelx.ui.screens.marketplace.components

import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.theme.values.customColors

/**
 * An image of a listing.
 */
@Composable
fun ListingImage(
    imageUrl:String,
    title:String,
    modifier: Modifier = Modifier
) {
    WrapAsyncImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(
            id = R.string.screen_marketplace_content_description_listing,
            title
        ),
        modifier = modifier
            .then(
                Modifier.background(
                    color = MaterialTheme.customColors.white,
                    shape = MaterialTheme.shapes.medium
                )
            )
    )
}
