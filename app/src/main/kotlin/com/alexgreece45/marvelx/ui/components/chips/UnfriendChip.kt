package com.alexgreece45.marvelx.ui.components.chips

import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

/**
 * The `unfriend` chip.
 *
 * @param onClick Callback invoked when this chip is clicked.
 * @param isLoading Whether the chip is in loading state.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UnfriendChip(onClick: () -> Unit, isLoading: Boolean) {
    LoadingChip(
        onClick = onClick,
        label = stringResource(R.string.generic_chip_label_unfriend),
        isLoading = isLoading,
        leadingIconData = IconData(
            iconResource = R.drawable.ic_remove_friend,
            contentDescription = R.string.content_description_unfriend
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.customColors.chipBackgroundColors.unfriend,
            contentColor = MaterialTheme.customColors.white,
            leadingIconContentColor = MaterialTheme.customColors.white
        )
    )
}
