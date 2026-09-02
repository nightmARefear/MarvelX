package com.alexgreece45.marvelx.ui.screens.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderStatusUi
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi

@Composable
fun OrderStatusInfoBottomSheetContent(
    status: OrderStatusUi,
    extraContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = status.backgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = status.icon,
                contentDescription = null,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
            )
        }

        VSpacer.Medium()

        Text(
            text = stringResource(id = status.labelResId),
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.h6
        )

        VSpacer.Small()

        Text(
            text = stringResource(id = status.fullDescriptionResId),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )

        extraContent?.invoke()
    }
}