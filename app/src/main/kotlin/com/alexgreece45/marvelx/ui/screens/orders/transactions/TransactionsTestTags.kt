package com.alexgreece45.marvelx.ui.screens.orders.transactions

import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTab

internal object TransactionsTestTags {
    // TransactionsContract.Event.OnBackClicked
    const val BACK_BUTTON = "transactions_back_button"

    // TransactionsContract.Event.OnTabClicked
    const val PURCHASES_TAB = "transactions_purchases_tab"
    const val SALES_TAB = "transactions_sales_tab"

    // TransactionsContract.Event.OnErrorRetryClicked
    const val ERROR_RETRY_BUTTON = "transactions_error_retry_button"

    // TransactionsContract.Event.OnTransactionClicked (suffixed with the order item id)
    const val TRANSACTION_ROW = "transactions_row"

    fun tab(tab: TransactionsTab): String = when (tab) {
        TransactionsTab.PURCHASES -> PURCHASES_TAB
        TransactionsTab.SALES -> SALES_TAB
    }

    fun transactionRow(orderItemId: String): String = "${TRANSACTION_ROW}_$orderItemId"
}
