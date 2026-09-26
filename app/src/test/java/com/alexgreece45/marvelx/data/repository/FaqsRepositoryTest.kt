package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.tools.FaqsTest
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals

/**
 * Class responsible for unit testing all the methods in [FaqsRepository].
 *
 * The unit tests are grouped by _method_.
 */
class FaqsRepositoryTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var apiClient: ApiClient

    private lateinit var repository: FaqsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        repository = FaqsRepositoryImpl(
            apiClient = apiClient,
            ioDispatcher = coroutineRule.testDispatcher
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ getFaqs CASES                                                                             ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN getFaqs is Success WHEN repository getFaqs THEN returns Success`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(apiClient.getFaqs())
                .thenReturn(ApiResult.Success(body = FaqsTest.responseFaqs))

            // WHEN
            val result = repository.getFaqs()

            // THEN
            assertEquals(
                RepositoryResult.Success(data = FaqsTest.repositoryFaqs),
                result
            )

            verify(apiClient).getFaqs()
        }

    @Test
    fun `GIVEN getFaqs is Failed WHEN repository getFaqs THEN returns Failed with error message`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(apiClient.getFaqs())
                .thenReturn(ApiResult.Failed(failureMessage = ErrorUtil.errorMessage))

            // WHEN
            val result = repository.getFaqs()

            // THEN
            assertEquals(
                RepositoryResult.Failed(failureMessage = ErrorUtil.errorMessage),
                result
            )

            verify(apiClient).getFaqs()
        }

    @Test
    fun `GIVEN getFaqs is Error WHEN repository getFaqs THEN returns Failed with error message`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(apiClient.getFaqs())
                .thenReturn(ApiResult.Error(errorBody = ErrorUtil.errorMessage))

            // WHEN
            val result = repository.getFaqs()

            // THEN
            assertEquals(
                RepositoryResult.Failed(failureMessage = ErrorUtil.errorMessage),
                result
            )

            verify(apiClient).getFaqs()
        }

    @Test
    fun `GIVEN getFaqs is NetworkError WHEN repository getFaqs THEN returns NetworkError`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(apiClient.getFaqs())
                .thenReturn(ApiResult.NetworkError)

            // WHEN
            val result = repository.getFaqs()

            // THEN
            assertEquals(
                RepositoryResult.NetworkError,
                result
            )

            verify(apiClient).getFaqs()
        }

    @Test
    fun `GIVEN getFaqs throws WHEN repository getFaqs THEN returns Failed with error message`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(apiClient.getFaqs())
                .thenThrow(ErrorUtil.runtimeException)

            // WHEN
            val result = repository.getFaqs()

            // THEN
            assertEquals(
                RepositoryResult.Failed(failureMessage = ErrorUtil.errorMessage),
                result
            )
        }
}
