package com.alexgreece45.marvelx.common_ui.components.statusBox

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.alexgreece45.marvelx.ui_theme.R
import com.alexgreece45.marvelx.ui_theme.theme.values.StatusContainerColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

private val infoColors: StatusContainerColors
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.customColors.infoStatusContainerColors

@Composable
fun InfoContainer(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = infoColors.contentColor,
    showOutline: Boolean = true,
    showIcon: Boolean = true,
    extraContent: (@Composable () -> Unit)? = null
) {
    StatusContainer(
        title = title,
        titleColor = titleColor,
        backgroundColor = infoColors.backgroundColor,
        modifier = modifier,
        outlineColor = if (showOutline) infoColors.outlineColor else null,
        icon = {
            if (showIcon) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = titleColor
                )
            }
        },
        extraContent = extraContent
    )
}