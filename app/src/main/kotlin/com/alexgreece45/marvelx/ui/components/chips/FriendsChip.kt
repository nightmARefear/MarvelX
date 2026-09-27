package com.alexgreece45.marvelx.ui.components.chips

import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

/**
 * The `friends` chip.
 *
 * @param onClick Callback invoked when this chip is clicked.
 * @param isLoading Whether the chip is in loading state.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendsChip(onClick: () -> Unit, isLoading: Boolean, modifier: Modifier = Modifier) {
    LoadingChip(
        onClick = onClick,
        modifier = modifier,
        label = stringResource(R.string.generic_chip_label_friends),
        isLoading = isLoading,
        leadingIconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.content_description_friends
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.customColors.chipBackgroundColors.friends,
            contentColor = MaterialTheme.customColors.white,
            leadingIconContentColor = MaterialTheme.customColors.white
        )
    )
}
