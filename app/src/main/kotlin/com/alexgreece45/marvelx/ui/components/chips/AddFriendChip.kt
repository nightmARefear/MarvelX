package com.alexgreece45.marvelx.ui.components.chips

import android.content.res.Configuration
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

/**
 * The `add friend` chip.
 *
 * @param onClick Callback invoked when this chip is clicked.
 * @param isLoading Whether the chip is in loading state.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddFriendChip(onClick: () -> Unit, isLoading: Boolean) {
    LoadingChip(
        onClick = onClick,
        label = stringResource(R.string.generic_chip_label_add_friend),
        isLoading = isLoading,
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.customColors.chipBackgroundColors.addFriend,
            contentColor = MaterialTheme.customColors.white,
            leadingIconContentColor = MaterialTheme.customColors.white
        ),
        leadingIconData = IconData(
            iconResource = R.drawable.ic_add_friend,
            contentDescription = R.string.content_description_add_friend
        )
    )
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AddFriendChipPreview() {
    MarvelXTheme {
        AddFriendChip(
            onClick = {},
            isLoading = true
        )
    }
}
