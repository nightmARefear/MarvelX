package com.alexgreece45.marvelx.ui.components.chips

import androidx.compose.foundation.layout.size
import androidx.compose.material.ChipColors
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.previewAnnotation.ThemePreviewNoSystemUi
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.constants.ConstantsUi

/**
 * Composable function that displays a [WrapChip] with a leading icon, a label, and also supports
 * loading functionality by displaying the loading icon when [isLoading] is set to `true`.
 *
 * @param onClick Callback invoked when this chip is clicked.
 * @param label The text to display after the chip.
 * @param isLoading Whether the chip is in loading state.
 * @param colors The colors of the chip.
 * @param leadingIconData The data of the icon when the loading state is off.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingChip(
    onClick: () -> Unit,
    label: String,
    isLoading: Boolean,
    colors: ChipColors,
    leadingIconData: IconData,
) {
    WrapChip(
        onClick = {
            if (!isLoading) onClick()
        },
        colors = colors,
        leadingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                    color = MaterialTheme.customColors.white
                )
            } else {
                Icon(
                    painter = painterResource(leadingIconData.iconResource),
                    contentDescription = stringResource(
                        id = leadingIconData.contentDescription
                    )
                )
            }
        },
        content = {
            Text(text = label)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@ThemePreviewNoSystemUi
@Composable
private fun LoadingChipLoadingTruePreview() {
    MarvelXTheme {
        LoadingChip(
            onClick = { },
            label = "Add Friend",
            isLoading = true,
            colors = ChipDefaults.chipColors(
                backgroundColor = MaterialTheme.customColors.chipBackgroundColors.addFriend,
                contentColor = MaterialTheme.customColors.white,
                leadingIconContentColor = MaterialTheme.customColors.white
            ),
            leadingIconData = IconData(
                iconResource = R.drawable.ic_add_friend,
                contentDescription = 0
            )
        )
    }
}