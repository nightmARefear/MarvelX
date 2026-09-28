package com.alexgreece45.marvelx.domain.interactors.notification

import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.data.model.notification.NotificationItemData
import com.alexgreece45.marvelx.data.repository.NotificationRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationDomain
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationsPageDomain
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.Instant

class NotificationInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    private lateinit var interactor: NotificationInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        interactor = NotificationInteractorImpl(
            notificationRepository = notificationRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ getNotifications CASES                                                                    ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN notificationRepository#getNotifications is Success WHEN interactor getNotifications THEN returns the mapped page`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(1))
                .thenReturn(RepositoryResult.Success(listOf(TestConstants.orderStatusChangedData)))

            // WHEN
            val result = interactor.getNotifications(1)

            // THEN
            assertEquals(
                InteractorPartialState.Success(
                    NotificationsPageDomain(
                        items = listOf(TestConstants.orderStatusChangedDomain),
                        isLastPage = false
                    )
                ),
                result
            )
        }

    @Test
    fun `GIVEN a page with only unknown types WHEN interactor getNotifications THEN they are skipped and the list does not end`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(1))
                .thenReturn(RepositoryResult.Success(listOf(TestConstants.unknownTypeData)))

            // WHEN
            val result = interactor.getNotifications(1)

            // THEN
            assertEquals(
                InteractorPartialState.Success(NotificationsPageDomain(items = emptyList(), isLastPage = false)),
                result
            )
        }

    @Test
    fun `GIVEN an empty page WHEN interactor getNotifications THEN it is the last page`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(2)).thenReturn(RepositoryResult.Success(emptyList()))

            // WHEN
            val result = interactor.getNotifications(2)

            // THEN
            assertEquals(
                InteractorPartialState.Success(NotificationsPageDomain(items = emptyList(), isLastPage = true)),
                result
            )
        }

    @Test
    fun `GIVEN notificationRepository#getNotifications is Failed WHEN interactor getNotifications THEN returns Failed`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(1))
                .thenReturn(RepositoryResult.Failed(ErrorUtil.errorMessage))

            // WHEN
            val result = interactor.getNotifications(1)

            // THEN
            assertEquals(InteractorPartialState.Failed<NotificationsPageDomain>(ErrorUtil.errorMessage), result)
        }

    @Test
    fun `GIVEN notificationRepository#getNotifications is NetworkError WHEN interactor getNotifications THEN returns NoInternetConnection`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(1)).thenReturn(RepositoryResult.NetworkError)

            // WHEN
            val result = interactor.getNotifications(1)

            // THEN
            assertEquals(InteractorPartialState.NoInternetConnection, result)
        }

    @Test
    fun `GIVEN an invalid createdAt WHEN interactor getNotifications THEN returns Error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.getNotifications(1)).thenReturn(
                RepositoryResult.Success(listOf(TestConstants.orderStatusChangedData.copy(createdAt = "2026-09-28")))
            )

            // WHEN
            val result = interactor.getNotifications(1)

            // THEN
            assertEquals(true, result is InteractorPartialState.Error)
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ markAsRead CASES                                                                          ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN notificationRepository#markAsRead is true WHEN interactor markAsRead THEN returns true`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(notificationRepository.markAsRead(TestConstants.NOTIFICATION_ID)).thenReturn(true)

            // WHEN
            val result = interactor.markAsRead(TestConstants.NOTIFICATION_ID)

            // THEN
            assertEquals(true, result)
            verify(notificationRepository).markAsRead(TestConstants.NOTIFICATION_ID)
        }

    private object TestConstants {
        const val NOTIFICATION_ID = "7"
        private const val CREATED_AT = "2026-09-28T10:15:00Z"

        private val deepLinkAction = DeepLinkAction(
            screenRoute = "OrderDetailsRoute",
            data = mapOf("orderId" to "3")
        )

        val orderStatusChangedData = NotificationItemData(
            notificationId = NOTIFICATION_ID,
            channelType = "OrderStatusChanged",
            title = "Order #3 updated",
            body = "All your items have shipped.",
            imageUrl = null,
            createdAt = CREATED_AT,
            isRead = false,
            deepLinkAction = deepLinkAction
        )

        val orderStatusChangedDomain = NotificationDomain(
            notificationId = NOTIFICATION_ID,
            channelType = NotificationChannelType.OrderStatusChanged,
            title = "Order #3 updated",
            body = "All your items have shipped.",
            imageUrl = null,
            createdAt = Instant.parse(CREATED_AT),
            isRead = false,
            deepLinkAction = deepLinkAction
        )

        val unknownTypeData = orderStatusChangedData.copy(channelType = "SomeFutureType")
    }
}
