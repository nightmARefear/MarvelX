package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.GenericInfoScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.components.icons.ProcessingRequestIconLottie
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onError
import com.alexgreece45.marvelx.ui.model_generic.onLoading
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCourierUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.OrderTrackingHistoryItemUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryErrorType
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingStepState
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingStepUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.mapToTrackingStepList
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.minusDays
import com.alexgreece45.marvelx.util.extensions.plusDays
import com.alexgreece45.marvelx.util.extensions.setText
import com.alexgreece45.marvelx.util.extensions.showToast
import com.alexgreece45.marvelx.util.extensions.toOrderEstimatedDeliveryFormatFull
import kotlinx.coroutines.flow.Flow

@Composable
fun OrderItemTrackingHistoryScreen(
    onEvent: (OrderItemTrackingHistoryContract.Event) -> Unit,
    state: OrderItemTrackingHistoryContract.State,
    effect: Flow<OrderItemTrackingHistoryContract.Effect>,
    onNavigationEffect: (OrderItemTrackingHistoryContract.Effect.Navigation) -> Unit,
    orderId: String,
    orderItemId: String,
    sellerUserId: String,
    orderItemTitle: String,
    orderItemCoverImageUrl: String,
    orderItemPriceLabel: String,
) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is OrderItemTrackingHistoryContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is OrderItemTrackingHistoryContract.Effect.SetClipboardText -> {
                clipboard.setText(uiEffect.text)
                context.showToast(uiEffect.toastMessage)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(
            OrderItemTrackingHistoryContract.Event.Init(
                orderId = orderId,
                orderItemId = orderItemId,
                sellerId = sellerUserId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = orderItemCoverImageUrl,
                orderItemPriceLabel = orderItemPriceLabel
            )
        )
    }

    ContentScreen(
        isLoading = state.uiState is UiState.Loading,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = {
                onEvent(OrderItemTrackingHistoryContract.Event.OnBack)
            }
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
                    GenericInfoScreen(
                        title = stringResource(
                            if (isErrorTypeNoData) R.string.screen_order_item_tracking_history_error_label_no_data else R.string.screen_order_item_tracking_history_error_label_fetching_data,
                            state.orderId,
                            state.orderItemId
                        ),
                        titleColor = MaterialTheme.colors.error,
                        buttonLabel = stringResource(if (isErrorTypeNoData) R.string.generic_go_back else R.string.generic_retry_button),
                        modifier = Modifier.fillMaxSize(),
                        onButtonClicked = {
                            onEvent(OrderItemTrackingHistoryContract.Event.OnErrorButtonClicked)
                        }
                    )
                }
                .onSuccess { orderItemTrackingHistoryUi ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                    ) {
                        // Order IDs
                        Text(
                            text = stringResource(
                                R.string.screen_order_item_tracking_history_label_order_ids,
                                orderId, orderItemId
                            ),
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )

                        OrderItemCard(
                            title = orderItemTitle,
                            coverImageUrl = orderItemCoverImageUrl,
                            priceLabel = orderItemPriceLabel,
                            status = orderItemTrackingHistoryUi.status,
                            currentStepItem = orderItemTrackingHistoryUi.currentStepItem,
                            sellerBasicProfileDetailsState = state.sellerDetailsUiState,
                            onSellerCardClicked = {
                                onEvent(OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnClicked)
                            },
                            onSellerCardErrorButtonClicked = {
                                onEvent(OrderItemTrackingHistoryContract.Event.SellerDetailsCardEvents.OnErrorButtonClicked)
                            },
                        )

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

                        if (orderItemTrackingHistoryUi.status !is OrderItemStatusUi.Delivered) {
                            EstimatedDeliveryCard(estimatedDateRangeLabel = orderItemTrackingHistoryUi.estimatedDelivery.estimatedDateRangeLabel)
                        }

                        TrackingHistorySteps(trackingSteps = orderItemTrackingHistoryUi.steps)
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
    onSellerCardClicked: () -> Unit,
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
                    topLeft = Offset(0f, 0f),
                    size = Size(SPACING_TINY.dp.toPx(), size.height)
                )
            }
    ) {
        ListingCard(
            title = title,
            additionalTitleContent = {
                Text(
                    text = priceLabel,
                    style = MaterialTheme.typography.subtitle1
                )
            },
            titleContentAlignment = Alignment.CenterVertically,
            coverImageUrl = coverImageUrl,
            trailingIcon = null,
            additionalContent = {
                VSpacer.Medium()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    WrapHorizontalDivider()

                    // Current Status.
                    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
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
                                    .size(SPACING_MEDIUM.dp)
                                    .background(color = statusColor, shape = CircleShape)
                                    .drawBehind {
                                        drawCircle(
                                            color = statusColor.copy(alpha = 0.25f),
                                            radius = size.width / 2 + 6.dp.toPx()
                                        )
                                    }
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
                            text = stringResource(R.string.screen_order_item_tracking_history_title_seller),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )

                        SellerDetailsCard(
                            sellerBasicProfileDetailsState = sellerBasicProfileDetailsState,
                            onClicked = onSellerCardClicked,
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
                onRetryClick = onErrorButtonClicked
            )
        }
        .onSuccess { profileDetails ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableNoRipple(onClick = onClicked),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
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

                    Text(
                        text = profileDetails.fullName,
                        style = MaterialTheme.typography.subtitle1
                    )
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
        title = stringResource(R.string.screen_order_item_tracking_history_title_courier),
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

                        ThrottledIconButton(onClick = onCopyButtonClicked) {
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.screen_order_item_tracking_history_courier_card_button)
                )
            }
        },
        enabled = false,
        onClick = {},
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
private fun TrackingHistorySteps(
    trackingSteps: List<OrderItemTrackingStepUi>
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
                            .size(SPACING_MEDIUM.dp)
                            .then(
                                when (step.state) {
                                    OrderItemTrackingStepState.COMPLETED -> {
                                        Modifier.background(
                                            color = completedColor,
                                            shape = CircleShape
                                        )
                                    }

                                    OrderItemTrackingStepState.CURRENT -> {
                                        Modifier
                                            .background(activeColor, CircleShape)
                                            .drawBehind {
                                                drawCircle(
                                                    color = activeColor.copy(alpha = 0.25f),
                                                    radius = size.width / 2 + 6.dp.toPx()
                                                )
                                            }
                                    }

                                    OrderItemTrackingStepState.NEXT -> {
                                        Modifier.border(
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
    descriptionColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
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

    val shipped = OrderTrackingHistoryItemUi(
        location = "Thessaloniki",
        date = InstantUi(
            value = shippedDate,
            label = shippedDate.toOrderEstimatedDeliveryFormatFull()
        )
    )

    val departedSortingFacility = OrderTrackingHistoryItemUi(
        location = "Larissa",
        date = InstantUi(
            value = departedSortingFacilityDate,
            label = departedSortingFacilityDate.toOrderEstimatedDeliveryFormatFull()
        )
    )

    val outForDelivery = null

    val delivered = null

    MarvelXTheme {
        Scaffold {
            TrackingHistorySteps(
                trackingSteps = mapToTrackingStepList(
                    shipped,
                    departedSortingFacility,
                    outForDelivery,
                    delivered
                )
            )
        }
    }
}
