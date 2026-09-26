package com.alexgreece45.marvelx.ui.screens.marketplace

import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.base.BaseViewModelTest
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.InputFieldValidator
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.common_ui.messageBanner.delegate.MessageBannerDelegate
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerAction
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerActionType
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerDuration
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toEpochMillis
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingDateRangeOption
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.cart.CartInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplacePromoSectionDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceSearchCriteriaDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runFlowTest
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.FilteredListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.MarketplaceUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.FilteredListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDatePostedCustomRangeUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDateRangeOptionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingPreviewUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceBottomSheetType
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceCategorySectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceComparisonUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceListingsLayout
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplacePromoSectionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceSearchCriteriaUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.SelectableOption
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi
import com.alexgreece45.marvelx.util.ResourceProvider
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import java.math.RoundingMode
import java.time.LocalDate

internal class MarketplaceViewModelTest : BaseViewModelTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var marketplaceInteractor: MarketplaceInteractor

    @Mock
    private lateinit var marketplaceUiMapper: MarketplaceUiMapper

    @Mock
    private lateinit var timerController: TimerController

    @Mock
    private lateinit var inputFieldValidator: InputFieldValidator

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var listingCategoryRepository: ListingCategoryRepository

    @Mock
    private lateinit var listingDetailsRepository: ListingDetailsRepository

    @Mock
    private lateinit var configPriceLogic: ConfigPriceLogic

    @Mock
    private lateinit var snackbarDelegate: SnackbarDelegate

    @Mock
    private lateinit var listingInteractor: ListingInteractor

    @Mock
    private lateinit var filteredListingUiMapper: FilteredListingUiMapper

    @Mock
    private lateinit var messageBannerDelegate: MessageBannerDelegate

    @Mock
    private lateinit var cartInteractor: CartInteractor

    private val initialState = MarketplaceContract.State()

    private lateinit var viewModel: MarketplaceViewModel

    @Before
    override fun setUp() {
        MockitoAnnotations.openMocks(this)

        viewModel = MarketplaceViewModel(
            marketplaceInteractor = marketplaceInteractor,
            marketplaceUiMapper = marketplaceUiMapper,
            timerController = timerController,
            inputFieldValidator = inputFieldValidator,
            resourceProvider = resourceProvider,
            listingCategoryRepository = listingCategoryRepository,
            listingDetailsRepository = listingDetailsRepository,
            configPriceLogic = configPriceLogic,
            bottomSheetSnackbarDelegate = snackbarDelegate,
            listingInteractor = listingInteractor,
            filteredListingUiMapper = filteredListingUiMapper,
            messageBannerDelegate = messageBannerDelegate,
            cartInteractor = cartInteractor
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Init CASES                                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is Success WHEN Event#Init THEN state is updated`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState(),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is Success WHEN Event#Init THEN starts promo carousel auto scroll`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            verify(timerController).startIndefiniteCountdown(
                delayMillis = TestConstants.PROMO_CAROUSEL_DELAY_MILLIS,
                startImmediately = false
            )
        }

    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is Success WHEN Event#Init THEN triggers listingInteractor#getFilteredListings`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            verify(listingInteractor).getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
        }

    @Test
    fun `GIVEN marketplaceUiMapper#toUi returns null WHEN Event#Init THEN state is not loading and has no error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.Success(TestConstants.marketplaceDomain))
            `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
                .thenReturn(null)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    hasError = false
                ),
                viewModel.uiState.value
            )
            verify(timerController, never()).startIndefiniteCountdown(
                delayMillis = any(),
                startImmediately = any()
            )
        }

    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is Failed WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    hasError = true
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is Error WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.Error(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    hasError = true
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN marketplaceInteractor#getMarketplaceInfo is NoInternetConnection WHEN Event#Init THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.NoInternetConnection)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    hasError = true
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN promo carousel delay has elapsed WHEN timerController ticks THEN promo carousel scrolls to next listing`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
                .thenReturn(TestConstants.marketplaceUiWithTwoPromoListings)
            `when`(
                timerController.startIndefiniteCountdown(
                    delayMillis = any(),
                    startImmediately = any()
                )
            ).thenReturn(flowOf(Unit))
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.ELAPSED_EPOCH_MILLIS

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().copy(
                    marketplaceUi = TestConstants.marketplaceUiWithTwoPromoListings.copy(
                        promo = TestConstants.marketplaceUiWithTwoPromoListings.promo?.copy(
                            selectedListingIndex = 1
                        )
                    ),
                    lastPromoSectionScrollMillis = TestConstants.ELAPSED_EPOCH_MILLIS
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN promo carousel is at last listing WHEN timerController ticks THEN promo carousel wraps to first listing`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(
                timerController.startIndefiniteCountdown(
                    delayMillis = any(),
                    startImmediately = any()
                )
            ).thenReturn(flowOf(Unit))
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.ELAPSED_EPOCH_MILLIS

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().copy(
                    lastPromoSectionScrollMillis = TestConstants.ELAPSED_EPOCH_MILLIS
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN promo carousel delay has not elapsed WHEN timerController ticks THEN promo carousel does not scroll`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
                .thenReturn(TestConstants.marketplaceUiWithTwoPromoListings)
            `when`(
                timerController.startIndefiniteCountdown(
                    delayMillis = any(),
                    startImmediately = any()
                )
            ).thenReturn(flowOf(Unit))
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.NOT_ELAPSED_EPOCH_MILLIS

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().copy(
                    marketplaceUi = TestConstants.marketplaceUiWithTwoPromoListings
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnBackClicked CASES                                                                       ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN mode is DISCOVER WHEN Event#OnBackClicked THEN state is not changed`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnBackClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN mode is SEARCH WHEN Event#OnBackClicked THEN search state is reset`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnSearchTermChanged(TestConstants.SEARCH_TERM))
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnLayoutChangeButtonClicked)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.SortByBottomSheet.OnSortSelected(ListingSort.PRICE_ASCENDING))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnBackClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.DISCOVER,
                    listingsLayout = MarketplaceListingsLayout.GRID,
                    searchUiState = UiState.Loading,
                    searchQuery = "",
                    activeSearchCriteria = MarketplaceSearchCriteriaUi(),
                    comparison = MarketplaceComparisonUi(),
                    activeSort = ListingSort.DATE_POSTED_DESCENDING
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnRefresh CASES                                                                           ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN mode is DISCOVER and marketplaceInteractor#getMarketplaceInfo is Success WHEN Event#OnRefresh THEN state is updated`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnRefresh)
            advanceUntilIdle()

            // THEN
            verify(marketplaceInteractor, times(2)).getMarketplaceInfo()
            assertEquals(
                expectedSuccessfulInitState(),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN mode is DISCOVER and marketplaceInteractor#getMarketplaceInfo is Failed WHEN Event#OnRefresh THEN state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnRefresh)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    hasError = true
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN mode is SEARCH WHEN Event#OnRefresh THEN triggers listingInteractor#getFilteredListings`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnRefresh)
            advanceUntilIdle()

            // THEN
            verify(listingInteractor, times(2)).getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
            verify(marketplaceInteractor, never()).getMarketplaceInfo()
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN a refresh is in progress WHEN Event#OnRefresh THEN refresh is not triggered again`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(marketplaceInteractor.getMarketplaceInfo())
                .thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))
            viewModel.setEvent(MarketplaceContract.Event.OnRefresh)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnRefresh)
            advanceUntilIdle()

            // THEN
            verify(marketplaceInteractor, times(1)).getMarketplaceInfo()
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnPromoCarouselScrolled CASES                                                             ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN marketplace is loaded WHEN Event#OnPromoCarouselScrolled THEN promo selected index is updated`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
                .thenReturn(TestConstants.marketplaceUiWithTwoPromoListings)
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.ELAPSED_EPOCH_MILLIS

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnPromoCarouselScrolled(newIndex = 1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().copy(
                    marketplaceUi = TestConstants.marketplaceUiWithTwoPromoListings.copy(
                        promo = TestConstants.marketplaceUiWithTwoPromoListings.promo?.copy(
                            selectedListingIndex = 1
                        )
                    ),
                    lastPromoSectionScrollMillis = TestConstants.ELAPSED_EPOCH_MILLIS
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN marketplace is not loaded WHEN Event#OnPromoCarouselScrolled THEN only last scroll millis is updated`() =
        coroutineRule.runTest {
            // GIVEN
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.ELAPSED_EPOCH_MILLIS

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnPromoCarouselScrolled(newIndex = 1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    lastPromoSectionScrollMillis = TestConstants.ELAPSED_EPOCH_MILLIS
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnCategoryClicked CASES                                                                   ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN listingInteractor#getFilteredListings is Success WHEN Event#OnCategoryClicked THEN state has search results`() =
        coroutineRule.runTest {
            // WHEN
            givenSearchResults()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchUiState = UiState.Success(TestConstants.filteredListings)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN marketplace is loaded WHEN Event#OnCategoryClicked THEN category is selected and searched`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().let { state ->
                    state.copy(
                        mode = MarketplaceMode.SEARCH,
                        activeSearchCriteria = state.activeSearchCriteria.copy(
                            categories = listOf(
                                SelectableOption(
                                    option = TestConstants.apparelCategory,
                                    isSelected = true
                                )
                            )
                        )
                    )
                },
                viewModel.uiState.value
            )
            verify(listingInteractor, atLeastOnce()).getFilteredListings(
                searchQuery = "",
                searchCriteria = MarketplaceSearchCriteriaDomain(
                    categoryCodes = listOf(TestConstants.apparelCategory.code),
                    conditionCodes = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    datePosted = ListingDateRangeOption.ALL,
                    datedPostedCustomRange = null,
                    finalSale = false
                ),
                sort = ListingSort.DATE_POSTED_DESCENDING
            )
        }

    @Test
    fun `GIVEN filteredListingUiMapper#toUiList returns null WHEN Event#OnCategoryClicked THEN search state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(InteractorPartialState.Success(emptyList()))
            `when`(filteredListingUiMapper.toUiList(any())).thenReturn(null)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchUiState = UiState.Error(Unit)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN listingInteractor#getFilteredListings is Failed WHEN Event#OnCategoryClicked THEN search state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(InteractorPartialState.Failed(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchUiState = UiState.Error(Unit)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN listingInteractor#getFilteredListings is Error WHEN Event#OnCategoryClicked THEN search state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(InteractorPartialState.Error(ErrorUtil.errorMessage))

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchUiState = UiState.Error(Unit)
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN listingInteractor#getFilteredListings is NoInternetConnection WHEN Event#OnCategoryClicked THEN search state has error`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(InteractorPartialState.NoInternetConnection)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchUiState = UiState.Error(Unit)
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Listing#OnClicked CASES                                                                   ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `WHEN Event#Listing#OnClicked THEN emits Effect#Navigation#GoToListingDetailsScreen`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen(TestConstants.LISTING_ID_1),
                    awaitItem()
                )
            }
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Listing#OnFavoriteClicked CASES                                                           ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN listing is not favorite and listingInteractor#addToFavorites is true WHEN Event#Listing#OnFavoriteClicked THEN listing is favorite`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(listingInteractor.addToFavorites(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_added_to_favorites_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(
                    listings = listOf(
                        TestConstants.filteredListing1.copy(
                            favorite = ToggleStateUi(isChecked = true, isLoading = false)
                        ),
                        TestConstants.filteredListing2
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN listingInteractor#addToFavorites is true WHEN message banner action is invoked THEN emits Effect#Navigation#GoToFavoritesScreen`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(listingInteractor.addToFavorites(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_added_to_favorites_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // WHEN
            invokeMessageBannerCustomAction(message = TestConstants.SUCCESS_MESSAGE)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToFavoritesScreen,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN listing is not favorite and listingInteractor#addToFavorites is false WHEN Event#Listing#OnFavoriteClicked THEN shows error message banner`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(listingInteractor.addToFavorites(TestConstants.LISTING_ID_1)).thenReturn(false)
            stubString(R.string.screen_marketplace_error_filtered_listing_add_to_favorites, TestConstants.ERROR_MESSAGE)
            stubString(R.string.generic_ok_button, TestConstants.OK_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.ERROR_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = MessageBannerAction.Button(
                    actionType = MessageBannerActionType.Hide,
                    label = TestConstants.OK_LABEL
                )
            )
        }

    @Test
    fun `GIVEN listing is favorite and listingInteractor#removeFromFavorites is true WHEN Event#Listing#OnFavoriteClicked THEN listing is not favorite`() =
        coroutineRule.runTest {
            // GIVEN
            val favoriteListing = TestConstants.filteredListing1.copy(favorite = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(favoriteListing, TestConstants.filteredListing2))
            `when`(listingInteractor.removeFromFavorites(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_removed_from_favorites_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(
                    listings = listOf(
                        favoriteListing.copy(
                            favorite = ToggleStateUi(isChecked = false, isLoading = false)
                        ),
                        TestConstants.filteredListing2
                    )
                ),
                viewModel.uiState.value
            )
            verify(listingInteractor, never()).addToFavorites(any())
        }

    @Test
    fun `GIVEN listingInteractor#removeFromFavorites is true WHEN message banner action is invoked THEN emits Effect#Navigation#GoToFavoritesScreen`() =
        coroutineRule.runTest {
            // GIVEN
            val favoriteListing = TestConstants.filteredListing1.copy(favorite = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(favoriteListing, TestConstants.filteredListing2))
            `when`(listingInteractor.removeFromFavorites(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_removed_from_favorites_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // WHEN
            invokeMessageBannerCustomAction(message = TestConstants.SUCCESS_MESSAGE)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToFavoritesScreen,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN listing is favorite and listingInteractor#removeFromFavorites is false WHEN Event#Listing#OnFavoriteClicked THEN shows error message banner`() =
        coroutineRule.runTest {
            // GIVEN
            val favoriteListing = TestConstants.filteredListing1.copy(favorite = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(favoriteListing, TestConstants.filteredListing2))
            `when`(listingInteractor.removeFromFavorites(TestConstants.LISTING_ID_1)).thenReturn(false)
            stubString(R.string.screen_marketplace_error_filtered_listing_remove_from_favorites, TestConstants.ERROR_MESSAGE)
            stubString(R.string.generic_ok_button, TestConstants.OK_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnFavoriteClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(listings = listOf(favoriteListing, TestConstants.filteredListing2)),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.ERROR_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = MessageBannerAction.Button(
                    actionType = MessageBannerActionType.Hide,
                    label = TestConstants.OK_LABEL
                )
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Listing#OnComparisonClicked CASES                                                         ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN no comparison WHEN Event#Listing#OnComparisonClicked THEN listing is added to comparison`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()

            // THEN
            val comparedListing = TestConstants.filteredListing1.copy(comparison = true)
            assertEquals(
                searchState(
                    listings = listOf(comparedListing, TestConstants.filteredListing2)
                ).copy(
                    comparison = MarketplaceComparisonUi(
                        listings = listOf(comparedListing),
                        comparisonCategory = TestConstants.apparelCategory
                    )
                ),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = eq(TestConstants.COMPARISON_ADDED_MESSAGE),
                messageBannerDuration = eq(MessageBannerDuration.Short),
                action = any()
            )
        }

    @Test
    fun `GIVEN listing is added to comparison WHEN message banner action is invoked THEN shows comparison bottom sheet`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()

            // WHEN
            invokeMessageBannerCustomAction(message = TestConstants.COMPARISON_ADDED_MESSAGE)
            advanceUntilIdle()

            // THEN
            assertEquals(
                MarketplaceBottomSheetType.COMPARISON,
                viewModel.uiState.value.bottomSheetType
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.ShowBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN listing is the only one in comparison WHEN Event#Listing#OnComparisonClicked THEN comparison is cleared`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            val event = MarketplaceContract.Event.Listing.OnComparisonClicked(
                listingId = TestConstants.LISTING_ID_1,
                category = TestConstants.apparelCategory
            )
            viewModel.setEvent(event)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(event)
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = eq(TestConstants.COMPARISON_REMOVED_MESSAGE),
                messageBannerDuration = eq(MessageBannerDuration.Short),
                action = any()
            )
        }

    @Test
    fun `GIVEN two listings in comparison WHEN Event#Listing#OnComparisonClicked on one THEN comparison category is kept`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            givenListingsInComparison()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()

            // THEN
            val comparedListing = TestConstants.filteredListing2.copy(comparison = true)
            assertEquals(
                searchState(
                    listings = listOf(TestConstants.filteredListing1, comparedListing)
                ).copy(
                    comparison = MarketplaceComparisonUi(
                        listings = listOf(comparedListing),
                        comparisonCategory = TestConstants.apparelCategory
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN comparison has different category WHEN Event#Listing#OnComparisonClicked THEN shows ineligible info snackbar`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults(listings = TestConstants.filteredListingsWithComic)
            stubComparisonStrings()
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()
            val stateBeforeEvent = viewModel.uiState.value

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_3,
                    category = TestConstants.comicsCategory
                )
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                stateBeforeEvent,
                viewModel.uiState.value
            )
            val snackbarActionCaptor = argumentCaptor<SnackbarActionData>()
            verify(snackbarDelegate).showInfoSnackbar(
                message = eq(TestConstants.COMPARISON_INELIGIBLE_MESSAGE),
                maxMessageLines = eq(2),
                duration = eq(SnackbarDuration.Short),
                snackbarActionData = snackbarActionCaptor.capture()
            )
            assertEquals(
                TestConstants.COMPARISON_INELIGIBLE_BUTTON,
                snackbarActionCaptor.firstValue.label
            )
        }

    @Test
    fun `GIVEN comparison has different category WHEN ineligible snackbar action is invoked THEN comparison is replaced`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults(listings = TestConstants.filteredListingsWithComic)
            stubComparisonStrings()
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_3,
                    category = TestConstants.comicsCategory
                )
            )
            advanceUntilIdle()
            val snackbarActionCaptor = argumentCaptor<SnackbarActionData>()
            verify(snackbarDelegate).showInfoSnackbar(
                message = eq(TestConstants.COMPARISON_INELIGIBLE_MESSAGE),
                maxMessageLines = eq(2),
                duration = eq(SnackbarDuration.Short),
                snackbarActionData = snackbarActionCaptor.capture()
            )

            // WHEN
            (snackbarActionCaptor.firstValue.actionType as SnackbarActionType.Custom).onAction()
            advanceUntilIdle()

            // THEN
            val comparedListing = TestConstants.comicListing.copy(comparison = true)
            assertEquals(
                searchState(
                    listings = listOf(
                        TestConstants.filteredListing1,
                        TestConstants.filteredListing2,
                        comparedListing
                    )
                ).copy(
                    comparison = MarketplaceComparisonUi(
                        listings = listOf(comparedListing),
                        comparisonCategory = TestConstants.comicsCategory,
                        currentCarouselPageIndex = 0
                    )
                ),
                viewModel.uiState.value
            )
            verify(snackbarDelegate).showInfoSnackbar(
                message = TestConstants.COMPARISON_REPLACED_MESSAGE,
                maxMessageLines = 5,
                duration = SnackbarDuration.Short,
                snackbarActionData = null
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Listing#OnCartClicked CASES                                                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN listing is not in cart and cartInteractor#addCartItem is true WHEN Event#Listing#OnCartClicked THEN listing is in cart`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(cartInteractor.addCartItem(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_added_to_cart_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(
                    listings = listOf(
                        TestConstants.filteredListing1.copy(
                            cart = ToggleStateUi(isChecked = true, isLoading = false)
                        ),
                        TestConstants.filteredListing2
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN cartInteractor#addCartItem is true WHEN message banner action is invoked THEN emits Effect#Navigation#GoToCartScreen`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(cartInteractor.addCartItem(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_added_to_cart_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // WHEN
            invokeMessageBannerCustomAction(message = TestConstants.SUCCESS_MESSAGE)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToCartScreen,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN listing is not in cart and cartInteractor#addCartItem is false WHEN Event#Listing#OnCartClicked THEN shows error message banner`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            `when`(cartInteractor.addCartItem(TestConstants.LISTING_ID_1)).thenReturn(false)
            stubString(R.string.screen_marketplace_error_filtered_listing_add_to_cart, TestConstants.ERROR_MESSAGE)
            stubString(R.string.generic_ok_button, TestConstants.OK_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.ERROR_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = MessageBannerAction.Button(
                    actionType = MessageBannerActionType.Hide,
                    label = TestConstants.OK_LABEL
                )
            )
        }

    @Test
    fun `GIVEN listing is in cart and cartInteractor#deleteCartItem is true WHEN Event#Listing#OnCartClicked THEN listing is not in cart`() =
        coroutineRule.runTest {
            // GIVEN
            val cartListing = TestConstants.filteredListing1.copy(cart = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(cartListing, TestConstants.filteredListing2))
            `when`(cartInteractor.deleteCartItem(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_removed_from_cart_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(
                    listings = listOf(
                        cartListing.copy(
                            cart = ToggleStateUi(isChecked = false, isLoading = false)
                        ),
                        TestConstants.filteredListing2
                    )
                ),
                viewModel.uiState.value
            )
            verify(cartInteractor, never()).addCartItem(any())
        }

    @Test
    fun `GIVEN cartInteractor#deleteCartItem is true WHEN message banner action is invoked THEN emits Effect#Navigation#GoToCartScreen`() =
        coroutineRule.runTest {
            // GIVEN
            val cartListing = TestConstants.filteredListing1.copy(cart = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(cartListing, TestConstants.filteredListing2))
            `when`(cartInteractor.deleteCartItem(TestConstants.LISTING_ID_1)).thenReturn(true)
            stubString(R.string.screen_marketplace_filtered_listing_removed_from_cart_success, TestConstants.SUCCESS_MESSAGE)
            stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // WHEN
            invokeMessageBannerCustomAction(message = TestConstants.SUCCESS_MESSAGE)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToCartScreen,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN listing is in cart and cartInteractor#deleteCartItem is false WHEN Event#Listing#OnCartClicked THEN shows error message banner`() =
        coroutineRule.runTest {
            // GIVEN
            val cartListing = TestConstants.filteredListing1.copy(cart = ToggleStateUi(isChecked = true))
            givenSearchResults(listings = listOf(cartListing, TestConstants.filteredListing2))
            `when`(cartInteractor.deleteCartItem(TestConstants.LISTING_ID_1)).thenReturn(false)
            stubString(R.string.screen_marketplace_error_filtered_listing_remove_from_cart, TestConstants.ERROR_MESSAGE)
            stubString(R.string.generic_ok_button, TestConstants.OK_LABEL)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Listing.OnCartClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(listings = listOf(cartListing, TestConstants.filteredListing2)),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.ERROR_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = MessageBannerAction.Button(
                    actionType = MessageBannerActionType.Hide,
                    label = TestConstants.OK_LABEL
                )
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ SearchBar CASES                                                                           ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN promo carousel is scrolled WHEN Event#SearchBar#OnClicked THEN mode is SEARCH and promo index is reset`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
                .thenReturn(TestConstants.marketplaceUiWithTwoPromoListings)
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            mockkObject(DateUtil)
            every { DateUtil.getEpochMillis() } returns TestConstants.ELAPSED_EPOCH_MILLIS
            viewModel.setEvent(MarketplaceContract.Event.OnPromoCarouselScrolled(newIndex = 1))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().copy(
                    marketplaceUi = TestConstants.marketplaceUiWithTwoPromoListings,
                    lastPromoSectionScrollMillis = TestConstants.ELAPSED_EPOCH_MILLIS,
                    mode = MarketplaceMode.SEARCH
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `WHEN Event#SearchBar#OnClicked THEN emits Effect#RequestSearchBarFieldFocus and triggers listingInteractor#getFilteredListings`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(InteractorPartialState.Success(emptyList()))
            `when`(filteredListingUiMapper.toUiList(any())).thenReturn(TestConstants.filteredListings)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
            verify(listingInteractor).getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.RequestSearchBarFieldFocus,
                    awaitItem()
                )
            }
        }

    @Test
    fun `WHEN Event#SearchBar#OnSearchTermChanged THEN search query is updated and mode is SEARCH`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnSearchTermChanged(TestConstants.SEARCH_TERM))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchQuery = TestConstants.SEARCH_TERM
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN search query is not empty WHEN Event#SearchBar#OnDeleteIconClicked THEN search query is cleared`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnSearchTermChanged(TestConstants.SEARCH_TERM))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SearchBar.OnDeleteIconClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    mode = MarketplaceMode.SEARCH,
                    searchQuery = ""
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Bottom sheet chips CASES                                                                  ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `WHEN Event#OnComparisonStatusContainerButtonClicked THEN shows comparison bottom sheet`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(bottomSheetType = MarketplaceBottomSheetType.COMPARISON),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.ShowBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `WHEN Event#OnSortChipClicked THEN shows sort bottom sheet`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnSortChipClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(bottomSheetType = MarketplaceBottomSheetType.SORT),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.ShowBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `WHEN Event#OnFiltersChipClicked THEN shows filter menu bottom sheet with draft search criteria`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    bottomSheetType = MarketplaceBottomSheetType.FILTER_MENU,
                    draftSearchCriteria = initialState.activeSearchCriteria
                ),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.ShowBottomSheet,
                    awaitItem()
                )
            }
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnLayoutChangeButtonClicked CASES                                                         ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN layout is GRID WHEN Event#OnLayoutChangeButtonClicked THEN layout is LIST`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnLayoutChangeButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(listingsLayout = MarketplaceListingsLayout.LIST),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN layout is LIST WHEN Event#OnLayoutChangeButtonClicked THEN layout is GRID`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnLayoutChangeButtonClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnLayoutChangeButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(listingsLayout = MarketplaceListingsLayout.GRID),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnActiveCategoryDeleteIconClicked CASES                                                   ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN category is active WHEN Event#OnActiveCategoryDeleteIconClicked THEN category is deselected and shows message banner`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            stubString(
                R.string.screen_marketplace_message_category_filter_removed,
                TestConstants.CATEGORY_REMOVED_MESSAGE,
                TestConstants.apparelCategory.label
            )
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnActiveCategoryDeleteIconClicked(TestConstants.apparelCategory))
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().activeSearchCriteria,
                viewModel.uiState.value.activeSearchCriteria
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.CATEGORY_REMOVED_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = null
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ SortByBottomSheet CASES                                                                   ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN sort bottom sheet is shown WHEN Event#SortByBottomSheet#OnCloseButtonClicked THEN emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnSortChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SortByBottomSheet.OnCloseButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN sort bottom sheet is shown WHEN Event#SortByBottomSheet#OnSortSelected THEN sort is updated and emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnSortChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.SortByBottomSheet.OnSortSelected(ListingSort.PRICE_ASCENDING))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(activeSort = ListingSort.PRICE_ASCENDING),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FilterMenu CASES                                                                          ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN filter menu is shown WHEN Event#FilterMenu#OnCloseButtonClicked THEN draft is discarded and emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.FilterMenu.OnCloseButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN draft has selections WHEN Event#FilterMenu#OnNegativeButtonClicked THEN draft is reset and emits Effect#ShowToastMessage`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            stubString(R.string.screen_marketplace_filter_menu_negative_button_action_message, TestConstants.FILTERS_RESET_MESSAGE)
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnCategorySelected(TestConstants.apparelCategory.code))
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnConditionSelected(TestConstants.listingCondition.code))
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnFinalSaleChecked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.FilterMenu.OnNegativeButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().activeSearchCriteria,
                viewModel.uiState.value.draftSearchCriteria
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.ShowToastMessage(TestConstants.FILTERS_RESET_MESSAGE),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN draft search criteria exists WHEN Event#FilterMenu#OnPositiveButtonClicked THEN draft is applied and emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnFinalSaleChecked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.FilterMenu.OnPositiveButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    activeSearchCriteria = initialState.activeSearchCriteria.copy(finalSale = true),
                    draftSearchCriteria = null,
                    bottomSheetType = null
                ),
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN draft search criteria is null WHEN Event#FilterMenu#OnPositiveButtonClicked THEN only emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.FilterMenu.OnPositiveButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Filter CASES                                                                              ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#OnCategorySelected THEN draft category is toggled`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnCategorySelected(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            val activeSearchCriteria = expectedSuccessfulInitState().activeSearchCriteria
            assertEquals(
                activeSearchCriteria.copy(
                    categories = listOf(
                        SelectableOption(
                            option = TestConstants.apparelCategory,
                            isSelected = true
                        )
                    )
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
            assertEquals(
                activeSearchCriteria,
                viewModel.uiState.value.activeSearchCriteria
            )
        }

    @Test
    fun `GIVEN draft search criteria is null WHEN Event#Filter#OnCategorySelected THEN state is not changed`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnCategorySelected(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#OnConditionSelected THEN draft condition is toggled`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnConditionSelected(TestConstants.listingCondition.code))
            advanceUntilIdle()

            // THEN
            assertEquals(
                expectedSuccessfulInitState().activeSearchCriteria.copy(
                    conditions = listOf(
                        SelectableOption(
                            option = TestConstants.listingCondition,
                            isSelected = true
                        )
                    )
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#OnPriceRangeMinChanged THEN draft min price is sanitized and validated`() =
        coroutineRule.runTest {
            // GIVEN
            stubPriceValidation()
            `when`(
                inputFieldValidator.validateInputSerially(
                    value = any(),
                    rules = any()
                )
            ).thenReturn(InputFieldValidationResult.Success)
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnPriceRangeMinChanged(TestConstants.UNSANITIZED_PRICE))
            advanceUntilIdle()

            // THEN
            val searchCriteria = initialState.activeSearchCriteria
            assertEquals(
                searchCriteria.copy(
                    priceRangeMin = searchCriteria.priceRangeMin.copy(
                        value = TestConstants.SANITIZED_PRICE,
                        validationResult = InputFieldValidationResult.Success
                    ),
                    priceRangeMax = searchCriteria.priceRangeMax.copy(
                        validationResult = InputFieldValidationResult.Success
                    )
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
            verify(inputFieldValidator).validateInputSerially(
                value = TestConstants.SANITIZED_PRICE,
                rules = listOf(
                    InputFieldRule.ValidateBigDecimal(errorMessage = TestConstants.INVALID_PRICE_MESSAGE),
                    InputFieldRule.ValidateOverMinimumBigDecimalValue(
                        minValue = TestConstants.minimumPrice,
                        errorMessage = TestConstants.BELOW_MIN_PRICE_MESSAGE
                    ),
                    InputFieldRule.ValidateUnderMaximumBigDecimalValue(
                        maxValue = TestConstants.maximumPrice,
                        errorMessage = TestConstants.OVER_MAX_PRICE_MESSAGE
                    )
                )
            )
            verify(inputFieldValidator).validateInputSerially(
                value = "",
                rules = emptyList()
            )
        }

    @Test
    fun `GIVEN inputFieldValidator#validateInputSerially is Error WHEN Event#Filter#OnPriceRangeMinChanged THEN draft min price has error`() =
        coroutineRule.runTest {
            // GIVEN
            stubPriceValidation()
            `when`(
                inputFieldValidator.validateInputSerially(
                    value = any(),
                    rules = any()
                )
            ).thenReturn(InputFieldValidationResult.Uninitialized)
            `when`(
                inputFieldValidator.validateInputSerially(
                    value = eq(TestConstants.SANITIZED_PRICE),
                    rules = any()
                )
            ).thenReturn(InputFieldValidationResult.Error(TestConstants.BELOW_MIN_PRICE_MESSAGE))
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnPriceRangeMinChanged(TestConstants.SANITIZED_PRICE))
            advanceUntilIdle()

            // THEN
            val searchCriteria = initialState.activeSearchCriteria
            assertEquals(
                searchCriteria.copy(
                    priceRangeMin = searchCriteria.priceRangeMin.copy(
                        value = TestConstants.SANITIZED_PRICE,
                        validationResult = InputFieldValidationResult.Error(TestConstants.BELOW_MIN_PRICE_MESSAGE)
                    )
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#OnPriceRangeMaxChanged THEN draft max price is sanitized and validated`() =
        coroutineRule.runTest {
            // GIVEN
            stubPriceValidation()
            `when`(
                inputFieldValidator.validateInputSerially(
                    value = any(),
                    rules = any()
                )
            ).thenReturn(InputFieldValidationResult.Success)
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnPriceRangeMaxChanged(TestConstants.UNSANITIZED_PRICE))
            advanceUntilIdle()

            // THEN
            val searchCriteria = initialState.activeSearchCriteria
            assertEquals(
                searchCriteria.copy(
                    priceRangeMin = searchCriteria.priceRangeMin.copy(
                        validationResult = InputFieldValidationResult.Success
                    ),
                    priceRangeMax = searchCriteria.priceRangeMax.copy(
                        value = TestConstants.SANITIZED_PRICE,
                        validationResult = InputFieldValidationResult.Success
                    )
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
            verify(inputFieldValidator).validateInputSerially(
                value = TestConstants.SANITIZED_PRICE,
                rules = listOf(
                    InputFieldRule.ValidateBigDecimal(errorMessage = TestConstants.INVALID_PRICE_MESSAGE),
                    InputFieldRule.ValidateUnderMaximumBigDecimalValue(
                        maxValue = TestConstants.maximumPrice,
                        errorMessage = TestConstants.OVER_MAX_PRICE_MESSAGE
                    ),
                    InputFieldRule.ValidateOverMinimumBigDecimalValue(
                        minValue = TestConstants.minimumPrice,
                        errorMessage = TestConstants.BELOW_MIN_PRICE_MESSAGE
                    )
                )
            )
            verify(inputFieldValidator).validateInputSerially(
                value = "",
                rules = emptyList()
            )
        }

    @Test
    fun `GIVEN draft search criteria is null WHEN Event#Filter#OnPriceRangeMaxChanged THEN state is not changed`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnPriceRangeMaxChanged(TestConstants.SANITIZED_PRICE))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
            verifyNoInteractions(inputFieldValidator)
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#DatePosted#OnDateRangeOptionSelected THEN draft date posted is updated`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected(ListingDateRangeOptionUi.Last7Days)
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.activeSearchCriteria.copy(
                    datePosted = ListingDateRangeOptionUi.Last7Days,
                    datedPostedCustomRange = null
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN custom range is selected WHEN Event#Filter#DatePosted#OnDateRangeOptionSelected with Custom THEN custom range is kept`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected(
                    minDateMillis = TestConstants.customRangeFrom.toEpochMillis(),
                    maxDateMillis = TestConstants.customRangeTo.toEpochMillis()
                )
            )
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected(ListingDateRangeOptionUi.Custom)
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.activeSearchCriteria.copy(
                    datePosted = ListingDateRangeOptionUi.Custom,
                    datedPostedCustomRange = TestConstants.customRangeUi
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN custom range is selected WHEN Event#Filter#DatePosted#OnDateRangeOptionSelected with non Custom THEN custom range is cleared`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected(
                    minDateMillis = TestConstants.customRangeFrom.toEpochMillis(),
                    maxDateMillis = TestConstants.customRangeTo.toEpochMillis()
                )
            )
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected(ListingDateRangeOptionUi.Last30Days)
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.activeSearchCriteria.copy(
                    datePosted = ListingDateRangeOptionUi.Last30Days,
                    datedPostedCustomRange = null
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#DatePosted#OnRangeSelected THEN draft custom range is updated`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected(
                    minDateMillis = TestConstants.customRangeFrom.toEpochMillis(),
                    maxDateMillis = TestConstants.customRangeTo.toEpochMillis()
                )
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.activeSearchCriteria.copy(
                    datedPostedCustomRange = TestConstants.customRangeUi
                ),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    @Test
    fun `GIVEN no custom range WHEN Event#Filter#DatePosted#OnDatePostedCustomRangeClicked THEN emits Effect#ShowDateRangePicker without selection`() =
        coroutineRule.runTest {
            // GIVEN
            stubDateRangePicker()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.DatePosted.OnDatePostedCustomRangeClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    expectedShowDateRangePickerEffect(selectedDateRange = null),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN custom range is selected WHEN Event#Filter#DatePosted#OnDatePostedCustomRangeClicked THEN emits Effect#ShowDateRangePicker with selection`() =
        coroutineRule.runTest {
            // GIVEN
            stubDateRangePicker()
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()
            viewModel.setEvent(
                MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected(
                    minDateMillis = TestConstants.customRangeFrom.toEpochMillis(),
                    maxDateMillis = TestConstants.customRangeTo.toEpochMillis()
                )
            )
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.DatePosted.OnDatePostedCustomRangeClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    expectedShowDateRangePickerEffect(
                        selectedDateRange = Pair(
                            first = TestConstants.customRangeFrom.toEpochMillis(),
                            second = TestConstants.customRangeTo.toEpochMillis()
                        )
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN filter menu is shown WHEN Event#Filter#OnFinalSaleChecked THEN draft final sale is toggled`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnFiltersChipClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.Filter.OnFinalSaleChecked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.activeSearchCriteria.copy(finalSale = true),
                viewModel.uiState.value.draftSearchCriteria
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ ComparisonBottomSheet CASES                                                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN comparison bottom sheet is shown WHEN Event#ComparisonBottomSheet#OnCloseButtonClicked THEN emits Effect#HideBottomSheet`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnCloseButtonClicked)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN comparison is empty WHEN Event#ComparisonBottomSheet#OnShareButtonClicked THEN shows error message banner`() =
        coroutineRule.runTest {
            // GIVEN
            stubString(R.string.screen_marketplace_error_sharing_comparison_listings, TestConstants.ERROR_MESSAGE)

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnShareButtonClicked)
            advanceUntilIdle()

            // THEN
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.ERROR_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = null
            )
        }

    @Test
    fun `GIVEN comparison has listings WHEN Event#ComparisonBottomSheet#OnShareButtonClicked THEN emits Effect#Share#Comparison`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            givenListingsInComparison()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnShareButtonClicked)
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.Share.Comparison(
                        listingIds = listOf(TestConstants.LISTING_ID_1, TestConstants.LISTING_ID_2)
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `WHEN Event#ComparisonBottomSheet#OnListingClicked THEN emits Effect#HideBottomSheet and Effect#Navigation#GoToListingDetailsScreen`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnListingClicked(TestConstants.LISTING_ID_1))
            advanceUntilIdle()

            // THEN
            viewModel.uiEffect.runFlowTest {
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
                assertEquals(
                    MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen(TestConstants.LISTING_ID_1),
                    awaitItem()
                )
            }
        }

    @Test
    fun `GIVEN two listings in comparison WHEN Event#ComparisonBottomSheet#OnListingDeleteButtonClicked THEN listing is removed and bottom sheet stays`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            givenListingsInComparison()
            viewModel.setEvent(MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked(TestConstants.LISTING_ID_1)
            )
            advanceUntilIdle()

            // THEN
            val comparedListing = TestConstants.filteredListing2.copy(comparison = true)
            assertEquals(
                searchState(
                    listings = listOf(TestConstants.filteredListing1, comparedListing)
                ).copy(
                    comparison = MarketplaceComparisonUi(
                        listings = listOf(comparedListing),
                        comparisonCategory = TestConstants.apparelCategory
                    ),
                    bottomSheetType = MarketplaceBottomSheetType.COMPARISON
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN one listing in comparison WHEN Event#ComparisonBottomSheet#OnListingDeleteButtonClicked THEN comparison is cleared and bottom sheet is hidden`() =
        coroutineRule.runTest {
            // GIVEN
            givenSearchResults()
            stubComparisonStrings()
            stubString(R.string.screen_marketplace_listings_for_comparison_last_removed, TestConstants.LAST_COMPARISON_REMOVED_MESSAGE)
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = TestConstants.LISTING_ID_1,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(
                MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked(TestConstants.LISTING_ID_1)
            )
            advanceUntilIdle()

            // THEN
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
            verify(messageBannerDelegate).showMessageBanner(
                message = TestConstants.LAST_COMPARISON_REMOVED_MESSAGE,
                messageBannerDuration = MessageBannerDuration.Short,
                action = null
            )
            viewModel.uiEffect.runFlowTest {
                skipItems(1)
                assertEquals(
                    MarketplaceContract.Effect.HideBottomSheet,
                    awaitItem()
                )
            }
        }

    @Test
    fun `WHEN Event#ComparisonBottomSheet#OnScrolled THEN carousel page index is updated`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnScrolled(page = 2))
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState.copy(
                    comparison = MarketplaceComparisonUi(currentCarouselPageIndex = 2)
                ),
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnBottomSheetDismissed CASES                                                              ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN bottom sheet is shown and carousel is scrolled WHEN Event#OnBottomSheetDismissed THEN bottom sheet and carousel index are reset`() =
        coroutineRule.runTest {
            // GIVEN
            viewModel.setEvent(MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked)
            advanceUntilIdle()
            viewModel.setEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnScrolled(page = 2))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnBottomSheetDismissed)
            advanceUntilIdle()

            // THEN
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ OnErrorButtonClicked CASES                                                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN marketplace has error WHEN Event#OnErrorButtonClicked THEN retries marketplaceInteractor#getMarketplaceInfo`() =
        coroutineRule.runTest {
            // GIVEN
            stubSuccessfulMarketplaceInfo()
            `when`(marketplaceInteractor.getMarketplaceInfo()).thenReturn(
                InteractorPartialState.Failed(ErrorUtil.errorMessage),
                InteractorPartialState.Success(TestConstants.marketplaceDomain)
            )
            viewModel.setEvent(MarketplaceContract.Event.Init)
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnErrorButtonClicked)
            advanceUntilIdle()

            // THEN
            verify(marketplaceInteractor, times(2)).getMarketplaceInfo()
            assertEquals(
                expectedSuccessfulInitState(),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN search has error WHEN Event#OnErrorButtonClicked THEN retries listingInteractor#getFilteredListings`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                listingInteractor.getFilteredListings(
                    searchQuery = any(),
                    searchCriteria = any(),
                    sort = any()
                )
            ).thenReturn(
                InteractorPartialState.Failed(ErrorUtil.errorMessage),
                InteractorPartialState.Success(emptyList())
            )
            `when`(filteredListingUiMapper.toUiList(any())).thenReturn(TestConstants.filteredListings)
            viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
            advanceUntilIdle()

            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnErrorButtonClicked)
            advanceUntilIdle()

            // THEN
            verify(listingInteractor, times(2)).getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
            verify(marketplaceInteractor, never()).getMarketplaceInfo()
            assertEquals(
                searchState(),
                viewModel.uiState.value
            )
        }

    @Test
    fun `GIVEN no error WHEN Event#OnErrorButtonClicked THEN nothing is retried`() =
        coroutineRule.runTest {
            // WHEN
            viewModel.setEvent(MarketplaceContract.Event.OnErrorButtonClicked)
            advanceUntilIdle()

            // THEN
            verifyNoInteractions(marketplaceInteractor, listingInteractor)
            assertEquals(
                initialState,
                viewModel.uiState.value
            )
        }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ Helpers                                                                                   ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝

    /**
     * Stubs every collaborator reached by a successful `Event#Init`: the marketplace info, the
     * search criteria sources, the promo carousel timer and the initial filtered listings search
     * that the search criteria observer triggers.
     */
    private suspend fun stubSuccessfulMarketplaceInfo() {
        `when`(marketplaceInteractor.getMarketplaceInfo())
            .thenReturn(InteractorPartialState.Success(TestConstants.marketplaceDomain))
        `when`(marketplaceUiMapper.toUi(TestConstants.marketplaceDomain))
            .thenReturn(TestConstants.marketplaceUi)
        `when`(listingCategoryRepository.listingCategories)
            .thenReturn(listOf(TestConstants.listingCategory))
        `when`(listingDetailsRepository.listingConditions)
            .thenReturn(listOf(TestConstants.listingCondition))
        `when`(configPriceLogic.priceScale).thenReturn(TestConstants.PRICE_SCALE)
        `when`(configPriceLogic.roundingMode).thenReturn(TestConstants.roundingMode)
        `when`(
            timerController.startIndefiniteCountdown(
                delayMillis = any(),
                startImmediately = any()
            )
        ).thenReturn(emptyFlow())
        `when`(
            listingInteractor.getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
        ).thenReturn(InteractorPartialState.Success(emptyList()))
        `when`(filteredListingUiMapper.toUiList(emptyList())).thenReturn(emptyList())
    }

    /**
     * The state after a successful `Event#Init` with [stubSuccessfulMarketplaceInfo].
     */
    private fun expectedSuccessfulInitState(): MarketplaceContract.State =
        initialState.copy(
            isLoading = false,
            hasError = false,
            marketplaceUi = TestConstants.marketplaceUi,
            searchUiState = UiState.Success(emptyList()),
            activeSearchCriteria = initialState.activeSearchCriteria.copy(
                categories = listOf(
                    SelectableOption(
                        option = EnumGeneric(
                            code = TestConstants.listingCategory.code,
                            label = TestConstants.listingCategory.label
                        ),
                        isSelected = false
                    )
                ),
                conditions = listOf(
                    SelectableOption(
                        option = TestConstants.listingCondition,
                        isSelected = false
                    )
                ),
                priceRangeMin = initialState.activeSearchCriteria.priceRangeMin.copy(
                    priceScale = TestConstants.PRICE_SCALE,
                    roundingMode = TestConstants.roundingMode
                ),
                priceRangeMax = initialState.activeSearchCriteria.priceRangeMax.copy(
                    priceScale = TestConstants.PRICE_SCALE,
                    roundingMode = TestConstants.roundingMode
                )
            )
        )

    /**
     * Switches the marketplace into [MarketplaceMode.SEARCH] with [listings] as the successful
     * search results, without starting the search criteria observer.
     */
    private suspend fun TestScope.givenSearchResults(
        listings: List<FilteredListingUi> = TestConstants.filteredListings
    ) {
        `when`(
            listingInteractor.getFilteredListings(
                searchQuery = any(),
                searchCriteria = any(),
                sort = any()
            )
        ).thenReturn(InteractorPartialState.Success(emptyList()))
        `when`(filteredListingUiMapper.toUiList(any())).thenReturn(listings)
        viewModel.setEvent(MarketplaceContract.Event.OnCategoryClicked(TestConstants.apparelCategory.code))
        advanceUntilIdle()
    }

    /**
     * The state after [givenSearchResults] with [listings].
     */
    private fun searchState(
        listings: List<FilteredListingUi> = TestConstants.filteredListings
    ): MarketplaceContract.State =
        initialState.copy(
            mode = MarketplaceMode.SEARCH,
            searchUiState = UiState.Success(listings)
        )

    /**
     * Adds both [TestConstants.filteredListing1] and [TestConstants.filteredListing2] to the
     * comparison.
     */
    private fun TestScope.givenListingsInComparison() {
        listOf(TestConstants.LISTING_ID_1, TestConstants.LISTING_ID_2).forEach { listingId ->
            viewModel.setEvent(
                MarketplaceContract.Event.Listing.OnComparisonClicked(
                    listingId = listingId,
                    category = TestConstants.apparelCategory
                )
            )
            advanceUntilIdle()
        }
    }

    /**
     * Captures the [MessageBannerAction] of the message banner shown with [message] and invokes
     * its [MessageBannerActionType.Custom] action.
     */
    private fun invokeMessageBannerCustomAction(message: String) {
        val actionCaptor = argumentCaptor<MessageBannerAction>()
        verify(messageBannerDelegate).showMessageBanner(
            message = eq(message),
            messageBannerDuration = eq(MessageBannerDuration.Short),
            action = actionCaptor.capture()
        )
        assertEquals(
            TestConstants.BANNER_ACTION_LABEL,
            actionCaptor.firstValue.label
        )
        (actionCaptor.firstValue.actionType as MessageBannerActionType.Custom).action()
    }

    private fun stubString(@StringRes resId: Int, value: String, vararg formatArgs: Any?) {
        `when`(resourceProvider.getString(resId, *formatArgs)).thenReturn(value)
    }

    private fun stubComparisonStrings() {
        stubString(R.string.screen_marketplace_filtered_listing_added_to_comparison_success, TestConstants.COMPARISON_ADDED_MESSAGE)
        stubString(R.string.screen_marketplace_filtered_listing_removed_from_comparison_success, TestConstants.COMPARISON_REMOVED_MESSAGE)
        stubString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label, TestConstants.BANNER_ACTION_LABEL)
        stubString(
            R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_title,
            TestConstants.COMPARISON_INELIGIBLE_MESSAGE,
            TestConstants.apparelCategory.label,
            TestConstants.comicsCategory.label
        )
        stubString(R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_button, TestConstants.COMPARISON_INELIGIBLE_BUTTON)
        stubString(
            R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_action_executed_message,
            TestConstants.COMPARISON_REPLACED_MESSAGE,
            TestConstants.comicsCategory.label
        )
    }

    private fun stubPriceValidation() {
        `when`(configPriceLogic.priceScale).thenReturn(TestConstants.PRICE_SCALE)
        `when`(configPriceLogic.roundingMode).thenReturn(TestConstants.roundingMode)
        `when`(configPriceLogic.minimumPrice).thenReturn(TestConstants.minimumPrice)
        `when`(configPriceLogic.maximumPrice).thenReturn(TestConstants.maximumPrice)
        stubString(R.string.screen_marketplace_error_invalid_price, TestConstants.INVALID_PRICE_MESSAGE)
        stubString(
            R.string.screen_marketplace_error_below_min_price,
            TestConstants.BELOW_MIN_PRICE_MESSAGE,
            TestConstants.minimumPrice.formatPrice(TestConstants.PRICE_SCALE, TestConstants.roundingMode)
        )
        stubString(
            R.string.screen_marketplace_error_over_max_price,
            TestConstants.OVER_MAX_PRICE_MESSAGE,
            TestConstants.maximumPrice.formatPrice(TestConstants.PRICE_SCALE, TestConstants.roundingMode)
        )
    }

    private fun stubDateRangePicker() {
        mockkObject(DateUtil)
        every { DateUtil.getCurrentLocalDate() } returns TestConstants.currentDate
        stubString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_label, TestConstants.DATE_PICKER_TITLE)
        stubString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_negative_button, TestConstants.DATE_PICKER_NEGATIVE)
        stubString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_positive_button, TestConstants.DATE_PICKER_POSITIVE)
    }

    private fun expectedShowDateRangePickerEffect(selectedDateRange: Pair<Long, Long>?) =
        MarketplaceContract.Effect.ShowDateRangePicker(
            selectedDateRange = selectedDateRange,
            minDateMillis = TestConstants.datePickerMinDate.toEpochMillis(),
            maxDateMillis = TestConstants.currentDate.toEpochMillis(),
            title = TestConstants.DATE_PICKER_TITLE,
            negativeButtonLabel = TestConstants.DATE_PICKER_NEGATIVE,
            positiveButtonLabel = TestConstants.DATE_PICKER_POSITIVE
        )

    private object TestConstants {
        const val PRICE_SCALE = 2
        const val PROMO_CAROUSEL_DELAY_MILLIS = 8000L
        const val ELAPSED_EPOCH_MILLIS = 10_000L
        const val NOT_ELAPSED_EPOCH_MILLIS = 5_000L

        const val LISTING_ID_1 = "listing_1"
        const val LISTING_ID_2 = "listing_2"
        const val LISTING_ID_3 = "listing_3"

        const val SEARCH_TERM = "spiderman"
        const val UNSANITIZED_PRICE = "1a2.5€"
        const val SANITIZED_PRICE = "12.5"

        const val SUCCESS_MESSAGE = "Success message"
        const val ERROR_MESSAGE = "Error message"
        const val OK_LABEL = "OK"
        const val BANNER_ACTION_LABEL = "View"
        const val COMPARISON_ADDED_MESSAGE = "Added to comparison"
        const val COMPARISON_REMOVED_MESSAGE = "Removed from comparison"
        const val COMPARISON_INELIGIBLE_MESSAGE = "Only listings of the same category can be compared"
        const val COMPARISON_INELIGIBLE_BUTTON = "Replace"
        const val COMPARISON_REPLACED_MESSAGE = "Comparison replaced"
        const val LAST_COMPARISON_REMOVED_MESSAGE = "Last listing removed from comparison"
        const val CATEGORY_REMOVED_MESSAGE = "Category filter removed"
        const val FILTERS_RESET_MESSAGE = "Filters reset"
        const val INVALID_PRICE_MESSAGE = "Invalid price"
        const val BELOW_MIN_PRICE_MESSAGE = "Below minimum price"
        const val OVER_MAX_PRICE_MESSAGE = "Over maximum price"
        const val DATE_PICKER_TITLE = "Select range"
        const val DATE_PICKER_NEGATIVE = "Cancel"
        const val DATE_PICKER_POSITIVE = "Apply"

        val roundingMode = RoundingMode.HALF_UP
        val minimumPrice = "0.99".toBigDecimal()
        val maximumPrice = "9999.99".toBigDecimal()

        val currentDate: LocalDate = LocalDate.of(2026, 9, 26)
        val datePickerMinDate: LocalDate = LocalDate.of(2026, 1, 1)
        val customRangeFrom: LocalDate = LocalDate.of(2026, 3, 1)
        val customRangeTo: LocalDate = LocalDate.of(2026, 3, 15)

        val customRangeUi = ListingDatePostedCustomRangeUi(
            from = LocalDateUi(value = customRangeFrom, label = "01/03/2026"),
            to = LocalDateUi(value = customRangeTo, label = "15/03/2026")
        )

        val listingPreviewDomain = ListingPreviewDomain(
            listingId = LISTING_ID_1,
            title = "Spiderman leather jacket XL -- 2010 edition",
            price = "150.30".toBigDecimal(),
            coverImageUrl = "",
            favorite = true,
            cart = false
        )

        val listingPreviewUi = ListingPreviewUi(
            listingId = LISTING_ID_1,
            title = "Spiderman leather jacket XL -- 2010 edition",
            priceLabel = "€150.30",
            coverImageUrl = "",
            favorite = true,
            cart = false
        )

        val marketplaceDomain = MarketplaceDomain(
            promo = MarketplacePromoSectionDomain(
                title = "Hot picks!!!",
                listings = listOf(listingPreviewDomain)
            ),
            categories = listOf(
                MarketplaceCategorySectionDomain(
                    title = "Apparel",
                    categoryCode = "apparel",
                    listings = listOf(listingPreviewDomain)
                )
            )
        )

        val marketplaceUi = MarketplaceUi(
            promo = MarketplacePromoSectionUi(
                title = "Hot picks!!!",
                listings = persistentListOf(listingPreviewUi)
            ),
            categories = listOf(
                MarketplaceCategorySectionUi(
                    title = "Apparel",
                    categoryCode = "apparel",
                    listings = persistentListOf(listingPreviewUi)
                )
            )
        )

        val marketplaceUiWithTwoPromoListings = marketplaceUi.copy(
            promo = MarketplacePromoSectionUi(
                title = "Hot picks!!!",
                listings = persistentListOf(
                    listingPreviewUi,
                    listingPreviewUi.copy(listingId = LISTING_ID_2)
                )
            )
        )

        val listingCategory = ListingCategory(
            id = 1,
            code = "apparel",
            label = "Apparel",
            subcategories = emptyList()
        )

        val listingCondition = EnumGeneric(
            code = "good",
            label = "Good"
        )

        val apparelCategory = EnumGeneric(
            code = "apparel",
            label = "Apparel"
        )

        val comicsCategory = EnumGeneric(
            code = "comics",
            label = "Comics"
        )

        val filteredListing1 = FilteredListingUi(
            listingId = LISTING_ID_1,
            category = apparelCategory,
            condition = listingCondition,
            title = "Spiderman leather jacket XL -- 2010 edition",
            price = BigDecimalUi(
                value = "150.30".toBigDecimal(),
                label = "€150.30"
            ),
            coverImageUrl = "",
            details = ListingDetailsDomain.ApparelDetailsDomain(
                size = EnumGeneric(code = "xl", label = "XL"),
                color = "Red"
            ),
            finalSale = false,
            postedAt = LocalDateUi(
                value = LocalDate.of(2026, 2, 10),
                label = "10/02/2026"
            ),
            favorite = ToggleStateUi(isChecked = false),
            cart = ToggleStateUi(isChecked = false)
        )

        val filteredListing2 = filteredListing1.copy(
            listingId = LISTING_ID_2,
            title = "Iron Man hoodie M"
        )

        val comicListing = filteredListing1.copy(
            listingId = LISTING_ID_3,
            category = comicsCategory,
            title = "Amazing Spider-Man #300"
        )

        val filteredListings = listOf(filteredListing1, filteredListing2)

        val filteredListingsWithComic = listOf(filteredListing1, filteredListing2, comicListing)
    }
}
