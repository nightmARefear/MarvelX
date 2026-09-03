package com.alexgreece45.marvelx.common_ui.components.statusContainer

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

private val errorColors: StatusContainerColors
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.customColors.errorStatusContainerColors

@Composable
fun ErrorStatusContainer(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = errorColors.contentColor,
    extraContent: (@Composable () -> Unit)? = null
) {
    StatusContainer(
        title = title,
        titleColor = titleColor,
        backgroundColor = errorColors.backgroundColor,
        modifier = modifier,
        icon = { Icon(painter = painterResource(R.drawable.ic_error), contentDescription = null) },
        extraContent = extraContent
    )
}