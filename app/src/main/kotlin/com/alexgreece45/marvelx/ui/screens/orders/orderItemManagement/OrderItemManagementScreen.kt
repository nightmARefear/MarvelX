package com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.model_generic.onError
import com.alexgreece45.marvelx.ui.model_generic.onSuccess
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.model.OrderItemManagementContract
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.showToast
import com.alexgreece45.marvelx.util.extensions.withEuroSymbol
import kotlinx.coroutines.flow.emptyFlow
import java.math.BigDecimal

@Composable
internal fun OrderItemManagementScreen(
    onEvent: (OrderItemManagementContract.Event) -> Unit,
    state: OrderItemManagementContract.State,
    effect: SingleObserverFlow<OrderItemManagementContract.Effect>,
    onNavigationEffect: (OrderItemManagementContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current

    ObserveUiEffect(flow = effect, tag = "OrderItemManagementScreen") { uiEffect ->
        when (uiEffect) {
            is OrderItemManagementContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is OrderItemManagementContract.Effect.ShowToast -> {
                context.showToast(uiEffect.message)
            }
        }
    }

    ContentScreen(
        isLoading = state.uiState.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = {
                onEvent(OrderItemManagementContract.Event.OnBack)
            }
        ),
        onBack = {
            onEvent(OrderItemManagementContract.Event.OnBack)
        },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_order_item_management_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            state.uiState
                .onError {
                    GenericErrorScreen(
                        errorMessage = stringResource(
                            R.string.screen_order_item_management_error_label_fetching_data,
                            state.orderId.value,
                            state.orderItemId.value
                        ),
                        onErrorButtonClick = {
                            onEvent(OrderItemManagementContract.Event.OnErrorButtonClicked)
                        }
                    )
                }
                .onSuccess { orderItemUi ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                    ) {
                        // Order IDs.
                        Text(
                            text = stringResource(
                                R.string.screen_order_item_management_label_order_ids,
                                state.orderId.value,
                                state.orderItemId.value
                            ),
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )

                        OrderItemPreviewCard(
                            title = orderItemUi.title,
                            coverImageUrl = orderItemUi.coverImageUrl,
                            priceLabel = orderItemUi.price.label
                        )

                        CurrentStatusSection(status = orderItemUi.status)

                        StatusManagementSection(
                            currentStatus = orderItemUi.status,
                            selectedNewStatus = state.selectedNewStatus,
                            isDropdownExpanded = state.isDropdownExpanded,
                            onDropdownClicked = {
                                onEvent(OrderItemManagementContract.Event.DropdownEvents.OnClicked)
                            },
                            onDropdownDismissed = {
                                onEvent(OrderItemManagementContract.Event.DropdownEvents.OnDismissed)
                            },
                            onDropdownItemSelected = { newStatus ->
                                onEvent(
                                    OrderItemManagementContract.Event.DropdownEvents.OnItemSelected(
                                        newStatus
                                    )
                                )
                            },
                            onApplyStatusButtonClicked = {
                                onEvent(OrderItemManagementContract.Event.OnApplyStatusButtonClicked)
                            }
                        )
                    }
                }
        }
    }
}

@Composable
private fun OrderItemPreviewCard(
    title: String,
    coverImageUrl: String,
    priceLabel: String
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
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun CurrentStatusSection(status: OrderItemStatusUi) {
    val statusColor = status.backgroundColor

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        Text(
            text = stringResource(R.string.screen_order_item_management_label_current_status),
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
            )

            Text(
                text = stringResource(status.labelResId),
                color = statusColor,
                style = MaterialTheme.typography.h6
            )
        }

        WrapHorizontalDivider()
    }
}

@Composable
private fun StatusManagementSection(
    currentStatus: OrderItemStatusUi,
    selectedNewStatus: OrderItemStatusUi?,
    isDropdownExpanded: Boolean,
    onDropdownClicked: () -> Unit,
    onDropdownDismissed: () -> Unit,
    onDropdownItemSelected: (OrderItemStatusUi) -> Unit,
    onApplyStatusButtonClicked: () -> Unit
) {
    val statusOptions = listOf(
        OrderItemStatusUi.Processing,
        OrderItemStatusUi.Shipped,
        OrderItemStatusUi.DepartedSortingFacility,
        OrderItemStatusUi.OutForDelivery,
        OrderItemStatusUi.Delivered
    )

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)) {
        ExposedDropdownMenu(
            label = stringResource(R.string.screen_order_item_management_dropdown_label_new_status),
            expanded = isDropdownExpanded,
            modifier = Modifier.fillMaxWidth(),
            value = selectedNewStatus?.let { stringResource(it.labelResId) },
            onDismissRequest = onDropdownDismissed,
            onExpandedChange = onDropdownClicked
        ) {
            statusOptions.forEach { statusOption ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDropdownItemSelected(statusOption)
                    }
                ) {
                    Text(text = stringResource(statusOption.labelResId))
                }
            }
        }

        WrapButton(
            onClick = onApplyStatusButtonClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedNewStatus != null && selectedNewStatus != currentStatus
        ) {
            Text(text = stringResource(R.string.screen_order_item_management_button_apply_status))
        }
    }
}

// Previews.

@Preview(
    showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OrderItemManagementScreenPreview() {
    val orderId = "A1001567"
    val orderItemId = "A1"

    MarvelXTheme {
        OrderItemManagementScreen(
            onEvent = {},
            state = OrderItemManagementContract.State(
                uiState = UiState.Success(
                    OrderItemUi(
                        orderItemId = OrderIdDomain(orderItemId),
                        sellerUserId = "1",
                        title = "Super wow mug!",
                        coverImageUrl = "",
                        status = OrderItemStatusUi.Shipped,
                        price = BigDecimalUi(
                            value = BigDecimal("150.20"),
                            label = "150.20".withEuroSymbol()
                        )
                    )
                ),
                orderId = OrderId(orderId),
                orderItemId = OrderId(orderItemId),
                selectedNewStatus = OrderItemStatusUi.OutForDelivery
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {}
        )
    }
}