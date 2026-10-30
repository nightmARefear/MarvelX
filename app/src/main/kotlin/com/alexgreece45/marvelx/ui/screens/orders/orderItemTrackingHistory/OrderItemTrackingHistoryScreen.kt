package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.extensions.minusDays
import com.alexgreece45.marvelx.core.common.extensions.plusDays
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnEligibilityDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingHistoryItemDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.WrapImage
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.components.icons.ProcessingRequestIconLottie
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.components.listing.ListingPriceLabel
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderDefaults
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onError
import com.alexgreece45.marvelx.ui.model_generic.onLoading
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderIdsLabel
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCourierUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemTrackingHistoryUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.toUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.OrderTrackingHistoryItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderItemDetails
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryErrorType
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingStepState
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingStepUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.mapToTrackingStepList
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.setText
import com.alexgreece45.marvelx.util.extensions.showToast
import com.alexgreece45.marvelx.util.extensions.withEuroSymbol
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.random.Random
import kotlin.random.nextLong

@OptIn(ExperimentalAtomicApi::class)
@Composable
internal fun OrderItemTrackingHistoryScreen(
    onEvent: (OrderItemTrackingHistoryContract.Event) -> Unit,
    state: OrderItemTrackingHistoryContract.State,
    effect: SingleObserverFlow<OrderItemTrackingHistoryContract.Effect>,
    onNavigationEffect: (OrderItemTrackingHistoryContract.Effect.Navigation) -> Unit
) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    ObserveUiEffect(flow = effect, tag = "OrderItemTrackingHistoryScreen") { uiEffect ->
        when (uiEffect) {
            is OrderItemTrackingHistoryContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is OrderItemTrackingHistoryContract.Effect.SetClipboardText -> {
                clipboard.setText(uiEffect.text)
                context.showToast(uiEffect.toastMessage)
            }

            OrderItemTrackingHistoryContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch { modalBottomSheetState.show() }
            }
        }
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            if (!state.isBottomSheetVisible) return@WrapModalBottomSheetLayout
            ModalBottomSheetContent {
                if (state.uiState is UiState.Success) {
                    TrackingHistorySteps(state.uiState.data.steps)
                } else {
                    Text(
                        text = stringResource(
                            R.string.screen_order_item_tracking_history_error_label_fetching_data,
                            state.orderItemDetails.orderId.formatted,
                            state.orderItemDetails.orderItemId.formatted
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                }
            }
        },
        sheetState = modalBottomSheetState,
        onDismissed = { onEvent(OrderItemTrackingHistoryContract.Event.OnBottomSheetDismissed) }
    ) {
        ContentScreen(
            isLoading = state.uiState.isLoading,
            navigableAction = ScreenNavigableAction.Backable(
                onBack = {
                    onEvent(OrderItemTrackingHistoryContract.Event.OnBack)
                },
                testTag = OrderItemTrackingHistoryTestTags.BACK_BUTTON
            ),
            onBack = {
                onEvent(OrderItemTrackingHistoryContract.Event.OnBack)
            },
            showToolbar = true,
            toolbarTitle = stringResource(R.string.screen_order_item_tracking_history_toolbar_title)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                state.uiState
                    .onError { errorType ->
                        val isErrorTypeNoData =
                            errorType == OrderItemTrackingHistoryErrorType.NO_DATA

                        GenericErrorScreen(
                            errorMessage = stringResource(
                                if (isErrorTypeNoData) R.string.screen_order_item_tracking_history_error_label_no_data else R.string.screen_order_item_tracking_history_error_label_fetching_data,
                                state.orderItemDetails.orderId.formatted,
                                state.orderItemDetails.orderItemId.formatted
                            ),
                            buttonLabel = stringResource(if (isErrorTypeNoData) R.string.generic_go_back else R.string.generic_retry_button),
                            buttonTestTag = OrderItemTrackingHistoryTestTags.ERROR_RETRY_BUTTON,
                            onErrorButtonClick = {
                                onEvent(OrderItemTrackingHistoryContract.Event.OnErrorButtonClicked)
                            }
                        )
                    }
                    .onSuccess { orderItemTrackingHistoryUi ->
                        val orderItemDetails = state.orderItemDetails
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                        ) {
                            OrderIdsLabel(
                                orderId = orderItemDetails.orderId,
                                orderItemId = orderItemDetails.orderItemId
                            )

                            OrderItemCard(
                                title = orderItemDetails.orderItemTitle,
                                coverImageUrl = orderItemDetails.orderItemCoverImageUrl,
                                priceLabel = orderItemDetails.orderItemPriceLabel,
                                status = orderItemTrackingHistoryUi.status,
                                currentStepItem = orderItemTrackingHistoryUi.currentStepItem,
                                sellerBasicProfileDetailsState = state.sellerDetailsUiState,
                                onCurrentStatusClicked = { onEvent(OrderItemTrackingHistoryContract.Event.OnCurrentStatusClicked) },
                                onSellerCardClick = {
                                    onEvent(OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnClicked)
                                },
                                onSellerCardErrorButtonClicked = {
                                    onEvent(OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnErrorButtonClicked)
                                }
                            )

                            if (orderItemTrackingHistoryUi.status is OrderItemStatusUi.Delivered) {
                                with(orderItemTrackingHistoryUi) {
                                    PostDeliverySection(
                                        deliveryDateLabel = currentStepItem?.date?.label.orEmpty(),
                                        courierUi = courier,
                                        trackingId = trackingId,
                                        returnUntilDateLabel = returnEligibility.returnUntilDate.label,
                                        returnStatus = returnEligibility.returnStatus,
                                        refundStatus = returnEligibility.refundStatus,
                                        refundUntilDateLabel = returnEligibility.refundUntilDate.label,
                                        refundRequested = returnEligibility.refundRequested,
                                        onReturnActionButtonClicked = {
                                            onEvent(OrderItemTrackingHistoryContract.Event.OnReturnActionButtonClicked)
                                        },
                                        onRefundActionButtonClicked = {
                                            onEvent(OrderItemTrackingHistoryContract.Event.OnRefundActionButtonClicked)
                                        }
                                    )
                                }
                            } else {
                                CourierCard(
                                    trackingId = orderItemTrackingHistoryUi.trackingId,
                                    courierUi = orderItemTrackingHistoryUi.courier,
                                    onCopyButtonClicked = {
                                        onEvent(OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnCopyButtonClicked)
                                    },
                                    onViewOnCourierWebsiteButtonClicked = {
                                        onEvent(OrderItemTrackingHistoryContract.Event.CourierCardEvents.OnViewOnCourierWebsiteButtonClicked)
                                    }
                                )

                                EstimatedDeliveryCard(estimatedDateRangeLabel = orderItemTrackingHistoryUi.estimatedDelivery.estimatedDateRangeLabel)

                                TrackingHistorySteps(trackingSteps = orderItemTrackingHistoryUi.steps)
                            }

                            WrapOutlinedButton(
                                onClick = {
                                    onEvent(OrderItemTrackingHistoryContract.Event.OnContactSellerButtonClicked)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(OrderItemTrackingHistoryTestTags.CONTACT_SELLER_BUTTON)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    WrapImage(
                                        painter = painterResource(R.drawable.ic_phone),
                                        contentDescription = null,
                                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                                        colorFilter = ColorFilter.tint(MaterialTheme.customColors.genericRed)
                                    )

                                    Text(text = stringResource(R.string.screen_order_item_tracking_history_extra_action_button_contact_seller))
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun OrderItemCard(
    title: String,
    coverImageUrl: String,
    priceLabel: String,
    status: OrderItemStatusUi,
    currentStepItem: OrderTrackingHistoryItemUi?,
    sellerBasicProfileDetailsState: UiState<RepositoryBasicProfileDetails, Unit>,
    onCurrentStatusClicked: () -> Unit,
    onSellerCardClick: () -> Unit,
    onSellerCardErrorButtonClicked: () -> Unit,
) {
    val statusColor = status.backgroundColor
    val subtextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    val transparentColor = MaterialTheme.customColors.transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            statusColor,
                            transparentColor
                        ),
                        startY = 0f,
                        endY = size.height
                    ),
                    size = Size(SPACING_TINY.dp.toPx(), size.height)
                )
            }
    ) {
        ListingCard(
            title = title,
            additionalTitleContent = { ListingPriceLabel(price = priceLabel) },
            coverImageUrl = coverImageUrl,
            trailingIcon = null,
            additionalContent = {
                VSpacer.Medium()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(OrderItemTrackingHistoryTestTags.CURRENT_STATUS_CARD)
                        .clickableNoRipple(
                            enabled = status is OrderItemStatusUi.Delivered,
                            onClick = onCurrentStatusClicked
                        ),
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    WrapHorizontalDivider()

                    // Current Status.
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.screen_order_item_tracking_history_title_current_status),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = statusColor.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
                                    .padding(6.dp)
                                    .size(SPACING_MEDIUM.dp)
                                    .background(color = statusColor, shape = CircleShape)
                            )

                            TrackingHistoryStepLabels(
                                stepLabel = stringResource(status.labelResId),
                                descriptionLabel = currentStepItem?.let { step -> "${step.location}, ${step.date.label}" },
                                stepLabelColor = statusColor,
                                descriptionColor = subtextColor
                            )
                        }
                    }

                    WrapHorizontalDivider()

                    // Seller details.
                    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                        Text(
                            text = stringResource(R.string.screen_order_item_tracking_history_seller_card_title),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )

                        SellerDetailsCard(
                            sellerBasicProfileDetailsState = sellerBasicProfileDetailsState,
                            onClicked = onSellerCardClick,
                            onErrorButtonClicked = onSellerCardErrorButtonClicked
                        )
                    }
                }
            },
            enabled = false,
            onClicked = {}
        )
    }
}

@Composable
private fun SellerDetailsCard(
    sellerBasicProfileDetailsState: UiState<RepositoryBasicProfileDetails, Unit>,
    onClicked: () -> Unit,
    onErrorButtonClicked: () -> Unit
) {
    sellerBasicProfileDetailsState
        .onLoading {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ProcessingRequestIconLottie()
            }
        }
        .onError {
            GenericErrorScreen(
                errorMessage = stringResource(R.string.screen_order_item_tracking_history_error_label_fetching_seller_info),
                modifier = Modifier.fillMaxWidth(),
                buttonTestTag = OrderItemTrackingHistoryTestTags.SELLER_DETAILS_ERROR_RETRY_BUTTON,
                onErrorButtonClick = onErrorButtonClicked
            )
        }
        .onSuccess { profileDetails ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(OrderItemTrackingHistoryTestTags.SELLER_DETAILS_CARD)
                    .clickableNoRipple(onClick = onClicked),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WrapAsyncImage(
                        imageUrl = profileDetails.profileImageUrl,
                        contentDescription = stringResource(R.string.screen_listing_details_content_description_seller_profile_image),
                        modifier = Modifier
                            .size(50.dp)
                            .circularImageWithBorder(
                                borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                                borderColor = MaterialTheme.customColors.genericRed
                            )
                    )

                    Column {
                        Text(
                            text = profileDetails.fullName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3,
                            style = MaterialTheme.typography.subtitle1
                        )

                        Text(
                            text = stringResource(R.string.screen_order_item_tracking_history_seller_card_label_view_profile),
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }

                NavigateIcon()
            }
        }
}

@Composable
private fun CourierCard(
    trackingId: String,
    courierUi: OrderCourierUi,
    onCopyButtonClicked: () -> Unit,
    onViewOnCourierWebsiteButtonClicked: () -> Unit,
) {
    SectionHeader(
        title = stringResource(R.string.screen_order_item_tracking_history_courier_card_title),
        additionalTitleContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                // Item cover image.
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = MaterialTheme.customColors.white,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(courierUi.iconResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SPACING_TINY.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeaderTitle(title = stringResource(courierUi.labelResId))

                    // Tracking ID.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.screen_order_item_tracking_history_courier_card_label_tracking_id,
                                trackingId
                            ),
                            modifier = Modifier.padding(end = SPACING_SMALL.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3,
                            style = MaterialTheme.typography.subtitle1
                        )

                        ThrottledIconButton(
                            onClick = onCopyButtonClicked,
                            modifier = Modifier.testTag(OrderItemTrackingHistoryTestTags.COPY_TRACKING_ID_BUTTON)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_copy),
                                contentDescription = stringResource(R.string.screen_order_item_tracking_history_content_description_copy_tracking_id)
                            )
                        }
                    }
                }
            }
        },
        additionalContent = {
            VSpacer.Small()

            WrapOutlinedButton(
                onClick = onViewOnCourierWebsiteButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(OrderItemTrackingHistoryTestTags.VIEW_ON_COURIER_WEBSITE_BUTTON)
            ) {
                Text(
                    text = stringResource(R.string.screen_order_item_tracking_history_courier_card_button)
                )
            }
        },
        enabled = false,
        onClick = {}
    )
}

@Composable
private fun EstimatedDeliveryCard(estimatedDateRangeLabel: String) {
    SectionHeader(
        title = stringResource(R.string.screen_order_item_tracking_history_title_estimated_delivery),
        additionalTitleContent = {
            Text(
                text = estimatedDateRangeLabel,
                style = MaterialTheme.typography.subtitle1
            )
        },
        enabled = false,
        onClick = {}
    )
}

@Composable
private fun PostDeliverySection(
    deliveryDateLabel: String,
    courierUi: OrderCourierUi,
    trackingId: String,
    returnStatus: OrderItemReturnStatusDomain?,
    returnUntilDateLabel: String,
    refundStatus: OrderItemRefundStatusDomain?,
    refundUntilDateLabel: String,
    refundRequested: Boolean?,
    onReturnActionButtonClicked: () -> Unit,
    onRefundActionButtonClicked: () -> Unit,
) {
    DeliveryDetailsCard(
        deliveryDateLabel = deliveryDateLabel,
        courierUi = courierUi,
        trackingId = trackingId
    )

    // Once the seller decides, show only the card for what the buyer asked for: the refund card for a refund,
    // the return card for a replacement. A rejection sets both statuses to rejected, so it relies on
    // refundRequested (null, from a backend without the field, falls back to the return card).
    val isRefundOutcome = when (returnStatus) {
        OrderItemReturnStatusDomain.REFUNDED -> true
        OrderItemReturnStatusDomain.ITEM_REPLACED -> false
        OrderItemReturnStatusDomain.REJECTED -> refundRequested == true
        else -> null
    }

    // Once either request is submitted, the other side's window card (available / expired / final sale) must not
    // offer a second request next to it.
    val hasReturnRequest = returnStatus?.isProcessingStatus() == true || returnStatus == OrderItemReturnStatusDomain.REJECTED
    val hasRefundRequest = refundStatus?.isProcessingStatus() == true || refundStatus == OrderItemRefundStatusDomain.REJECTED
    val showReturnCard = hasReturnRequest || !hasRefundRequest
    val showRefundCard = hasRefundRequest || !hasReturnRequest

    if (refundStatus?.isProcessingStatus() == false || refundStatus != OrderItemRefundStatusDomain.REJECTED) {
        if (returnStatus != null && isRefundOutcome != true && showReturnCard) {
            ReturnCard(
                returnStatus = returnStatus,
                returnUntilDateLabel = returnUntilDateLabel,
                onRequestReturnClicked = onReturnActionButtonClicked
            )
        }
    }

    if (returnStatus?.isProcessingStatus() == false || returnStatus != OrderItemReturnStatusDomain.REJECTED) {
        if (refundStatus != null && isRefundOutcome != false && showRefundCard) {
            RefundCard(
                refundStatus = refundStatus,
                refundUntilDateLabel = refundUntilDateLabel,
                onButtonClicked = onRefundActionButtonClicked
            )
        }
    }
}

@Composable
private fun DeliveryDetailsCard(
    deliveryDateLabel: String,
    courierUi: OrderCourierUi,
    trackingId: String
) {
    SectionHeader(
        title = stringResource(R.string.screen_order_item_tracking_history_delivery_details_card_title),
        additionalTitleContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                DeliveryDetailsRow(
                    label = stringResource(R.string.screen_order_item_tracking_history_delivery_details_card_label_delivered_date),
                    value = deliveryDateLabel
                )

                DeliveryDetailsRow(
                    label = stringResource(R.string.screen_order_item_tracking_history_delivery_details_card_label_courier),
                    value = stringResource(courierUi.labelResId)
                )

                DeliveryDetailsRow(
                    label = stringResource(R.string.screen_order_item_tracking_history_delivery_details_card_label_tracking_id),
                    value = trackingId,
                    onClick = {}
                )
            }
        },
        enabled = false,
        onClick = {}
    )
}

@Composable
private fun DeliveryDetailsRow(label: String, value: String, onClick: (() -> Unit)? = null) {
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

        when (onClick) {
            null -> {
                Text(
                    text = value
                )
            }

            else -> {
                Text(
                    text = value,
                    modifier = Modifier.clickableNoRipple(onClick = onClick),
                    color = MaterialTheme.customColors.linkTextColor,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun ReturnCard(
    returnStatus: OrderItemReturnStatusDomain,
    returnUntilDateLabel: String,
    onRequestReturnClicked: () -> Unit
) {
    when (returnStatus) {
        OrderItemReturnStatusDomain.AVAILABLE -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_available_title),
                additionalTitleContentLabel = stringResource(
                    R.string.screen_order_item_tracking_history_return_card_available_description,
                    returnUntilDateLabel
                ),
                leadingIconResId = R.drawable.ic_alarm,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_return_section_button_request_return),
                        testTag = OrderItemTrackingHistoryTestTags.RETURN_ACTION_BUTTON,
                        onClick = onRequestReturnClicked
                    )
                }
            )
        }

        OrderItemReturnStatusDomain.EXPIRED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_expired_title),
                additionalTitleContentLabel = stringResource(
                    R.string.screen_order_item_tracking_history_return_card_expired_description,
                    returnUntilDateLabel
                ),
                leadingIconResId = R.drawable.ic_lock,
                leadingIconTint = MaterialTheme.customColors.white,
                additionalContent = null
            )
        }

        OrderItemReturnStatusDomain.FINAL_SALE -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_final_sale_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_final_sale_description),
                leadingIconResId = R.drawable.ic_lock,
                leadingIconTint = MaterialTheme.customColors.white,
                additionalContent = {
                    val errorColor = MaterialTheme.colors.error
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = errorColor.copy(alpha = ContentAlpha.medium))
                            .drawWithContent {
                                drawContent()

                                drawRect(
                                    color = errorColor,
                                    size = Size(SPACING_TINY.dp.toPx(), size.height)
                                )
                            }
                            .padding(SPACING_MEDIUM.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.screen_order_item_tracking_history_return_card_final_sale_additional_description),
                            color = MaterialTheme.customColors.white
                        )
                    }
                }
            )
        }

        // TODO: Add further customization in the future.
        OrderItemReturnStatusDomain.PENDING -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_pending_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_pending_description),
                leadingIconResId = R.drawable.ic_hourglass,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_return_section_button_view_return_request),
                        testTag = OrderItemTrackingHistoryTestTags.RETURN_ACTION_BUTTON,
                        onClick = onRequestReturnClicked
                    )
                }
            )
        }

        OrderItemReturnStatusDomain.REJECTED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_rejected_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_rejected_description),
                leadingIconResId = R.drawable.ic_close, // TODO: Review icon.
                leadingIconTint = MaterialTheme.customColors.genericRed,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_return_card_rejected_action_button),
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative,
                        testTag = OrderItemTrackingHistoryTestTags.RETURN_ACTION_BUTTON,
                        onClick = onRequestReturnClicked
                    )
                }
            )
        }

        OrderItemReturnStatusDomain.ITEM_REPLACED, OrderItemReturnStatusDomain.REFUNDED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_accepted_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_return_card_accepted_description),
                leadingIconResId = R.drawable.ic_check,
                leadingIconTint = MaterialTheme.customColors.greenCheckmark,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_return_card_accepted_action_button),
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundPositive,
                        testTag = OrderItemTrackingHistoryTestTags.RETURN_ACTION_BUTTON,
                        onClick = onRequestReturnClicked
                    )
                }
            )
        }
    }
}

@Composable
private fun RefundCard(
    refundStatus: OrderItemRefundStatusDomain,
    refundUntilDateLabel: String,
    onButtonClicked: () -> Unit
) {
    when (refundStatus) {
        OrderItemRefundStatusDomain.AVAILABLE -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_available_title),
                additionalTitleContentLabel = stringResource(
                    R.string.screen_order_item_tracking_history_refund_card_available_description,
                    refundUntilDateLabel
                ),
                leadingIconResId = R.drawable.ic_alarm,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_refund_section_button_request_refund),
                        testTag = OrderItemTrackingHistoryTestTags.REFUND_ACTION_BUTTON,
                        onClick = onButtonClicked
                    )
                }
            )
        }

        OrderItemRefundStatusDomain.EXPIRED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_expired_title),
                additionalTitleContentLabel = stringResource(
                    R.string.screen_order_item_tracking_history_refund_card_expired_description,
                    refundUntilDateLabel
                ),
                leadingIconResId = R.drawable.ic_lock,
                leadingIconTint = MaterialTheme.customColors.white,
                additionalContent = null
            )
        }

        OrderItemRefundStatusDomain.FINAL_SALE -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_final_sale_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_final_sale_description),
                leadingIconResId = R.drawable.ic_lock,
                leadingIconTint = MaterialTheme.customColors.white,
                additionalContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .background(color = MaterialTheme.colors.error.copy(alpha = ContentAlpha.medium))
                    ) {
                        Box(
                            modifier = Modifier
                                .width(SPACING_TINY.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colors.error)
                        )
                        Text(
                            text = stringResource(R.string.screen_order_item_tracking_history_refund_card_final_sale_additional_description),
                            modifier = Modifier.padding(SPACING_MEDIUM.dp),
                            color = MaterialTheme.customColors.white
                        )
                    }
                }
            )
        }

        OrderItemRefundStatusDomain.PENDING -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_pending_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_pending_description),
                leadingIconResId = R.drawable.ic_hourglass,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_refund_section_button_view_refund_request),
                        testTag = OrderItemTrackingHistoryTestTags.REFUND_ACTION_BUTTON,
                        onClick = onButtonClicked
                    )
                }
            )
        }

        OrderItemRefundStatusDomain.REJECTED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_rejected_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_rejected_description),
                leadingIconResId = R.drawable.ic_close,
                leadingIconTint = MaterialTheme.customColors.genericRed,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_refund_card_rejected_action_button),
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative,
                        testTag = OrderItemTrackingHistoryTestTags.REFUND_ACTION_BUTTON,
                        onClick = onButtonClicked
                    )
                }
            )
        }

        OrderItemRefundStatusDomain.ACCEPTED -> {
            GenericCard(
                titleLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_accepted_title),
                additionalTitleContentLabel = stringResource(R.string.screen_order_item_tracking_history_refund_card_accepted_description),
                leadingIconResId = R.drawable.ic_check,
                leadingIconTint = MaterialTheme.customColors.greenCheckmark,
                additionalContent = {
                    GenericCardButton(
                        label = stringResource(R.string.screen_order_item_tracking_history_refund_card_accepted_action_button),
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundPositive,
                        testTag = OrderItemTrackingHistoryTestTags.REFUND_ACTION_BUTTON,
                        onClick = onButtonClicked
                    )
                }
            )
        }
    }
}

@Composable
private fun GenericCard(
    titleLabel: String,
    additionalTitleContentLabel: String,
    @DrawableRes leadingIconResId: Int,
    leadingIconTint: Color? = null,
    additionalContent: (@Composable () -> Unit)?,
    backgroundColor: Color = SectionHeaderDefaults.BACKGROUND_COLOR
) {
    SectionHeader(
        title = titleLabel,
        additionalTitleContent = {
            Text(
                text = additionalTitleContentLabel,
                style = MaterialTheme.typography.subtitle1
            )
        },
        leadingIcon = {
            WrapImage(
                painter = painterResource(leadingIconResId),
                contentDescription = null,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                colorFilter = leadingIconTint?.let { ColorFilter.tint(color = it) }
            )
        },
        additionalContent = {
            additionalContent?.let {
                VSpacer.Small()

                it()
            }
        },
        backgroundColor = backgroundColor,
        enabled = false,
        onClick = {}
    )
}

@Composable
private fun GenericCardButton(
    label: String,
    testTag: String,
    backgroundColor: Color = ButtonDefaults.outlinedButtonColors().backgroundColor(true).value,
    onClick: () -> Unit
) {
    WrapOutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = backgroundColor)
    ) {
        Text(text = label)
    }
}

@Composable
private fun TrackingHistorySteps(
    trackingSteps: ImmutableList<OrderItemTrackingStepUi>
) {
    val completedColor = MaterialTheme.customColors.genericRed
    val activeColor = MaterialTheme.customColors.genericRed
    val pendingColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
    val textCompletedColor = MaterialTheme.colors.onSurface
    val textPendingColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    val subtextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_order_item_tracking_history_title_delivery_timeline))

        trackingSteps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circle indicator & bar
                Column(
                    modifier = Modifier.width(SPACING_MEDIUM.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circle indicator
                    Box(
                        modifier = Modifier
                            .then(
                                when (step.state) {
                                    OrderItemTrackingStepState.COMPLETED -> {
                                        Modifier
                                            .size(SPACING_MEDIUM.dp)
                                            .background(
                                                color = completedColor,
                                                shape = CircleShape
                                            )
                                    }

                                    OrderItemTrackingStepState.CURRENT -> {
                                        Modifier
                                            .requiredSize(28.dp) // 16.dp inner circle, 6.dp per side padding
                                            .background(
                                                color = activeColor.copy(alpha = 0.25f),
                                                shape = CircleShape
                                            )
                                            .wrapContentSize(Alignment.Center)
                                            .requiredSize(SPACING_MEDIUM.dp)
                                            .background(color = activeColor, shape = CircleShape)
                                    }

                                    OrderItemTrackingStepState.NEXT -> {
                                        Modifier
                                            .size(SPACING_MEDIUM.dp)
                                            .border(
                                                width = 4.dp,
                                                color = pendingColor,
                                                shape = CircleShape
                                            )
                                    }
                                }
                            )
                    )

                    // Bar
                    if (index < trackingSteps.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .width(SPACING_TINY.dp)
                                .height(32.dp)
                                .background(
                                    color = when (step.state) {
                                        OrderItemTrackingStepState.COMPLETED -> completedColor
                                        OrderItemTrackingStepState.CURRENT -> pendingColor
                                        OrderItemTrackingStepState.NEXT -> pendingColor
                                    },
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }

                // Title & location - timestamp
                TrackingHistoryStepLabels(
                    stepLabel = stringResource(step.titleResId),
                    descriptionLabel = when (val stepData = step.data) {
                        null -> stringResource(R.string.screen_order_item_tracking_history_label_tracking_step_pending)
                        else -> {
                            "${stepData.location}, ${stepData.date.label}"
                        }
                    },
                    stepLabelColor = when (step.state) {
                        OrderItemTrackingStepState.NEXT -> textPendingColor
                        else -> textCompletedColor
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < trackingSteps.lastIndex) 20.dp else 0.dp),
                    descriptionColor = when (step.state) {
                        OrderItemTrackingStepState.NEXT -> subtextColor.copy(alpha = 0.5f)
                        else -> subtextColor
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackingHistoryStepLabels(
    stepLabel: String,
    descriptionLabel: String?,
    stepLabelColor: Color,
    modifier: Modifier = Modifier,
    descriptionColor: Color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
    ) {
        Text(
            text = stepLabel,
            color = stepLabelColor,
            style = MaterialTheme.typography.h6
        )

        if (descriptionLabel != null) {
            Text(
                text = descriptionLabel,
                color = descriptionColor,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

// Previews.

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true, showSystemUi = true, apiLevel = 36
)
@Composable
private fun TrackingHistoryCardPreview() {
    val currentDate = DateUtil.getUTCInstant()
    val shippedDate = currentDate.minusDays(2)
    val departedSortingFacilityDate = shippedDate.plusDays(1)
    val outForDeliveryDate = departedSortingFacilityDate.plusDays(2)
    val deliveredDate = outForDeliveryDate.plusDays(2)

    val shipped = OrderTrackingHistoryItemDomain(
        location = "Thessaloniki",
        date = shippedDate
    ).toUi()

    val departedSortingFacility = OrderTrackingHistoryItemDomain(
        location = "Larissa",
        date = departedSortingFacilityDate
    ).toUi()

    val outForDelivery = null

    val delivered = null

    val trackingSteps = mapToTrackingStepList(
        shipped,
        departedSortingFacility,
        outForDelivery,
        delivered
    )

    MarvelXTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .systemBarsPadding()
                    .padding(SPACING_MEDIUM.dp)
            )
            TrackingHistorySteps(
                trackingSteps = trackingSteps
            )
        }
    }
}

@Preview(
    showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OrderItemTrackingHistoryScreenDeliveredPreview() {
    val orderId = "A1001567"
    val orderItemId = "A1"
    val orderItemTitle = "Super wow mug!"
    val orderItemCoverImageUrl = ""
    val orderItemPriceLabel = "150.20".withEuroSymbol()
    val sellerUserId = UserId("1")

    val orderDateMinusDays = Random.nextLong(3L..10L)
    val estimatedDeliveryDateMinPlusDays = Random.nextLong(1L..3L)
    val estimatedDeliveryDateMaxPlusDays = Random.nextLong(1L..5L)

    val orderDate = DateUtil.getUTCInstant().minusDays(orderDateMinusDays)
    val estimatedDeliveryDateMin = orderDate.plusDays(estimatedDeliveryDateMinPlusDays)
    val estimatedDeliveryDateMax = orderDate.plusDays(estimatedDeliveryDateMaxPlusDays)

    val deliveredDate = estimatedDeliveryDateMin.plusDays(1)

    val returnUntilDate = deliveredDate.plusDays(30)
    val refundUntilDate = deliveredDate.plusDays(30)

    MarvelXTheme {
        OrderItemTrackingHistoryScreen(
            onEvent = {},
            state = OrderItemTrackingHistoryContract.State(
                uiState = UiState.Success(
                    OrderItemTrackingHistoryUi(
                        trackingId = "T1002350",
                        courier = OrderCourierUi.entries.random(),
                        estimatedDelivery = OrderEstimatedDeliveryDateRangeDomain(
                            from = estimatedDeliveryDateMin.toLocalDate(),
                            to = estimatedDeliveryDateMax.toLocalDate()
                        ).toUi(),
                        status = OrderItemStatusUi.Delivered,
                        steps = persistentListOf(),
                        currentStepItem = OrderTrackingHistoryItemDomain(
                            location = "Athens",
                            date = estimatedDeliveryDateMin.plusDays(1)
                        ).toUi(),
                        returnEligibility = OrderItemReturnEligibilityDomain(
                            returnStatus = OrderItemReturnStatusDomain.AVAILABLE,
                            returnUntilDate = returnUntilDate,
                            refundStatus = OrderItemRefundStatusDomain.REJECTED,
                            refundUntilDate = refundUntilDate,
                            refundRequested = null
                        ).toUi()
                    )
                ),
                sellerDetailsUiState = UiState.Success(
                    RepositoryBasicProfileDetails(
                        firstName = "Alexandros",
                        lastName = "Voliotis",
                        profileImageUrl = ""
                    )
                ),
                orderItemDetails = OrderItemDetails(
                    orderId = OrderIdDomain(orderId),
                    orderItemId = OrderIdDomain(orderItemId),
                    orderItemTitle = orderItemTitle,
                    orderItemCoverImageUrl = orderItemCoverImageUrl,
                    orderItemPriceLabel = orderItemPriceLabel
                ),
                sellerUserId = sellerUserId
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {}
        )
    }
}