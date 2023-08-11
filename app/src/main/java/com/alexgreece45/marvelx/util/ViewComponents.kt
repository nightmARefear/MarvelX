package com.alexgreece45.marvelx.util

import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.extensions.applyPadding

@Composable
fun BottomBar() {
    //Bottom
}

data class IconData(
    @DrawableRes val iconResource: Int,
    val contentDescription: String,
    val colorTint: Color? = null
)

data class ToolbarAction(
    val label: String,
    val order: Int,
    val iconData: IconData,
    val onClick: () -> Unit
)

// ======== START OF ========
//        Generic Screens
// ==========================

@Composable
fun ContentScreen(
    onBack: (() -> Unit)? = null,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    fab: Boolean = false,
    fabIcon: Icon? = null,
    fabAction: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showToolbar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    contentPadding = PaddingValues(0.dp).applyPadding()
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        onBack?.let {
                            IconButton(onClick = { it.invoke() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back_32),
                                    contentDescription = stringResource(R.string.back),
                                )
                            }
                        }

                        toolbarTitle?.let { toolbarTitle ->
                            Text(
                                modifier = Modifier.weight(1f),
                                text = toolbarTitle,
                                style = MaterialTheme.typography.h6,
                                maxLines = 1
                            )
                        }

                        toolbarActions?.sortedBy { it.order }?.forEach { toolbarAction ->
                            IconButton(onClick = { toolbarAction.onClick() }) {
                                Icon(
                                    painter = painterResource(toolbarAction.iconData.iconResource),
                                    contentDescription = toolbarAction.iconData.contentDescription,
                                    tint = toolbarAction.iconData.colorTint
                                        ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (fab) {
                FloatingActionButton(onClick = { fabAction?.invoke() }) {

                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content(padding.applyPadding())
        }
    }
}

@Composable
fun LoadingScreen(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "loading.....",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3,
                )
            }
        }
    }
}

@Composable
fun ItemDescription(
    description: String,
    emptyDescriptionMessage: String
) {
    if (description.isBlank()) {
        Text(
            text = emptyDescriptionMessage,
            style = MaterialTheme.typography.body1.copy(
                color = if (MaterialTheme.colors.isLight) Color.Gray
                else LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
                fontStyle = FontStyle.Italic
            )
        )
    } else {
        Text(
            text = description,
            style = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.Justify
            )
        )
    }
}

// ======== END OF ========
//        Generic Screens
// ==========================

sealed class SortingType(val order: Int, val label: String) {

    object Random : SortingType(order = 0, label = "Random")
    object TextAscending : SortingType(order = 1, label = "A-Z Asc")

    object TextDescending : SortingType(order = 2, label = "A-Z Desc")

    companion object {
        fun asList(): List<SortingType> = listOf(Random, TextAscending, TextDescending)
    }
}

@Composable
fun SortingDropDownMenu(
    isExpanded: Boolean,
    sortingType: SortingType,
    onDismissRequest: () -> Unit,
    onDropDownArrowClick: () -> Unit,
    onItemClick: (SortingType) -> Unit
) {
    val items = SortingType.asList()

    Text(
        text = "Sort by",
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
        style = MaterialTheme.typography.subtitle1
    )

    VSpacer.Tiny()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sortingType.label,
            style = MaterialTheme.typography.h6
        )

        IconButton(onClick = { onDropDownArrowClick() }) {
            if (isExpanded) {
                Icon(
                    imageVector = Icons.Filled.ArrowDropUp,
                    contentDescription = Icons.Filled.ArrowDropUp.name,
                    tint = MaterialTheme.colors.onBackground
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = Icons.Filled.ArrowDropDown.name,
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onDismissRequest() }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    modifier = Modifier.background(Color.Transparent),
                    onClick = {
                        // Stops from selecting the same item again.
                        if (item != sortingType)
                            onItemClick(item)
                        else
                            onDismissRequest()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.h6
                        )

                        if (index == sortingType.order) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = Icons.Filled.Check.name,
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                }
            }
        }
    }

}

// ======== START OF ========
//        Error Screens
// ==========================
@Composable
fun LazyItemScope.GenericErrorScreen(
    errorMessage: String,
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.error
            )
        )
    }
}

@Composable
fun LazyItemScope.NoInternetErrorScreen(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection_error_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.error
            )
        )

        VSpacer.Medium()

        Button(
            onClick = { onRetryClick() }
        ) {
            Text(
                text = stringResource(R.string.retry).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

// ======== END OF ========
//        Error Screens
// ==========================
