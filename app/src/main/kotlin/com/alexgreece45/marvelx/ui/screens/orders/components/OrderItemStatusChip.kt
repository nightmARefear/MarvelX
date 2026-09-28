package com.alexgreece45.marvelx.ui.screens.orders.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui_theme.theme.values.customShapes
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi

@Composable
internal fun OrderItemStatusChip(status: OrderItemStatusUi) {
    val statusContentColor = status.contentColor
    Card(
        backgroundColor = status.backgroundColor,
        contentColor = statusContentColor,
        shape = MaterialTheme.customShapes.chipCornerShape
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(SPACING_TINY.dp)
        ) {
            if (status == OrderItemStatusUi.Delivered) {
                Image(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                    colorFilter = ColorFilter.tint(statusContentColor)
                )
            } else {
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(color = statusContentColor)
                }
            }

            Text(
                text = stringResource(status.labelResId),
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
