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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    LoadingChip(
        onClick = onClick,
        modifier = modifier,
        label = stringResource(R.string.generic_chip_label_message),
        isLoading = false,
        leadingIconData = IconData(
            iconResource = R.drawable.ic_chat,
            contentDescription = R.string.content_description_message_user
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.customColors.chipBackgroundColors.addFriend,
            contentColor = MaterialTheme.customColors.white,
            leadingIconContentColor = MaterialTheme.customColors.white
        )
    )
}
