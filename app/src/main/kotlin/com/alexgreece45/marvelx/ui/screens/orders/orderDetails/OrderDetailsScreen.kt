package com.alexgreece45.marvelx.ui.screens.orders.orderDetails

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.checkoutDataCard.CheckoutInfoCard
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.orderSumamaryCard.OrderSummaryCard
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onError
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderStatusInfoBottomSheetContent
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsContract
import com.alexgreece45.marvelx.ui.theme.values.customShapes
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun OrderDetailsScreen(
    onEvent: (OrderDetailsContract.Event) -> Unit,
    state: OrderDetailsContract.State,
    effect: Flow<OrderDetailsContract.Effect>,
    onNavigationEffect: (OrderDetailsContract.Effect.Navigation) -> Unit,
    orderId: String
) {

    val bottomSheetState = rememberWrapModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is OrderDetailsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            OrderDetailsContract.Effect.HideBottomSheet -> {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }

            OrderDetailsContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(OrderDetailsContract.Event.Init(orderId))
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            val sheetType = state.bottomSheetType ?: return@WrapModalBottomSheetLayout

            ModalBottomSheetContent {
                when (sheetType) {
                    is OrderDetailsBottomSheetType.OrderStatusInfo -> {
                        OrderStatusInfoBottomSheetContent(status = sheetType.orderStatusUi)
                    }
                }
            }
        },
        sheetState = bottomSheetState
    ) {
        ContentScreen(
            isLoading = state.uiState is UiState.Loading,
            navigableAction = ScreenNavigableAction.Backable(
                onBack = {
                    onEvent(OrderDetailsContract.Event.OnBack)
                }
            ),
            onBack = {
                onEvent(OrderDetailsContract.Event.OnBack)
            },
            showToolbar = true,
            toolbarTitle = stringResource(
                R.string.screen_order_details_toolbar_title,
                state.orderId
            )
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                state.uiState
                    .onError {
                        item(key = "errorView") {
                            LazyItemContentErrorScreen(
                                errorMessage = stringResource(
                                    R.string.screen_order_details_error,
                                    state.orderId
                                ),
                                onRetryClick = {
                                    onEvent(OrderDetailsContract.Event.OnErrorButtonClicked)
                                }
                            )
                        }
                    }
                    .onSuccess { order ->
                        item(key = "orderStatus") {
                            OrderStatusBanner(
                                orderStatusUi = order.status,
                                statusDescription = order.statusDescription,
                                onClick = { onEvent(OrderDetailsContract.Event.OnOrderStatusBannerClicked) }
                            )
                        }

                        item(key = "orderItemsTitle") {
                            Text(
                                text = stringResource(R.string.screen_order_details_title_order_items),
                                style = MaterialTheme.typography.h6
                            )
                        }

                        items(
                            items = order.items,
                            key = { item -> item.id.value }
                        ) { order ->
                            OrderItemCard(
                                id = order.id.formatted,
                                title = order.title,
                                coverImageUrl = order.coverImageUrl,
                                priceLabel = order.price.label,
                                status = order.status,
                                buttonEnabled = order.status !is OrderItemStatusUi.Processing,
                                onButtonClicked = {
                                    onEvent(
                                        OrderDetailsContract.Event.OrderItemEvents.OnTrackButtonClicked(
                                            orderItem = order
                                        )
                                    )
                                }
                            )
                        }

                        item(key = "checkoutInfoCard") {
                            CheckoutInfoCard(
                                orderCheckoutInfo = order.orderCheckoutInfo,
                                isShippingDetailsEditable = false,
                                isPaymentDetailsEditable = false,
                                onShippingDetailsEditButtonClicked = {},
                                onPaymentDetailsEditButtonClicked = {}
                            )
                        }

                        item(key = "orderSummaryCard") {
                            OrderSummaryCard(
                                title = stringResource(R.string.screen_order_details_order_summary_title),
                                subtotalPrice = order.subtotalPrice.label,
                                shippingPrice = order.shippingPrice.label,
                                totalPrice = order.totalPrice.label,
                                additionalContent = null
                            )
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrderItemCard(
    id: String,
    title: String,
    coverImageUrl: String,
    priceLabel: String,
    status: OrderItemStatusUi,
    buttonEnabled: Boolean,
    onButtonClicked: () -> Unit,
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = {
            Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                Text(
                    text = stringResource(
                        R.string.screen_order_details_order_item_card_tracking_id_label,
                        id
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                OrderItemStatusChip(status = status)
            }
        },
        trailingIcon = {
            Text(
                text = priceLabel,
                style = MaterialTheme.typography.subtitle1
            )
        },
        additionalContent = {
            VSpacer.Small()

            WrapOutlinedButton(
                onClick = onButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = buttonEnabled
            ) {
                Text(
                    text = stringResource(R.string.screen_order_details_order_item_card_button)
                )
            }
        },
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun OrderItemStatusChip(status: OrderItemStatusUi) {
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
                val circleColor = statusContentColor
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(color = circleColor)
                }
            }

            Text(
                text = stringResource(status.labelResId),
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrderStatusBanner(
    orderStatusUi: OrderStatusUi,
    statusDescription: String,
    onClick: () -> Unit
) {
    val statusColor = orderStatusUi.backgroundColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .drawWithContent {
                drawContent()

                drawRect(
                    color = statusColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(SPACING_TINY.dp.toPx(), size.height)
                )
            }
    ) {
        SectionHeader(
            title = stringResource(R.string.screen_order_details_title_order_status),
            additionalTitleContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    Text(
                        text = stringResource(orderStatusUi.labelResId),
                        color = statusColor,
                        style = MaterialTheme.typography.h6
                    )

                    Text(
                        text = statusDescription,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = statusColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = orderStatusUi.icon,
                        contentDescription = null,
                        modifier = Modifier.size(
                            ConstantsUi.MIN_ICON_SIZE
                        )
                    )
                }
            },
            onClick = onClick
        )
    }
}
