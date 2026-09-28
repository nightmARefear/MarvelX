package com.alexgreece45.marvelx.ui.screens.orders.transactions

import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.base.BaseViewModelTest
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.transaction.TransactionDomain
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runFlowTest
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsContract
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTab
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsTabState
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.time.Instant

internal class TransactionsViewModelTest : BaseViewModelTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var orderInteractor: OrderInteractor

    @Mock
    private lateinit var configPriceLogic: ConfigPriceLogic

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var snackbarDelegate: SnackbarDelegate

    private val initialState = TransactionsContract.State()

    private lateinit var viewModel: TransactionsViewModel

    @Before
    override fun setUp() {
        MockitoAnnotations.openMocks(this)

        `when`(configPriceLogic.priceScale).thenReturn(TestConstants.PRICE_SCALE)

        viewModel = TransactionsViewModel(
            orderInteractor = orderInteractor,
            configPriceLogic = configPriceLogic,
            resourceProvider = resourceProvider,
            snackbarDelegate = snackbarDelegate
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Init CASES                                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN orderInteractor#getPurchases is Success WHEN Event#Init THEN purchases first page is shown`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(orderInteractor.getPurchases(1))
                .thenReturn(InteractorPartialState.Success(TestConstants.purchasesPage1))

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(purchases = loadedTab(page = 1, domains = TestConstants.purchasesPage1)),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN orderInteractor#getPurchases is an empty Success WHEN Event#Init THEN purchases has reached the end`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(orderInteractor.getPurchases(1)).thenReturn(InteractorPartialState.Success(emptyList()))

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    purchases = TransactionsTabState(isLoading = false, currentPage = 1, hasReachedEnd = true)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN orderInteractor#getPurchases is Failed WHEN Event#Init THEN purchases has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(orderInteractor.getPurchases(1))
                .thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(purchases = TransactionsTabState(isLoading = false, hasError = true)),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN purchases already loaded WHEN Event#Init THEN orderInteractor#getPurchases is not called again`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            verify(orderInteractor, times(1)).getPurchases(1)
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnEndOfListScrolled CASES                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN page 2 is Success WHEN Event#OnEndOfListScrolled THEN page 2 is appended`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getPurchases(2))
                .thenReturn(InteractorPartialState.Success(TestConstants.purchasesPage2))

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    purchases = loadedTab(
                        page = 2,
                        domains = TestConstants.purchasesPage1 + TestConstants.purchasesPage2
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN page 2 is NetworkError WHEN Event#OnEndOfListScrolled THEN the page does not advance and a snackbar is shown`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getPurchases(2)).thenReturn(InteractorPartialState.NoInternetConnection)
            `when`(resourceProvider.getString(R.string.screen_transactions_error_loading_more))
                .thenReturn(TestConstants.ERROR_LOADING_MORE)

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(purchases = loadedTab(page = 1, domains = TestConstants.purchasesPage1)),
                viewModel.uiState.value
            )
            verify(snackbarDelegate).showErroneousSnackbar(
                TestConstants.ERROR_LOADING_MORE,
                2,
                SnackbarDuration.Short,
                null
            )
        }

    @Test
    fun `GIVEN page 2 failed WHEN Event#OnEndOfListScrolled again THEN page 2 is requested again`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getPurchases(2)).thenReturn(
                InteractorPartialState.NoInternetConnection,
                InteractorPartialState.Success(TestConstants.purchasesPage2)
            )
            `when`(resourceProvider.getString(R.string.screen_transactions_error_loading_more))
                .thenReturn(TestConstants.ERROR_LOADING_MORE)
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            verify(orderInteractor, times(2)).getPurchases(2)
            verify(orderInteractor, never()).getPurchases(3)
            assertEquals(
                initialState.copy(
                    purchases = loadedTab(
                        page = 2,
                        domains = TestConstants.purchasesPage1 + TestConstants.purchasesPage2
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN the end was reached WHEN Event#OnEndOfListScrolled THEN no further page is requested`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getPurchases(2)).thenReturn(InteractorPartialState.Success(emptyList()))
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            verify(orderInteractor, never()).getPurchases(3)
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnTabClicked CASES                                                                        ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN orderInteractor#getSales is Success WHEN Event#OnTabClicked Sales THEN sales first page is shown`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getSales(1))
                .thenReturn(InteractorPartialState.Success(TestConstants.salesPage1))

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnTabClicked(TransactionsTab.SALES))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    selectedTab = TransactionsTab.SALES,
                    purchases = loadedTab(page = 1, domains = TestConstants.purchasesPage1),
                    sales = loadedTab(page = 1, domains = TestConstants.salesPage1)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN sales already loaded WHEN Event#OnTabClicked Sales again THEN orderInteractor#getSales is not called again`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getSales(1))
                .thenReturn(InteractorPartialState.Success(TestConstants.salesPage1))
            viewModel.setEvent(TransactionsContract.Event.OnTabClicked(TransactionsTab.SALES))
            advanceUntilIdle()
            viewModel.setEvent(TransactionsContract.Event.OnTabClicked(TransactionsTab.PURCHASES))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnTabClicked(TransactionsTab.SALES))
            advanceUntilIdle()

            // THEN
            verify(orderInteractor, times(1)).getSales(1)
            verify(orderInteractor, times(1)).getPurchases(1)
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnPullToRefresh CASES                                                                     ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN two pages loaded WHEN Event#OnPullToRefresh THEN the list is replaced by the first page`() =
        coroutineRule.runTest {
            // GIVEN
            givenPurchasesFirstPageLoaded()
            `when`(orderInteractor.getPurchases(2))
                .thenReturn(InteractorPartialState.Success(TestConstants.purchasesPage2))
            viewModel.setEvent(TransactionsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnPullToRefresh)
            advanceUntilIdle()

            // THEN
            verify(orderInteractor, times(2)).getPurchases(1)
            assertEquals(
                initialState.copy(purchases = loadedTab(page = 1, domains = TestConstants.purchasesPage1)),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnErrorRetryClicked CASES                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN the first page failed WHEN Event#OnErrorRetryClicked THEN the first page is shown`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(orderInteractor.getPurchases(1)).thenReturn(
                InteractorPartialState.Failed(ErrorUtil.errorMessage),
                InteractorPartialState.Success(TestConstants.purchasesPage1)
            )
            viewModel.setEvent(TransactionsContract.Event.Init)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnErrorRetryClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(purchases = loadedTab(page = 1, domains = TestConstants.purchasesPage1)),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnTransactionClicked CASES                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN Purchases tab WHEN Event#OnTransactionClicked THEN emits Effect#Navigation#GoToOrderItemTrackingHistoryScreen`() =
        coroutineRule.runTest {
            // GIVEN
            val transaction = TestConstants.purchasesPage1.first().toUi(priceScale = TestConstants.PRICE_SCALE)

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnTransactionClicked(transaction))
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    TransactionsContract.Effect.Navigation.GoToOrderItemTrackingHistoryScreen(
                        orderId = transaction.orderId.value,
                        orderItemId = transaction.orderItemId.value,
                        orderItemTitle = transaction.title,
                        // Raw, not URL-encoded: the type-safe route encodes its own arguments.
                        orderItemCoverImageUrl = TestConstants.COVER_IMAGE_URL,
                        orderItemPriceLabel = transaction.price.label,
                        sellerUserId = TestConstants.COUNTERPARTY_USER_ID
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN Sales tab WHEN Event#OnTransactionClicked THEN emits Effect#Navigation#GoToOrderItemManagementScreen`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(orderInteractor.getSales(1))
                .thenReturn(InteractorPartialState.Success(TestConstants.salesPage1))
            viewModel.setEvent(TransactionsContract.Event.OnTabClicked(TransactionsTab.SALES))
            advanceUntilIdle()
            val transaction = TestConstants.salesPage1.first().toUi(priceScale = TestConstants.PRICE_SCALE)

            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnTransactionClicked(transaction))
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    TransactionsContract.Effect.Navigation.GoToOrderItemManagementScreen(
                        orderId = transaction.orderId.value,
                        orderItemId = transaction.orderItemId.value
                    ),
                    awaitItem()
                )
            }
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnBackClicked CASES                                                                       ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `WHEN Event#OnBackClicked THEN emits Effect#Navigation#GoBack`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(TransactionsContract.Event.OnBackClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(TransactionsContract.Effect.Navigation.GoBack, awaitItem())
            }
        }

    // Helpers.

    private suspend fun TestScope.givenPurchasesFirstPageLoaded() {
        `when`(orderInteractor.getPurchases(1))
            .thenReturn(InteractorPartialState.Success(TestConstants.purchasesPage1))
        viewModel.setEvent(TransactionsContract.Event.Init)
        advanceUntilIdle()
    }

    private fun loadedTab(page: Int, domains: List<TransactionDomain>): TransactionsTabState =
        TransactionsTabState(
            isLoading = false,
            currentPage = page,
            items = domains.map { it.toUi(priceScale = TestConstants.PRICE_SCALE) }.toPersistentList()
        )

    private object TestConstants {
        const val PRICE_SCALE = 2
        const val COVER_IMAGE_URL = "https://backend.example.com/api/private/marvelx/listing/42/cover"
        const val COUNTERPARTY_USER_ID = "2"
        const val ERROR_LOADING_MORE = "An error occurred whilst loading more transactions."

        private fun transaction(
            orderItemId: String,
            status: OrderItemStatusDomain = OrderItemStatusDomain.Shipped,
            returnStatus: OrderItemReturnStatusDomain? = null,
            refundAmount: BigDecimal? = null
        ) = TransactionDomain(
            orderId = OrderIdDomain("3"),
            orderItemId = OrderIdDomain(orderItemId),
            listingId = "1$orderItemId",
            title = "Listing $orderItemId",
            coverImageUrl = COVER_IMAGE_URL,
            price = BigDecimal("234.10"),
            date = Instant.parse("2026-09-13T10:15:00Z"),
            status = status,
            returnStatus = returnStatus,
            refundAmount = refundAmount,
            counterpartyUserId = COUNTERPARTY_USER_ID
        )

        val purchasesPage1 = listOf(
            transaction(orderItemId = "5"),
            transaction(
                orderItemId = "6",
                status = OrderItemStatusDomain.Delivered,
                returnStatus = OrderItemReturnStatusDomain.REFUNDED,
                refundAmount = BigDecimal("200.00")
            )
        )
        val purchasesPage2 = listOf(transaction(orderItemId = "7", status = OrderItemStatusDomain.Processing))
        val salesPage1 = listOf(transaction(orderItemId = "50", status = OrderItemStatusDomain.Processing))
    }
}
