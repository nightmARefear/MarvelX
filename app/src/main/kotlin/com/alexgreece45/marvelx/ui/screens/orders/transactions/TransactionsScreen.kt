package com.alexgreece45.marvelx.ui.screens.orders.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.LoadingScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.isNearEnd
import com.alexgreece45.marvelx.ui.components.listing.ListingCard
import com.alexgreece45.marvelx.ui.components.listing.ListingPriceLabel
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderItemStatusChip
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.statusColors
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionUi
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsContract
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTab
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTabState
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customShapes
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import java.math.BigDecimal
import java.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter

/**
 * How many rows from the end of the list the next page is requested at.
 */
private const val LOAD_MORE_PREFETCH_DISTANCE = 3

@Composable
internal fun TransactionsScreen(
    onEvent: (TransactionsContract.Event) -> Unit,
    state: TransactionsContract.State,
    effect: SingleObserverFlow<TransactionsContract.Effect>,
    onNavigationEffect: (TransactionsContract.Effect.Navigation) -> Unit,
    snackbarEffects: SingleObserverFlow<SnackbarEvent>,
) {
    ObserveUiEffect(flow = effect, tag = "TransactionsScreen") { uiEffect ->
        when (uiEffect) {
            is TransactionsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(TransactionsContract.Event.Init)
    }

    ContentScreen(
        isLoading = false,
        navigableAction = ScreenNavigableAction.Backable(
            testTag = TransactionsTestTags.BACK_BUTTON,
            onBack = { onEvent(TransactionsContract.Event.OnBackClicked) }
        ),
        onBack = { onEvent(TransactionsContract.Event.OnBackClicked) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_transactions_toolbar_title),
        snackbarEvents = snackbarEffects
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TransactionsTabRow(
                selectedTab = state.selectedTab,
                onTabClicked = { tab -> onEvent(TransactionsContract.Event.OnTabClicked(tab)) }
            )

            // A separate list per tab, so each keeps its own scroll position.
            key(state.selectedTab) {
                TransactionsTabContent(
                    tab = state.selectedTab,
                    tabState = state.selectedTabState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun TransactionsTabRow(
    selectedTab: TransactionsTab,
    onTabClicked: (TransactionsTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                color = MaterialTheme.customColors.genericRed
            )
        }
    ) {
        TransactionsTab.entries.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabClicked(tab) },
                modifier = Modifier.testTag(TransactionsTestTags.tab(tab))
            ) {
                Text(
                    text = stringResource(tab.titleResId),
                    modifier = Modifier.padding(vertical = SPACING_SMALL.dp)
                )
            }
        }
    }
}

@Composable
private fun TransactionsTabContent(
    tab: TransactionsTab,
    tabState: TransactionsTabState,
    onEvent: (TransactionsContract.Event) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        lazyListState.isNearEnd(prefetchDistance = LOAD_MORE_PREFETCH_DISTANCE)
            .filter { isNearEnd -> isNearEnd }
            .collect {
                onEvent(TransactionsContract.Event.OnEndOfListScrolled)
            }
    }

    if (tabState.isLoading && !tabState.isRefreshing) {
        LoadingScreen()
        return
    }

    PullRefreshable(
        onRefresh = { onEvent(TransactionsContract.Event.OnPullToRefresh) },
        isRefreshing = tabState.isRefreshing
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            // The column already has the screen padding (like the Friends tabs), so only leave the
            // same medium gap under the tab row that Friends does.
            contentPadding = PaddingValues(top = SPACING_MEDIUM.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            if (tabState.hasError) {
                item(key = "errorView") {
                    LazyItemContentErrorScreen(
                        errorMessage = stringResource(R.string.screen_transactions_error_loading),
                        retryTestTag = TransactionsTestTags.ERROR_RETRY_BUTTON,
                        onRetryClick = { onEvent(TransactionsContract.Event.OnErrorRetryClicked) }
                    )
                }
                return@LazyColumn
            }

            if (tabState.items.isEmpty()) {
                item(key = "emptyView") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(
                                when (tab) {
                                    TransactionsTab.PURCHASES -> R.string.screen_transactions_empty_purchases
                                    TransactionsTab.SALES -> R.string.screen_transactions_empty_sales
                                }
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    }
                }
                return@LazyColumn
            }

            items(
                items = tabState.items,
                key = { it.orderItemId.value }
            ) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    onClick = { onEvent(TransactionsContract.Event.OnTransactionClicked(transaction)) }
                )
            }

            item(key = "additionalItemsLoadingView") {
                AnimatedVisibility(
                    visible = tabState.isFetchingNewItems,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                            color = MaterialTheme.customColors.genericRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: TransactionUi,
    onClick: () -> Unit
) {
    ListingCard(
        title = transaction.title,
        titleMaxLines = 2,
        coverImageUrl = transaction.coverImageUrl,
        additionalTitleContent = { ListingPriceLabel(price = transaction.price.label) },
        trailingIcon = null,
        additionalContent = {
            Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                Text(
                    text = stringResource(
                        R.string.screen_transactions_label_order_date,
                        transaction.orderId.formatted,
                        transaction.date.label
                    ),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrderItemStatusChip(status = transaction.status)

                    transaction.returnStatus?.let { returnStatus ->
                        ReturnStatusChip(
                            returnStatus = returnStatus,
                            refundAmountLabel = transaction.refundAmount?.label
                        )
                    }
                }
            }
        },
        modifier = Modifier.testTag(TransactionsTestTags.transactionRow(transaction.orderItemId.value)),
        onClicked = onClick
    )
}

@Composable
private fun ReturnStatusChip(
    returnStatus: OrderItemReturnStatusUi,
    refundAmountLabel: String?
) {
    val colors = returnStatus.statusColors
    Text(
        text = if (returnStatus == OrderItemReturnStatusUi.REFUNDED && refundAmountLabel != null) {
            stringResource(R.string.screen_transactions_label_refunded_amount, refundAmountLabel)
        } else {
            stringResource(returnStatus.labelResId)
        },
        color = colors.contentColor,
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier
            .background(
                color = colors.backgroundColor,
                shape = MaterialTheme.customShapes.chipCornerShape
            )
            .padding(horizontal = SPACING_SMALL.dp, vertical = SPACING_TINY.dp)
    )
}

// Previews.

private val previewTransactions = persistentListOf(
    TransactionUi(
        orderId = OrderIdDomain("3"),
        orderItemId = OrderIdDomain("5"),
        title = "Canas 50x20",
        coverImageUrl = "",
        price = BigDecimalUi(value = BigDecimal("234.10"), label = "234.10€"),
        date = InstantUi(value = Instant.parse("2026-09-13T10:15:00Z"), label = "Sep 13 2026"),
        status = OrderItemStatusUi.Shipped,
        returnStatus = null,
        refundAmount = null,
        counterpartyUserId = "2"
    ),
    TransactionUi(
        orderId = OrderIdDomain("2"),
        orderItemId = OrderIdDomain("4"),
        title = "Crazy rare marvel card!!!",
        coverImageUrl = "",
        price = BigDecimalUi(value = BigDecimal("289.23"), label = "289.23€"),
        date = InstantUi(value = Instant.parse("2026-09-01T09:00:00Z"), label = "Sep 01 2026"),
        status = OrderItemStatusUi.Delivered,
        returnStatus = OrderItemReturnStatusUi.REFUNDED,
        refundAmount = BigDecimalUi(value = BigDecimal("280.00"), label = "280.00€"),
        counterpartyUserId = "4"
    ),
)

@Preview
@Composable
private fun TransactionsScreenPreview() {
    MarvelXTheme {
        TransactionsScreen(
            onEvent = {},
            state = TransactionsContract.State(
                purchases = TransactionsTabState(
                    isLoading = false,
                    currentPage = 1,
                    items = previewTransactions
                )
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {},
            snackbarEffects = SingleObserverFlow(emptyFlow())
        )
    }
}

@Preview
@Composable
private fun TransactionsScreenEmptySalesPreview() {
    MarvelXTheme {
        TransactionsScreen(
            onEvent = {},
            state = TransactionsContract.State(
                selectedTab = TransactionsTab.SALES,
                sales = TransactionsTabState(isLoading = false, currentPage = 1, hasReachedEnd = true)
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {},
            snackbarEffects = SingleObserverFlow(emptyFlow())
        )
    }
}
