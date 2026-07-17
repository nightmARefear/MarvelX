package com.alexgreece45.marvelx.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM

/**
 * An info box component that displays an icon and a label in a rectangular box.
 */
@Composable
fun InfoBox(
    backgroundColor: Color,
    outlineColor: Color,
    label: String,
    iconData: IconData,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.medium

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(BorderStroke(width = 2.dp, color = outlineColor), shape)
            .padding(SPACING_MEDIUM.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            SPACING_MEDIUM.dp
        )
    ) {
        Icon(
            painter = painterResource(iconData.iconResource),
            contentDescription = stringResource(iconData.contentDescription),
            tint = outlineColor
        )

        Text(
            text = label,
            color = MaterialTheme.customColors.white,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun InfoBoxPreview() {
    MarvelXTheme {
        InfoBox(
            backgroundColor = Color(0xA6D57500),
            outlineColor = Color(0xFFD57500),
            label = stringResource(R.string.screen_listing_details_sticky_bottom_label_reserved_listing),
            iconData = IconData(
                iconResource = R.drawable.ic_error,
                contentDescription = R.string.screen_listing_details_content_description_info_icon,
                tint = Color(0xFFD57500)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
