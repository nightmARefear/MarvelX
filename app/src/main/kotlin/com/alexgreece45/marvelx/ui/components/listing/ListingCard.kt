package com.alexgreece45.marvelx.ui.components.listing

import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.util.constants.ConstantsUi

/**
 * Displays a listing with the basic information only.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListingCard(
    title: String,
    titleMaxLines: Int = 3,
    coverImageUrl: String,
    additionalTitleContent: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)?,
    additionalContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClicked: () -> Unit
) {
    SectionHeader(
        title = title,
        titleMaxLines = titleMaxLines,
        additionalTitleContent = additionalTitleContent,
        leadingIcon = {
            ListingImage(
                imageUrl = coverImageUrl,
                title = title,
                modifier = Modifier.size(ConstantsUi.LISTING_IMAGE_NORMAL_SIZE)
            )
        },
        trailingIcon = trailingIcon,
        additionalContent = additionalContent,
        enabled = enabled,
        onClick = onClicked
    )
}