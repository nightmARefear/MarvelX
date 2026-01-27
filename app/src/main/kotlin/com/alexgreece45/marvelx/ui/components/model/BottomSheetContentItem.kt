package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.ui.components.IconData

/**
 * Defines a single item in the bottom sheet content composable function.
 *
 * @param title             The text label for this item.
 * @param titleColor        The title color for the [title]. If `null` is provided, then the
 * material `onBackground` color will be used.
 * @param leadingIconData   The leading icon data for this item. It is optional.
 * @param trailingIconData  The trailing icon data for this item. It is optional.
 * @param onClickEvent      The [BaseEvent] to execute when this item is clicked.
 */
data class BottomSheetContentItem(
    val title: String,
    val titleColor: Color? = null,
    val leadingIconData: BottomSheetContentItemIconData? = null,
    val trailingIconData: BottomSheetContentItemIconData? = null,
    val onClickEvent: BaseEvent
)

/**
 * Defines the data to represent a single icon in the bottom sheet content composable function. You
 * can set the icon data, whether you want the material background color to appear behind the
 * icon, and what shape to clip the icon. If no shape is provided, then it's set to [CircleShape].
 */
data class BottomSheetContentItemIconData(
    val iconData: IconData,
    val showBackgroundColor: Boolean = false,
    val shape: Shape = CircleShape
)
