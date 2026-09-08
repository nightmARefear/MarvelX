package com.alexgreece45.marvelx.ui.screens.orders

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.extensions.plusDays
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.extensions.toOrderFormatShort
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericLazyItemInfoScreen
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.chips.WrapChip
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onError
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderStatusInfoBottomSheetContent
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderEstimatedDeliveryDateRangeUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderPreviewUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersBottomSheetType
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersContract
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.toOrderFormatShort
import kotlinx.coroutines.launch

private val LISTING_IMAGE_SIZE = ConstantsUi.LISTING_IMAGE_NORMAL_SIZE

@Composable
fun OrdersScreen(
    onEvent: (OrdersContract.Event) -> Unit,
    state: OrdersContract.State,
    effect: SingleObserverFlow<OrdersContract.Effect>,
    onNavigationEffect: (OrdersContract.Effect.Navigation) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    ObserveUiEffect(flow = effect, tag = "OrdersScreen") { uiEffect ->
        when (uiEffect) {
            is OrdersContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            OrdersContract.Effect.HideBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }

            OrdersContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(OrdersContract.Event.Init)
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            val type = state.ordersBottomSheetType ?: return@WrapModalBottomSheetLayout

            ModalBottomSheetContent {
                when (type) {
                    is OrdersBottomSheetType.OrderStatusInfo -> {
                        OrderStatusInfoBottomSheetContent(
                            status = type.statusUi,
                            extraContent = {
                                VSpacer.Custom(32.dp)

                                WrapButton(
                                    onClick = {
                                        onEvent(
                                            OrdersContract.Event.OrderStatusInfoBottomSheetEvents.OnViewOrderDetailsClicked(
                                                orderId = type.orderId
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative,
                                        contentColor = MaterialTheme.customColors.buttonContentWhite
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_order_details_status_info_bottom_sheet_button)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        sheetState = modalBottomSheetState,
        onDismissed = {
            onEvent(OrdersContract.Event.OrderStatusInfoBottomSheetEvents.OnBottomSheetDismissed)
        }
    ) {
        ContentScreen(
            isLoading = state.uiState is UiState.Loading,
            navigableAction = ScreenNavigableAction.Backable(
                onBack = {
                    onEvent(OrdersContract.Event.OnBackClicked)
                }
            ),
            onBack = {
                onEvent(OrdersContract.Event.OnBackClicked)
            },
            showToolbar = true,
            toolbarTitle = stringResource(R.string.screen_orders_toolbar_title)
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
                                errorMessage = stringResource(R.string.screen_orders_error_loading_orders),
                                onRetryClick = {
                                    onEvent(OrdersContract.Event.OnErrorButtonClicked)
                                }
                            )
                        }
                    }
                    .onSuccess { orders ->
                        if (orders.isEmpty()) {
                            item(key = "emptyOrdersView") {
                                GenericLazyItemInfoScreen(
                                    title = stringResource(R.string.screen_orders_empty_orders_found_title),
                                    buttonLabel = stringResource(R.string.screen_orders_empty_orders_found_button),
                                    onButtonClicked = { onEvent(OrdersContract.Event.OnEmptyOrdersButtonClicked) }
                                )
                            }
                            return@LazyColumn
                        }

                        items(
                            items = orders,
                            key = { order -> order.orderId + order.hashCode() }
                        ) { order ->
                            OrderCard(
                                order = order,
                                onClicked = {
                                    onEvent(
                                        OrdersContract.Event.OrderEvents.OnClicked(
                                            order.orderId
                                        )
                                    )
                                },
                                onStatusChipClicked = {
                                    onEvent(
                                        OrdersContract.Event.OrderEvents.OnStatusChipClicked(
                                            orderId = order.orderId,
                                            orderStatusUi = order.status
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrderCard( // TODO: Fix issue where if the data is refreshed and the status changes the old one is preserved in the on-click.
    order: OrderPreviewUi,
    onClicked: () -> Unit,
    onStatusChipClicked: () -> Unit
) {
    Card(
        onClick = onClicked,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SPACING_MEDIUM.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                // Order ID & status chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.screen_orders_order_id, order.orderId),
                        color = MaterialTheme.customColors.genericRed,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                        style = MaterialTheme.typography.h5
                    )

                    WrapChip(
                        onClick = onStatusChipClicked,
                        colors = ChipDefaults.chipColors(
                            backgroundColor = order.status.backgroundColor
                        ),
                        content = {
                            Text(text = stringResource(order.status.labelResId))
                        }
                    )
                }

                OrderCardLabel(
                    label = stringResource(
                        R.string.screen_orders_order_order_date,
                        order.orderDate.label
                    )
                )
                OrderCardLabel(
                    label = stringResource(
                        R.string.screen_orders_order_total_price,
                        order.price.label
                    )
                )

                WrapHorizontalDivider(modifier = Modifier.fillMaxWidth())
            }

            VSpacer.Medium()

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = LISTING_IMAGE_SIZE),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                itemsIndexed(
                    items = order.coverImageUrls,
                    key = { _, coverImageUrl -> coverImageUrl }
                ) { index, coverImageUrl ->
                    ListingImage(
                        imageUrl = coverImageUrl,
                        title = index.toString(),
                        modifier = Modifier.size(LISTING_IMAGE_SIZE)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCardLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.subtitle1
    )
}

@Preview(
    showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OrderCardPreview() {
    val orderDate = DateUtil.getUTCInstant()
    val deliveryDateMin = DateUtil.getUTCInstant().plusDays(2)
    val deliveryDateMax = DateUtil.getUTCInstant().plusDays(4)
    val price = "150.20".toBigDecimal()
    MarvelXTheme {
        OrderCard(
            order = OrderPreviewUi(
                orderId = "A1001890",
                coverImageUrls = listOf("", "", ""),
                orderDate = InstantUi(
                    value = orderDate,
                    label = orderDate.toOrderFormatShort()
                ),
                estimatedDeliveryDate = OrderEstimatedDeliveryDateRangeUi(
                    from = deliveryDateMin.toLocalDate().let {
                        LocalDateUi(
                            value = it,
                            label = it.toOrderFormatShort()
                        )
                    },
                    to = deliveryDateMax.toLocalDate().let {
                        LocalDateUi(
                            value = it,
                            label = it.toOrderFormatShort()
                        )
                    }
                ),
                status = OrderStatusUi.Shipped,
                price = BigDecimalUi(
                    value = price,
                    label = price.formatPrice(2)
                )
            ),
            onClicked = {},
            onStatusChipClicked = {}
        )
    }
}