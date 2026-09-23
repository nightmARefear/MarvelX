package com.alexgreece45.marvelx.ui.components.modalBottomSheetContents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.throttledClickable

/**
 * Composable function used for displaying a list of items inside a modal bottom sheet. Each item
 * occupies the whole width of the bottom sheet and consists of an optional leading icon, a title,
 * an optional trailing icon, and supports on-click functionality.
 *
 * @param bottomSheetContentItems   The items of this modal bottom sheet.
 * @param onEventSent               Callback to execute the event [T] of the clicked item.
 */
@Composable
fun <T : BaseEvent> ModalBottomSheetContentItems(
    bottomSheetContentItems: List<BottomSheetContentItem>,
    onEventSent: (T) -> Unit
) {
    ModalBottomSheetContent(paddingValues = PaddingValues(bottom = SPACING_MEDIUM.dp)) {
        Column {
            bottomSheetContentItems.forEach { bottomSheetContentItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(bottomSheetContentItem.testTag?.let { Modifier.testTag(it) } ?: Modifier)
                        .throttledClickable(
                            LocalView.current,
                            onClick = {
                                @Suppress("UNCHECKED_CAST")
                                onEventSent(bottomSheetContentItem.onClickEvent as T)
                            }
                        )
                        .padding(all = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomSheetContentItem.leadingIconData?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (it.showBackgroundColor) MaterialTheme.colors.background else MaterialTheme.colors.surface,
                                    shape = it.shape
                                )
                                .padding(SPACING_SMALL.dp)
                                .clip(it.shape)

                        ) {
                            Icon(
                                painter = painterResource(it.iconData.iconResource),
                                contentDescription = stringResource(it.iconData.contentDescription),
                                tint = it.iconData.tint
                                    ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                            )
                        }

                        HSpacer.Small()
                    }

                    Text(
                        text = bottomSheetContentItem.title,
                        color = bottomSheetContentItem.titleColor ?: LocalContentColor.current,
                        style = MaterialTheme.typography.subtitle1
                    )

                    bottomSheetContentItem.trailingIconData?.let {
                        Icon(
                            painter = painterResource(it.iconData.iconResource),
                            contentDescription = stringResource(it.iconData.contentDescription),
                            modifier = Modifier
                                .background(
                                    color = if (it.showBackgroundColor) MaterialTheme.colors.background else MaterialTheme.colors.surface,
                                )
                                .clip(it.shape)
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}
