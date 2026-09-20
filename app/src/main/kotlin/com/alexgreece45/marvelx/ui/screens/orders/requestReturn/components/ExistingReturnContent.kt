package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.components.statusContainer.InfoContainer
import com.alexgreece45.marvelx.common_ui.components.statusContainer.StatusContainer
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.components.listing.ListingPriceLabel
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderRow
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderIdsLabel
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.toUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.descriptionResId
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.headlineResId
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.iconResId
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.isApproved
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.isResolved
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.statusColors
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toUi
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi

private val ATTACHED_IMAGE_SIZE = ConstantsUi.LISTING_IMAGE_BIG_SIZE
private val SECTION_SPACING = 20.dp

/**
 * Read-only view of a return request that already exists, shared by the buyer and the seller.
 * A seller looking at a pending request additionally gets [SellerResponsePanel] below this.
 */
@Composable
internal fun ExistingReturnContent(
    orderId: OrderIdDomain,
    orderItemReturn: OrderItemReturnUi,
    isSeller: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SECTION_SPACING)
    ) {
        OrderIdsLabel(
            orderId = orderId,
            orderItemId = orderItemReturn.orderItem.orderItemId
        )

        ReturnStatusBanner(
            status = orderItemReturn.returnStatus,
            isSeller = isSeller,
            refundAmountLabel = orderItemReturn.refundAmount?.label
        )

        ReturnProgressTimeline(
            status = orderItemReturn.returnStatus,
            submittedAtLabel = orderItemReturn.submittedAt.label
        )

        ListingCard(
            title = orderItemReturn.orderItem.title,
            coverImageUrl = orderItemReturn.orderItem.coverImageUrl,
            additionalTitleContent = { ListingPriceLabel(orderItemReturn.orderItem.price.label) },
            trailingIcon = null,
            additionalContent = null,
            enabled = false,
            onClicked = {}
        )

        ReturnRequestDetailsSection(orderItemReturn = orderItemReturn)

        if (orderItemReturn.returnStatus.isResolved) {
            SellerResponseCard(
                status = orderItemReturn.returnStatus,
                isSeller = isSeller,
                sellerMessage = orderItemReturn.sellerMessage,
                refundAmountLabel = orderItemReturn.refundAmount?.label
            )
        }

        if (orderItemReturn.returnStatus == OrderItemReturnStatusUi.PENDING && !isSeller) {
            InfoContainer(
                title = stringResource(R.string.screen_request_item_return_buyer_awaiting_response_title),
                modifier = Modifier.fillMaxWidth(),
                extraContent = {
                    Text(
                        text = stringResource(R.string.screen_request_item_return_buyer_awaiting_response_description),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            )
        }
    }
}

/**
 * The single most important element of the screen: what state the request is in right now,
 * phrased from the perspective of whoever is looking at it.
 */
@Composable
private fun ReturnStatusBanner(
    status: OrderItemReturnStatusUi,
    isSeller: Boolean,
    refundAmountLabel: String?
) {
    val colors = status.statusColors

    StatusContainer(
        title = stringResource(status.headlineResId(isSeller)),
        titleColor = colors.contentColor,
        backgroundColor = colors.backgroundColor,
        modifier = Modifier.fillMaxWidth(),
        outlineColor = colors.outlineColor,
        verticalAlignment = Alignment.CenterVertically,
        icon = {
            Icon(
                painter = painterResource(status.iconResId),
                contentDescription = null,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                tint = colors.contentColor
            )
        },
        extraContent = {
            Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                Text(
                    text = stringResource(status.descriptionResId(isSeller)),
                    color = colors.contentColor,
                    style = MaterialTheme.typography.subtitle1
                )

                if (status == OrderItemReturnStatusUi.REFUNDED && refundAmountLabel != null) {
                    Text(
                        text = stringResource(
                            R.string.screen_request_item_return_status_banner_refund_amount,
                            refundAmountLabel
                        ),
                        color = colors.contentColor,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    )
}

/**
 * Submitted -> under review -> outcome, so both sides can see at a glance how far the request got.
 */
@Composable
private fun ReturnProgressTimeline(
    status: OrderItemReturnStatusUi,
    submittedAtLabel: String
) {
    val isResolved = status.isResolved
    val outcomeColor = when {
        !isResolved -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
        status.isApproved -> MaterialTheme.customColors.successStatusContainerColors.contentColor
        else -> MaterialTheme.customColors.errorStatusContainerColors.contentColor
    }
    val completedColor = MaterialTheme.customColors.successStatusContainerColors.contentColor
    val currentColor = MaterialTheme.customColors.warningStatusContainerColors.contentColor

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_timeline_title))

        Column(modifier = Modifier.fillMaxWidth()) {
            TimelineStep(
                title = stringResource(R.string.screen_request_item_return_timeline_step_submitted),
                subtitle = submittedAtLabel,
                indicatorColor = completedColor,
                isFilled = true,
                isLast = false
            )

            TimelineStep(
                title = stringResource(R.string.screen_request_item_return_timeline_step_review),
                subtitle = stringResource(
                    if (isResolved) {
                        R.string.screen_request_item_return_timeline_step_review_completed
                    } else {
                        R.string.screen_request_item_return_timeline_step_review_in_progress
                    }
                ),
                indicatorColor = if (isResolved) completedColor else currentColor,
                isFilled = true,
                isLast = false
            )

            TimelineStep(
                title = stringResource(R.string.screen_request_item_return_timeline_step_outcome),
                subtitle = stringResource(
                    if (isResolved) {
                        status.labelResId
                    } else {
                        R.string.screen_request_item_return_timeline_step_outcome_awaiting
                    }
                ),
                indicatorColor = outcomeColor,
                isFilled = isResolved,
                isLast = true
            )
        }
    }
}

@Composable
private fun TimelineStep(
    title: String,
    subtitle: String,
    indicatorColor: Color,
    isFilled: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.width(SPACING_MEDIUM.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = SPACING_TINY.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isFilled) {
                            indicatorColor
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                        }
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(vertical = SPACING_TINY.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(color = indicatorColor.copy(alpha = 0.4f))
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = SPACING_SMALL.dp, bottom = if (isLast) 0.dp else SPACING_MEDIUM.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                color = indicatorColor,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun ReturnRequestDetailsSection(orderItemReturn: OrderItemReturnUi) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_existing_return_details_title))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACING_MEDIUM.dp),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_existing_return_reason_label),
                    endLabel = stringResource(orderItemReturn.reason.toUi().titleResId)
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_existing_return_condition_label),
                    endLabel = stringResource(orderItemReturn.condition.toUi().titleResId)
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_existing_return_method_label),
                    endLabel = stringResource(orderItemReturn.returnMethod.toUi().titleResId)
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_existing_return_submitted_label),
                    endLabel = orderItemReturn.submittedAt.label
                )

                SectionHeaderRow(
                    startLabel = stringResource(R.string.screen_request_item_return_existing_return_refund_requested_label),
                    endLabel = stringResource(
                        if (orderItemReturn.isRefundRequested) {
                            R.string.generic_yes_button
                        } else {
                            R.string.generic_no_button
                        }
                    )
                )

                orderItemReturn.refundAmount?.let { refundAmount ->
                    SectionHeaderRow(
                        startLabel = stringResource(R.string.screen_request_item_return_existing_return_refund_amount_label),
                        endLabel = refundAmount.label
                    )
                }

                orderItemReturn.description?.let { description ->
                    WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                        Text(
                            text = stringResource(R.string.screen_request_item_return_existing_return_description_label),
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = description,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                if (orderItemReturn.attachedImageUrls.isNotEmpty()) {
                    WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                        Text(
                            text = stringResource(R.string.screen_request_item_return_existing_return_images_label),
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                            style = MaterialTheme.typography.body1
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ATTACHED_IMAGE_SIZE),
                            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                        ) {
                            items(
                                items = orderItemReturn.attachedImageUrls,
                                key = { it }
                            ) { imageUrl ->
                                WrapAsyncImage(
                                    imageUrl = imageUrl,
                                    contentDescription = stringResource(R.string.screen_request_item_return_existing_return_images_label),
                                    modifier = Modifier.size(ATTACHED_IMAGE_SIZE),
                                    boxModifier = Modifier
                                        .clip(shape = MaterialTheme.shapes.medium)
                                        .background(color = MaterialTheme.customColors.white),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The seller's decision and their note to the buyer, shown once the request has been answered.
 */
@Composable
private fun SellerResponseCard(
    status: OrderItemReturnStatusUi,
    isSeller: Boolean,
    sellerMessage: String?,
    refundAmountLabel: String?
) {
    val colors = status.statusColors

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_seller_response_card_title))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACING_MEDIUM.dp),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(status.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                        tint = colors.contentColor
                    )

                    Text(
                        text = stringResource(status.labelResId),
                        color = colors.contentColor,
                        style = MaterialTheme.typography.h6
                    )
                }

                if (status == OrderItemReturnStatusUi.REFUNDED && refundAmountLabel != null) {
                    SectionHeaderRow(
                        startLabel = stringResource(R.string.screen_request_item_return_existing_return_refund_amount_label),
                        endLabel = refundAmountLabel
                    )
                }

                WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())

                Text(
                    text = stringResource(if (isSeller) R.string.screen_request_item_return_existing_return_your_message_label else R.string.screen_request_item_return_existing_return_seller_message_label),
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    style = MaterialTheme.typography.body1
                )

                Text(
                    text = sellerMessage
                        ?: stringResource(R.string.screen_request_item_return_seller_message_empty),
                    style = MaterialTheme.typography.body2,
                    color = if (sellerMessage == null) {
                        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                    } else {
                        MaterialTheme.colors.onSurface
                    }
                )
            }
        }
    }
}

/**
 * Warning shown to the seller directly above their response controls.
 */
@Composable
internal fun SellerDecisionWarning(modifier: Modifier = Modifier) {
    StatusContainer(
        title = stringResource(R.string.screen_request_item_return_seller_response_warning_title),
        titleColor = MaterialTheme.customColors.warningStatusContainerColors.contentColor,
        backgroundColor = MaterialTheme.customColors.warningStatusContainerColors.backgroundColor,
        modifier = modifier,
        outlineColor = MaterialTheme.customColors.warningStatusContainerColors.outlineColor,
        icon = {
            Icon(
                painter = painterResource(UiThemeDrawable.ic_warning),
                contentDescription = null,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                tint = MaterialTheme.customColors.warningStatusContainerColors.contentColor
            )
        },
        extraContent = {
            Text(
                text = stringResource(R.string.screen_request_item_return_seller_response_warning_description),
                color = MaterialTheme.customColors.warningStatusContainerColors.contentColor,
                style = MaterialTheme.typography.subtitle1
            )

            VSpacer.Tiny()
        }
    )
}
