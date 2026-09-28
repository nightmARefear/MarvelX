package com.alexgreece45.marvelx.ui.screens.orders.transactions

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionUi
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsContract
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTab
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTabState
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [TransactionsViewModel::class])
internal class TransactionsViewModel(
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    snackbarDelegate: SnackbarDelegate,
) : BaseViewModel<TransactionsContract.Event, TransactionsContract.State, TransactionsContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    private val fetchJobs: MutableMap<TransactionsTab, Job> = mutableMapOf()

    override fun setInitialState(): TransactionsContract.State = TransactionsContract.State()

    override fun handleEvents(event: TransactionsContract.Event) {
        when (event) {
            TransactionsContract.Event.Init -> init()
            TransactionsContract.Event.OnBackClicked -> onBackClicked()
            is TransactionsContract.Event.OnTabClicked -> onTabClicked(event.tab)
            TransactionsContract.Event.OnEndOfListScrolled -> onEndOfListScrolled()
            TransactionsContract.Event.OnPullToRefresh -> onPullToRefresh()
            TransactionsContract.Event.OnErrorRetryClicked -> onErrorRetryClicked()
            is TransactionsContract.Event.OnTransactionClicked -> onTransactionClicked(event.transaction)
        }
    }

    // Event functions.

    /**
     * Runs every time the screen enters composition (e.g. when coming back from an order item), so
     * the first page is only requested once. Pull to refresh reloads it.
     */
    private fun init() {
        val tab = currentState.selectedTab
        if (currentState.tabState(tab).hasLoadedFirstPage || fetchJobs[tab]?.isActive == true) return

        fetchPage(tab = tab, page = 1)
    }

    private fun onBackClicked() {
        setEffect {
            TransactionsContract.Effect.Navigation.GoBack
        }
    }

    private fun onTabClicked(tab: TransactionsTab) {
        if (currentState.selectedTab == tab) return

        setState {
            copy(selectedTab = tab)
        }

        val tabState = currentState.tabState(tab)
        if (!tabState.hasLoadedFirstPage && !tabState.hasError && fetchJobs[tab]?.isActive != true) {
            fetchPage(tab = tab, page = 1)
        }
    }

    private fun onEndOfListScrolled() {
        val tab = currentState.selectedTab
        val tabState = currentState.tabState(tab)
        if (tabState.isLoading
            || tabState.isRefreshing
            || tabState.isFetchingNewItems
            || tabState.hasReachedEnd
            || tabState.items.isEmpty()
        ) return

        updateTab(tab) {
            copy(isFetchingNewItems = true)
        }
        fetchPage(tab = tab, page = tabState.currentPage + 1)
    }

    private fun onPullToRefresh() {
        val tab = currentState.selectedTab
        fetchJobs[tab]?.cancel()

        updateTab(tab) {
            copy(
                isRefreshing = true,
                isFetchingNewItems = false
            )
        }
        fetchPage(tab = tab, page = 1)
    }

    private fun onErrorRetryClicked() {
        val tab = currentState.selectedTab
        updateTab(tab) {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        fetchPage(tab = tab, page = 1)
    }

    private fun onTransactionClicked(transaction: TransactionUi) {
        setEffect {
            when (currentState.selectedTab) {
                TransactionsTab.PURCHASES -> TransactionsContract.Effect.Navigation.GoToOrderItemTrackingHistoryScreen(
                    orderId = transaction.orderId.value,
                    orderItemId = transaction.orderItemId.value,
                    orderItemTitle = transaction.title,
                    // Raw on purpose: the type-safe route encodes its own arguments.
                    orderItemCoverImageUrl = transaction.coverImageUrl,
                    orderItemPriceLabel = transaction.price.label,
                    sellerUserId = transaction.counterpartyUserId
                )

                TransactionsTab.SALES -> TransactionsContract.Effect.Navigation.GoToOrderItemManagementScreen(
                    orderId = transaction.orderId.value,
                    orderItemId = transaction.orderItemId.value
                )
            }
        }
    }

    // Helper functions.

    /**
     * Fetches [page] of [tab]. [TransactionsTabState.currentPage] only advances once the page
     * arrives, so a failed page is requested again on the next end-of-list scroll instead of being
     * skipped. Page `1` replaces the list (first load, refresh, retry).
     */
    private fun fetchPage(tab: TransactionsTab, page: Int) {
        fetchJobs[tab] = viewModelScope.launch {
            val result = when (tab) {
                TransactionsTab.PURCHASES -> orderInteractor.getPurchases(page)
                TransactionsTab.SALES -> orderInteractor.getSales(page)
            }

            when (result) {
                is InteractorPartialState.Success -> {
                    val newItems =
                        result.data.map { it.toUi(priceScale = configPriceLogic.priceScale) }

                    updateTab(tab) {
                        val existingItems = if (page == 1) persistentListOf() else items
                        val existingIds = existingItems.map { it.orderItemId }.toSet()
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            isFetchingNewItems = false,
                            hasError = false,
                            currentPage = page,
                            hasReachedEnd = newItems.isEmpty(),
                            items = existingItems.addAll(
                                newItems.filterNot { it.orderItemId in existingIds }
                                    .toPersistentList()
                            )
                        )
                    }
                }

                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    val hasItems = currentState.tabState(tab).items.isNotEmpty()
                    updateTab(tab) {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            isFetchingNewItems = false,
                            // With items on screen, keep them and report the failure in a snackbar.
                            hasError = !hasItems
                        )
                    }

                    if (hasItems) {
                        showErroneousSnackbar(
                            errorMessage = resourceProvider.getString(R.string.screen_transactions_error_loading_more)
                        )
                    }
                }
            }
        }
    }

    private fun updateTab(
        tab: TransactionsTab,
        reducer: TransactionsTabState.() -> TransactionsTabState
    ) {
        setState {
            when (tab) {
                TransactionsTab.PURCHASES -> copy(purchases = purchases.reducer())
                TransactionsTab.SALES -> copy(sales = sales.reducer())
            }
        }
    }
}
