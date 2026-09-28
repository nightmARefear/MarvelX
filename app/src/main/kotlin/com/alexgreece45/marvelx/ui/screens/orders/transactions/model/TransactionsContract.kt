package com.alexgreece45.marvelx.ui.screens.orders.transactions.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal enum class TransactionsTab(@StringRes val titleResId: Int) {
    PURCHASES(titleResId = R.string.screen_transactions_tab_purchases),
    SALES(titleResId = R.string.screen_transactions_tab_sales)
}

/**
 * The paginated list of a single tab. Each tab loads independently; [SALES][TransactionsTab.SALES]
 * only loads the first time it's opened.
 *
 * @param currentPage The last page that arrived. `0` until the first page arrives.
 * @param hasReachedEnd `true` once a page came back empty, so no more pages are requested.
 * @param hasError `true` when the first page failed; later page failures show a snackbar instead.
 */
internal data class TransactionsTabState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isFetchingNewItems: Boolean = false,
    val hasError: Boolean = false,
    val currentPage: Int = 0,
    val hasReachedEnd: Boolean = false,
    val items: PersistentList<TransactionUi> = persistentListOf(),
) {
    val hasLoadedFirstPage: Boolean get() = currentPage > 0
}

internal interface TransactionsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data class OnTabClicked(val tab: TransactionsTab) : Event
        data object OnEndOfListScrolled : Event
        data object OnPullToRefresh : Event
        data object OnErrorRetryClicked : Event
        data class OnTransactionClicked(val transaction: TransactionUi) : Event
    }

    data class State(
        val selectedTab: TransactionsTab = TransactionsTab.PURCHASES,
        val purchases: TransactionsTabState = TransactionsTabState(),
        val sales: TransactionsTabState = TransactionsTabState(),
    ) : BaseState {
        fun tabState(tab: TransactionsTab): TransactionsTabState =
            when (tab) {
                TransactionsTab.PURCHASES -> purchases
                TransactionsTab.SALES -> sales
            }

        val selectedTabState: TransactionsTabState get() = tabState(selectedTab)
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation

            data class GoToOrderItemTrackingHistoryScreen(
                val orderId: String,
                val orderItemId: String,
                val orderItemTitle: String,
                val orderItemCoverImageUrl: String,
                val orderItemPriceLabel: String,
                val sellerUserId: String
            ) : Navigation

            data class GoToOrderItemManagementScreen(
                val orderId: String,
                val orderItemId: String
            ) : Navigation
        }
    }
}
