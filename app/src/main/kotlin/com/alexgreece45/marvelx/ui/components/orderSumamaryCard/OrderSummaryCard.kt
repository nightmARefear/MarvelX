package com.alexgreece45.marvelx.ui.components.orderSumamaryCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderRow
import com.alexgreece45.marvelx.util.VSpacer

@Composable
fun OrderSummaryCard(
    title: String,
    subtotalPrice: String,
    shippingPrice: String,
    totalPrice: String,
    additionalContent: (@Composable () -> Unit)?
) {
    SectionHeader(
        title = title,
        additionalTitleContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeaderRow(
                    startLabel = stringResource(R.string.order_summary_card_label_subtotal),
                    endLabel = subtotalPrice
                )

                VSpacer.Small()

                SectionHeaderRow(
                    startLabel = stringResource(R.string.order_summary_card_label_shipping),
                    endLabel = shippingPrice
                )

                VSpacer.Small()

                WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                VSpacer.Small()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.order_summary_card_label_total),
                        style = MaterialTheme.typography.h5
                    )

                    Text(
                        text = totalPrice,
                        style = MaterialTheme.typography.h5
                    )
                }
            }
        },
        additionalContent = additionalContent,
        enabled = false,
        onClick = {}
    )
}