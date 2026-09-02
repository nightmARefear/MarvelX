package com.alexgreece45.marvelx.ui.screens.orders.components

import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain

@Composable
fun OrderIdsLabel(
    orderId: OrderIdDomain,
    orderItemId: OrderIdDomain
) {
    Text(
        text = stringResource(
            R.string.order_ids_label,
            orderId.formatted,
            orderItemId.formatted
        ),
        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    )
}