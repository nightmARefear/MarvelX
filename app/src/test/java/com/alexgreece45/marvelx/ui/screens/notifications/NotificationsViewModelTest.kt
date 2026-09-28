package com.alexgreece45.marvelx.ui.screens.notifications

import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.base.BaseViewModelTest
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.notification.NotificationInteractor
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationDomain
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationsPageDomain
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runFlowTest
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationsContract
import com.alexgreece45.marvelx.ui.screens.notifications.model.toUi
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import java.time.Instant

internal class NotificationsViewModelTest : BaseViewModelTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var notificationInteractor: NotificationInteractor

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var snackbarDelegate: SnackbarDelegate

    private val initialState = NotificationsContract.State()

    private lateinit var viewModel: NotificationsViewModel

    @Before
    override fun setUp() {
        MockitoAnnotations.openMocks(this)

        viewModel = NotificationsViewModel(
            notificationInteractor = notificationInteractor,
            resourceProvider = resourceProvider,
            snackbarDelegate = snackbarDelegate
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Init CASES                                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN notificationInteractor#getNotifications is Success WHEN Event#Init THEN first page is shown and badge is refreshed`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationInteractor.getNotifications(1))
                .thenReturn(InteractorPartialState.Success(TestConstants.page1))

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(loadedState(page = 1, domains = TestConstants.page1.items), viewModel.uiState.value)
            verify(notificationInteractor).refreshUnreadCount()
        }

    @Test
    fun `GIVEN notificationInteractor#getNotifications is the last page WHEN Event#Init THEN the end is reached`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationInteractor.getNotifications(1))
                .thenReturn(InteractorPartialState.Success(NotificationsPageDomain(items = emptyList(), isLastPage = true)))

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(isLoading = false, currentPage = 1, hasReachedEnd = true),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN notificationInteractor#getNotifications is Failed WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationInteractor.getNotifications(1))
                .thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(initialState.copy(isLoading = false, hasError = true), viewModel.uiState.value)
            verify(notificationInteractor, never()).refreshUnreadCount()
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnEndOfListScrolled CASES                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN page 2 is Success WHEN Event#OnEndOfListScrolled THEN page 2 is appended`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.getNotifications(2))
                .thenReturn(InteractorPartialState.Success(TestConstants.page2))

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            assertEquals(
                loadedState(page = 2, domains = TestConstants.page1.items + TestConstants.page2.items),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN page 2 is NoInternetConnection WHEN Event#OnEndOfListScrolled THEN the page does not advance and a snackbar is shown`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.getNotifications(2)).thenReturn(InteractorPartialState.NoInternetConnection)
            `when`(resourceProvider.getString(R.string.screen_notifications_error_loading_more))
                .thenReturn(TestConstants.ERROR_LOADING_MORE)

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnEndOfListScrolled)
            advanceUntilIdle()

            // THEN
            assertEquals(loadedState(page = 1, domains = TestConstants.page1.items), viewModel.uiState.value)
            verify(snackbarDelegate).showErroneousSnackbar(
                TestConstants.ERROR_LOADING_MORE,
                2,
                SnackbarDuration.Short,
                null
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnNotificationClicked CASES                                                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN an unread notification and markAsRead is true WHEN Event#OnNotificationClicked THEN it is read and emits Effect#Navigation#OpenDeepLinkAction`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.markAsRead(TestConstants.UNREAD_ID)).thenReturn(true)
            val notification = viewModel.uiState.value.notifications.first { it.notificationId == TestConstants.UNREAD_ID }

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnNotificationClicked(notification))
            advanceUntilIdle()

            // THEN
            assertEquals(
                loadedState(
                    page = 1,
                    domains = TestConstants.page1.items.map { it.copy(isRead = true) }
                ),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    NotificationsContract.Effect.Navigation.OpenDeepLinkAction(TestConstants.orderDetailsDeepLink),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN an unread notification and markAsRead is false WHEN Event#OnNotificationClicked THEN it is unread again and still opens`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.markAsRead(TestConstants.UNREAD_ID)).thenReturn(false)
            val notification = viewModel.uiState.value.notifications.first { it.notificationId == TestConstants.UNREAD_ID }

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnNotificationClicked(notification))
            advanceUntilIdle()

            // THEN
            assertEquals(loadedState(page = 1, domains = TestConstants.page1.items), viewModel.uiState.value)
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    NotificationsContract.Effect.Navigation.OpenDeepLinkAction(TestConstants.orderDetailsDeepLink),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN a read notification WHEN Event#OnNotificationClicked THEN notificationInteractor#markAsRead is not called`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            val notification = viewModel.uiState.value.notifications.first { it.notificationId == TestConstants.READ_ID }

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnNotificationClicked(notification))
            advanceUntilIdle()

            // THEN
            verify(notificationInteractor, never()).markAsRead(any())
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnMarkAllAsReadClicked CASES                                                              ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN markAllAsRead is true WHEN Event#OnMarkAllAsReadClicked THEN every notification is read`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.markAllAsRead()).thenReturn(true)

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnMarkAllAsReadClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                loadedState(page = 1, domains = TestConstants.page1.items.map { it.copy(isRead = true) }),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN markAllAsRead is false WHEN Event#OnMarkAllAsReadClicked THEN the unread ones are restored and a snackbar is shown`() =
        coroutineRule.runTest {
            // GIVEN
            givenFirstPageLoaded()
            `when`(notificationInteractor.markAllAsRead()).thenReturn(false)
            `when`(resourceProvider.getString(R.string.screen_notifications_error_marking_all_as_read))
                .thenReturn(TestConstants.ERROR_MARKING_ALL)

            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnMarkAllAsReadClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(loadedState(page = 1, domains = TestConstants.page1.items), viewModel.uiState.value)
            verify(snackbarDelegate).showErroneousSnackbar(
                TestConstants.ERROR_MARKING_ALL,
                2,
                SnackbarDuration.Short,
                null
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnBackClicked CASES                                                                       ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `WHEN Event#OnBackClicked THEN emits Effect#Navigation#GoBack`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(NotificationsContract.Event.OnBackClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(NotificationsContract.Effect.Navigation.GoBack, awaitItem())
            }
        }

    // Helpers.

    private suspend fun TestScope.givenFirstPageLoaded() {
        `when`(notificationInteractor.getNotifications(1))
            .thenReturn(InteractorPartialState.Success(TestConstants.page1))
        viewModel.setEvent(NotificationsContract.Event.Init)
        advanceUntilIdle()
    }

    private fun loadedState(page: Int, domains: List<NotificationDomain>): NotificationsContract.State =
        initialState.copy(
            isLoading = false,
            currentPage = page,
            notifications = domains.map { it.toUi() }.toPersistentList()
        )

    private object TestConstants {
        const val UNREAD_ID = "2"
        const val READ_ID = "1"
        const val ERROR_LOADING_MORE = "An error occurred whilst loading more notifications."
        const val ERROR_MARKING_ALL = "An error occurred whilst marking your notifications as read."

        val orderDetailsDeepLink = DeepLinkAction(
            screenRoute = "OrderDetailsRoute",
            data = mapOf("orderId" to "3")
        )

        private fun notification(id: String, isRead: Boolean) = NotificationDomain(
            notificationId = id,
            channelType = NotificationChannelType.OrderStatusChanged,
            title = "Order #3 updated",
            body = "Some of your items have shipped.",
            imageUrl = null,
            createdAt = Instant.parse("2026-09-28T10:15:00Z"),
            isRead = isRead,
            deepLinkAction = orderDetailsDeepLink
        )

        val page1 = NotificationsPageDomain(
            items = listOf(notification(id = UNREAD_ID, isRead = false), notification(id = READ_ID, isRead = true)),
            isLastPage = false
        )
        val page2 = NotificationsPageDomain(
            items = listOf(notification(id = "0", isRead = true)),
            isLastPage = false
        )
    }
}
