package com.alexgreece45.marvelx.ui.components.checkoutDataCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.safeLetComposable
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCheckoutInfoUi
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customShapes
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

@Composable
fun CheckoutInfoCard(
    orderCheckoutInfo: OrderCheckoutInfoUi,
    isShippingDetailsEditable: Boolean,
    isPaymentDetailsEditable: Boolean,
    onShippingDetailsEditButtonClicked: () -> Unit,
    onPaymentDetailsEditButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.checkout_info_card_shipping_details_title),
            additionalTitleContent = {
                Column {
                    Text(text = orderCheckoutInfo.fullName)
                    Text(text = orderCheckoutInfo.streetAddress)
                    Text(text = orderCheckoutInfo.cityAndZipCode)
                }
            },
            trailingIcon = {
                if (isShippingDetailsEditable) {
                    ThrottledIconButton(
                        onClick = onShippingDetailsEditButtonClicked,
                        modifier = Modifier.testTag(CheckoutInfoCardTestTags.EDIT_SHIPPING_DETAILS_BUTTON)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = stringResource(R.string.checkout_info_card_content_description_edit_shipping_details)
                        )
                    }
                }
            },
            enabled = false
        ) {}

        SectionHeader(
            title = stringResource(R.string.checkout_info_card_payment_details_title),
            additionalTitleContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    // Card brand logo & last 4 digits & cardholder name
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(orderCheckoutInfo.cardBrand.iconResId),
                            contentDescription = stringResource(orderCheckoutInfo.cardBrand.contentDescriptionResId),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(54.dp)
                                .background(
                                    color = MaterialTheme.customColors.cardBrandBackgroundColor,
                                    shape = MaterialTheme.customShapes.chipCornerShape
                                )
                        )

                        Column(modifier = Modifier.padding(start = SPACING_TINY.dp)) {
                            Text(
                                text = stringResource(
                                    R.string.checkout_info_card_label_card_ending,
                                    stringResource(orderCheckoutInfo.cardBrand.labelResId),
                                    orderCheckoutInfo.maskedCardNumber
                                )
                            )

                            Text(
                                text = orderCheckoutInfo.fullName,
                                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                    safeLetComposable(
                        orderCheckoutInfo.paymentDate,
                        orderCheckoutInfo.transactionId
                    ) { paymentDate, transactionId ->
                        WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                        Column(modifier = Modifier.fillMaxWidth()) {
                            PaymentDetailsRow(
                                label = stringResource(R.string.checkout_info_card_label_payment_date),
                                value = paymentDate.label
                            )

                            PaymentDetailsRow(
                                label = stringResource(R.string.checkout_info_card_label_transaction_id),
                                value = transactionId
                            )
                        }
                    }
                }
            },
            trailingIcon = {
                if (isPaymentDetailsEditable) {
                    ThrottledIconButton(
                        onClick = onPaymentDetailsEditButtonClicked,
                        modifier = Modifier.testTag(CheckoutInfoCardTestTags.EDIT_PAYMENT_DETAILS_BUTTON)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = stringResource(R.string.checkout_info_card_content_description_edit_payment_details)
                        )
                    }
                }
            },
            enabled = false
        ) {}
    }
}

@Composable
private fun PaymentDetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )

        Text(text = value)
    }
}
