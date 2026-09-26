package com.alexgreece45.marvelx.ui.screens.settings.faq

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.base.BaseViewModelTest
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.FaqsRepository
import com.alexgreece45.marvelx.tools.FaqsTest
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runFlowTest
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import com.alexgreece45.marvelx.ui.screens.settings.faq.model.toFaqUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals

internal class FaqViewModelTest : BaseViewModelTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var faqsRepository: FaqsRepository

    private val initialState = FaqContract.State()
    private lateinit var viewModel: FaqViewModel

    @Before
    override fun setUp() {
        MockitoAnnotations.openMocks(this)

        viewModel = FaqViewModel(
            resourceProvider = resourceProvider,
            faqsRepository = faqsRepository
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Init CASES                                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN faqsRepository#getFaqs is Success WHEN Event#Init THEN state is updated`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(faqsRepository.getFaqs())
                .thenReturn(RepositoryResult.Success(FaqsTest.repositoryFaqs))

            // WHEN
            viewModel.setEvent(FaqContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    sections = FaqsTest.repositoryFaqs.map { it.toFaqUi() }
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN faqsRepository#getFaqs is NetworkError WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(faqsRepository.getFaqs()).thenReturn(RepositoryResult.NetworkError)
            `when`(resourceProvider.getNoInternetConnectionMessage()).thenReturn("No internet")
            `when`(resourceProvider.getString(R.string.generic_retry_button)).thenReturn("Retry")

            // WHEN
            viewModel.setEvent(FaqContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    error = FaqScreenErrorView(
                        errorSubtitle = "No internet",
                        primaryButtonLabel = "Retry",
                        event = FaqContract.Event.Init
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN faqsRepository#getFaqs is Failed WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(faqsRepository.getFaqs())
                .thenReturn(RepositoryResult.Failed(failureMessage = ErrorUtil.errorMessage))
            `when`(resourceProvider.getString(R.string.generic_unknown_error)).thenReturn("Unknown error")
            `when`(resourceProvider.getString(R.string.generic_retry_button)).thenReturn("Retry")

            // WHEN
            viewModel.setEvent(FaqContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    error = FaqScreenErrorView(
                        errorSubtitle = "Unknown error",
                        primaryButtonLabel = "Retry",
                        event = FaqContract.Event.Init
                    )
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnSectionClicked CASES                                                                    ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN faqs are loaded WHEN Event#OnSectionClicked THEN section is expanded`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(faqsRepository.getFaqs())
                .thenReturn(RepositoryResult.Success(FaqsTest.repositoryFaqs))
            viewModel.setEvent(FaqContract.Event.Init)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(FaqContract.Event.OnSectionClicked(id = "faq_1"))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    sections = FaqsTest.repositoryFaqs.map { it.toFaqUi() }.map {
                        if (it.id == "faq_1") it.copy(isExpanded = true) else it
                    }
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnBackClicked CASES                                                                       ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `WHEN Event#OnBackClicked THEN emits Effect#Navigation#GoBack`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(FaqContract.Event.OnBackClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    FaqContract.Effect.Navigation.GoBack,
                    awaitItem()
                )
            }
        }
}
