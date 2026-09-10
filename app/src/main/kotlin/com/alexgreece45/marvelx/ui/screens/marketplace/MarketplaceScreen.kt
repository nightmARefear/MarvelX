package com.alexgreece45.marvelx.ui.screens.marketplace

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Badge
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Pair
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.components.statusContainer.StatusContainer
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerEvent
import com.alexgreece45.marvelx.common_ui.previewAnnotation.ThemePreview
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.ui.components.ActiveFilterChip
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ClickableIconData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.GenericSearchBar
import com.alexgreece45.marvelx.ui.components.LoadingScreen
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapCheckbox
import com.alexgreece45.marvelx.ui.components.WrapHorizontalPager
import com.alexgreece45.marvelx.ui.components.WrapImage
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.WrapTextButton
import com.alexgreece45.marvelx.ui.components.chips.WrapChip
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.marketplace.model.FilteredListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDateRangeOptionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingPreviewUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceBottomSheetType
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceComparisonUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract.Event.SortByBottomSheet.OnSortSelected
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceListingsLayout
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceSearchCriteriaUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.SelectableOption
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi
import com.alexgreece45.marvelx.ui.visual_transformations.VisualTransformationCurrencySuffix
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.getFragmentActivity
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.showIfHidden
import com.alexgreece45.marvelx.util.extensions.showToast
import com.alexgreece45.marvelx.util.extensions.withEuroSymbol
import com.google.android.material.datepicker.CalendarConstraints.Builder
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

private val FILTERED_LISTING_GRID_VIEW_WIDTH = 140.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun MarketplaceScreen(
    onEvent: (MarketplaceContract.Event) -> Unit,
    state: MarketplaceContract.State,
    effect: SingleObserverFlow<MarketplaceContract.Effect>,
    onNavigation: (MarketplaceContract.Effect.Navigation) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    snackbarEvents: SingleObserverFlow<SnackbarEvent>,
    messageBannerEvents: SingleObserverFlow<MessageBannerEvent>,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sheetState = rememberWrapModalBottomSheetState(
//        confirmValueChange = { it != ModalBottomSheetValue.Hidden } // TODO: If let uncommented and the bottom sheet closes, the scrim still remains.
    )
    val focusRequester = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()

    ObserveUiEffect(flow = effect, tag = "MarketplaceScreen") { uiEffect ->
        when (uiEffect) {
            is MarketplaceContract.Effect.Navigation -> {
                onNavigation(uiEffect)
            }

            is MarketplaceContract.Effect.ShowDateRangePicker -> {
                val datePostedCustomRangePicker = MaterialDatePicker.Builder
                    .dateRangePicker()
                    .setTheme(R.style.MarvelXDateRangePickerTheme)
                    .apply {
                        uiEffect.selectedDateRange?.let { range ->
                            setSelection(
                                Pair(
                                    range.first,
                                    range.second
                                )
                            )
                        }
                    }
                    .setTitleText(uiEffect.title)
                    .setNegativeButtonText(uiEffect.negativeButtonLabel)
                    .setPositiveButtonText(uiEffect.positiveButtonLabel)
                    .setCalendarConstraints(
                        Builder()
                            .setStart(uiEffect.minDateMillis)
                            .setEnd(uiEffect.maxDateMillis)
                            .setValidator(
                                CompositeDateValidator.allOf(
                                    listOf(
                                        DateValidatorPointForward.from(uiEffect.minDateMillis),
                                        DateValidatorPointBackward.before(uiEffect.maxDateMillis)
                                    )
                                )
                            )
                            .build()
                    )
                    .build()

                datePostedCustomRangePicker.addOnPositiveButtonClickListener { dateRange ->
                    onEvent(
                        OnRangeSelected(
                            minDateMillis = dateRange.first,
                            maxDateMillis = dateRange.second
                        )
                    )
                }

                val fragmentManager = context.getFragmentActivity()

                fragmentManager?.let { fm ->
                    if (!datePostedCustomRangePicker.isAdded) {
                        datePostedCustomRangePicker.show(
                            fm.supportFragmentManager,
                            "datePostedCustomRangePicker"
                        )
                    }
                }
            }

            MarketplaceContract.Effect.HideBottomSheet -> {
                coroutineScope.launch {
                    sheetState.hideIfVisible()
                }
            }

            MarketplaceContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch {
                    sheetState.showIfHidden()
                }
            }

            MarketplaceContract.Effect.RequestSearchBarFieldFocus -> {
                focusRequester.requestFocus()
            }

            is MarketplaceContract.Effect.Share.Comparison -> context.showToast("Sharing comparison list not supported!")
            is MarketplaceContract.Effect.Share.Filters -> context.showToast("Sharing filters not supported!")
            is MarketplaceContract.Effect.ShowToastMessage -> context.showToast(uiEffect.message)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(MarketplaceContract.Event.Init)
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            when (state.bottomSheetType) {
                MarketplaceBottomSheetType.SORT -> {
                    SortBottomSheetContent(
                        activeSort = state.activeSort,
                        onCloseButtonClick = { onEvent(MarketplaceContract.Event.SortByBottomSheet.OnCloseButtonClicked) },
                        onSortSelect = {
                            onEvent(
                                OnSortSelected(
                                    it
                                )
                            )
                        }
                    )
                }

                MarketplaceBottomSheetType.FILTER_MENU -> {
                    state.draftSearchCriteria?.let { draftSearchCriteria ->
                        FilterMenuBottomSheetContent(
                            searchCriteria = draftSearchCriteria,
                            onFilterMenuEvent = onEvent,
                            onFilterEvent = onEvent
                        )
                    }
                }

                MarketplaceBottomSheetType.COMPARISON -> {
                    ComparisonBottomSheetContent(
                        comparison = state.comparison,
                        onComparisonEvent = onEvent
                    )
                }

                null -> {}
            }
        },
        sheetState = sheetState,
        onDismissed = { onEvent(MarketplaceContract.Event.OnBottomSheetDismissed) }
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = if (state.mode == MarketplaceMode.DISCOVER) ScreenNavigableAction.None else ScreenNavigableAction.Backable(
                onBack = { onEvent(MarketplaceContract.Event.OnBackClicked) }
            ),
            showToolbar = true,
            toolbarTitle = stringResource(if (state.mode == MarketplaceMode.DISCOVER) R.string.screen_marketplace_discover_toolbar_title else R.string.screen_marketplace_search_toolbar_title),
            bottomNavigationBarData = bottomNavigationBarData(),
            snackbarEvents = snackbarEvents,
            messageBannerEvents = messageBannerEvents
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {}

                    state.hasError -> {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            GenericErrorScreen(
                                errorMessage = stringResource(R.string.screen_marketplace_generic_error_message),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(maxHeight),
                                buttonLabel = stringResource(R.string.generic_retry_button),
                                onErrorButtonClick = { onEvent(MarketplaceContract.Event.OnErrorButtonClicked) }
                            )
                        }
                    }

                    state.marketplaceUi != null -> {
                        PullRefreshable(
                            onRefresh = { onEvent(MarketplaceContract.Event.OnRefresh) },
                            isRefreshing = state.isRefreshing
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                                contentPadding = paddingValues,
                                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
                                horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                            ) {
                                item(
                                    key = "searchBarSection",
                                    span = { GridItemSpan(maxLineSpan) }) {
                                    SearchBarSection(
                                        mode = state.mode,
                                        listingsLayout = state.listingsLayout,
                                        searchQuery = state.searchQuery,
                                        selectedCategories = state.activeSearchCriteria.categories
                                            .filter { it.isSelected }
                                            .map { it.option },
                                        uniqueFiltersApplied = state.activeSearchCriteria.validFiltersCount,
                                        activeSort = state.activeSort,
                                        comparisonListingsCount = state.comparison.listings.size,
                                        comparisonCategory = state.comparison.comparisonCategory,
                                        searchBarModifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester = focusRequester),
                                        onSearchBarClick = { onEvent(MarketplaceContract.Event.SearchBar.OnClicked) },
                                        onSearchQueryChange = {
                                            onEvent(
                                                MarketplaceContract.Event.SearchBar.OnSearchTermChanged(
                                                    it
                                                )
                                            )
                                        },
                                        onDeleteIconClick = { onEvent(MarketplaceContract.Event.SearchBar.OnDeleteIconClicked) },
                                        onComparisonStatusContainerButtonClick = {
                                            onEvent(
                                                MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked
                                            )
                                        },
                                        onSortChipClick = { onEvent(MarketplaceContract.Event.OnSortChipClicked) },
                                        onFiltersChipClick = { onEvent(MarketplaceContract.Event.OnFiltersChipClicked) },
                                        onLayoutChangeButtonClick = {
                                            onEvent(
                                                MarketplaceContract.Event.OnLayoutChangeButtonClicked
                                            )
                                        },
                                        onActiveCategoryDeleteIconClick = {
                                            onEvent(
                                                MarketplaceContract.Event.OnActiveCategoryDeleteIconClicked(
                                                    it
                                                )
                                            )
                                        }
                                    )
                                }

                                if (state.isRefreshing) {
                                    item(
                                        key = "refreshingContent",
                                        span = { GridItemSpan(maxLineSpan) }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LoadingScreen()
                                        }
                                    }
                                } else {
                                    when (state.mode) {
                                        MarketplaceMode.DISCOVER -> {
                                            item(
                                                key = "discoverContent",
                                                span = { GridItemSpan(maxLineSpan) }
                                            ) {
                                                DiscoverContent(
                                                    marketplaceUi = state.marketplaceUi,
                                                    onEvent = onEvent
                                                )
                                            }
                                        }

                                        MarketplaceMode.SEARCH -> {
                                            when (val searchUiState = state.searchUiState) {
                                                UiState.Loading -> {
                                                    item(
                                                        key = "searchLoading",
                                                        span = { GridItemSpan(maxLineSpan) }
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(top = 48.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            LoadingScreen()
                                                        }
                                                    }
                                                }

                                                is UiState.Error -> {
                                                    item(
                                                        key = "searchError",
                                                        span = { GridItemSpan(maxLineSpan) }
                                                    ) {
                                                        GenericErrorScreen(
                                                            errorMessage = stringResource(R.string.screen_marketplace_generic_error_message),
                                                            modifier = Modifier.fillMaxSize(),
                                                            onErrorButtonClick = {
                                                                onEvent(
                                                                    MarketplaceContract.Event.OnErrorButtonClicked
                                                                )
                                                            }
                                                        )
                                                    }
                                                }

                                                is UiState.Success -> {
                                                    if (searchUiState.data.isEmpty()) {
                                                        item(
                                                            key = "searchListingsEmpty",
                                                            span = { GridItemSpan(maxLineSpan) }
                                                        ) {
                                                            NoListingsContent(searchQuery = state.searchQuery)
                                                        }
                                                    } else {
                                                        items(
                                                            items = searchUiState.data,
                                                            key = { it.listingId },
                                                            span = {
                                                                GridItemSpan(
                                                                    currentLineSpan = when (state.listingsLayout) {
                                                                        MarketplaceListingsLayout.GRID -> 1
                                                                        MarketplaceListingsLayout.LIST -> maxLineSpan
                                                                    }
                                                                )
                                                            }
                                                        ) { listing ->
                                                            FilteredListingItem(
                                                                filteredListing = listing,
                                                                listingsLayout = state.listingsLayout,
                                                                onListingClick = {
                                                                    onEvent(
                                                                        MarketplaceContract.Event.Listing.OnClicked(
                                                                            listingId = listing.listingId
                                                                        )
                                                                    )
                                                                },
                                                                onFavoriteClick = {
                                                                    onEvent(
                                                                        MarketplaceContract.Event.Listing.OnFavoriteClicked(
                                                                            listingId = listing.listingId
                                                                        )
                                                                    )
                                                                },
                                                                onComparisonClick = {
                                                                    onEvent(
                                                                        MarketplaceContract.Event.Listing.OnComparisonClicked(
                                                                            listingId = listing.listingId,
                                                                            category = listing.category
                                                                        )
                                                                    )
                                                                },
                                                                onCartClick = {
                                                                    onEvent(
                                                                        MarketplaceContract.Event.Listing.OnCartClicked(
                                                                            listingId = listing.listingId
                                                                        )
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchBarSection(
    mode: MarketplaceMode,
    listingsLayout: MarketplaceListingsLayout,
    searchQuery: String,
    selectedCategories: List<EnumGeneric>,
    uniqueFiltersApplied: Int,
    activeSort: ListingSort,
    comparisonListingsCount: Int,
    comparisonCategory: EnumGeneric?,
    @SuppressLint("ModifierParameter") searchBarModifier: Modifier,
    onSearchBarClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDeleteIconClick: () -> Unit,
    onComparisonStatusContainerButtonClick: () -> Unit,
    onSortChipClick: () -> Unit,
    onFiltersChipClick: () -> Unit,
    onLayoutChangeButtonClick: () -> Unit,
    onActiveCategoryDeleteIconClick: (category: EnumGeneric) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        // Search bar.
        Box(modifier = Modifier.fillMaxWidth()) {
            GenericSearchBar(
                searchTerm = searchQuery,
                hint = stringResource(R.string.screen_marketplace_search_bar_hint),
                modifier = searchBarModifier,
                onValueChange = onSearchQueryChange,
                onDeleteIconClicked = onDeleteIconClick
            )

            if (mode == MarketplaceMode.DISCOVER) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickableNoRipple(
                            role = Role.Button,
                            onClick = onSearchBarClick
                        )
                )
            }
        }

        // Comparison list status container.
        AnimatedVisibility(
            visible = comparisonListingsCount > 0 && comparisonCategory != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            val statusContainerColors = MaterialTheme.customColors.warningStatusContainerColors
            StatusContainer(
                title = stringResource(
                    R.string.screen_marketplace_listings_for_comparison_title,
                    comparisonCategory?.label.orEmpty(),
                    comparisonListingsCount
                ),
                titleColor = MaterialTheme.customColors.white,
                backgroundColor = statusContainerColors.backgroundColor,
                outlineColor = null,
                icon = {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            painter = painterResource(UiThemeDrawable.ic_compare),
                            contentDescription = null,
                            modifier = Modifier.rotate(30f)
                        )

                        Badge(
                            modifier = Modifier.size(SPACING_SMALL.dp),
                            backgroundColor = statusContainerColors.contentColor,
                            content = {}
                        )
                    }
                },
                extraTitleContent = {
                    WrapTextButton(onClick = onComparisonStatusContainerButtonClick) {
                        Text(
                            text = stringResource(R.string.screen_marketplace_listings_for_comparison_button),
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.customColors.white
                        )
                    }
                }
            )
        }

        // sort & filter chips.
        AnimatedVisibility(
            visible = mode == MarketplaceMode.SEARCH,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                SortFilterChipsRow(
                    listingsLayout = listingsLayout,
                    uniqueFiltersApplied = uniqueFiltersApplied,
                    activeSort = activeSort,
                    onSortChipClick = onSortChipClick,
                    onFiltersChipClick = onFiltersChipClick,
                    onLayoutChangeButtonClick = onLayoutChangeButtonClick
                )

                // Selected categories.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    selectedCategories.forEach { category ->
                        ActiveFilterChip(
                            label = category.label,
                            onRemoveClick = { onActiveCategoryDeleteIconClick(category) },
                            iconContentDescription = stringResource(
                                R.string.screen_marketplace_content_description_remove_category,
                                category.label
                            )
                        )
                    }
                }
            }
        }
    }
}

// Sort, Filter chips & listings layout button
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SortFilterChipsRow(
    modifier: Modifier = Modifier,
    listingsLayout: MarketplaceListingsLayout,
    uniqueFiltersApplied: Int,
    activeSort: ListingSort,
    onSortChipClick: () -> Unit,
    onFiltersChipClick: () -> Unit,
    onLayoutChangeButtonClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sort chip.
        WrapChip(
            onClick = onSortChipClick,
            contentPaddingValues = PaddingValues(SPACING_SMALL.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(UiThemeDrawable.ic_sort),
                    contentDescription = null,
                    tint = MaterialTheme.customColors.white
                )
            },
            content = {
                Text(
                    text = stringResource(
                        when (activeSort) {
                            ListingSort.PRICE_ASCENDING -> R.string.screen_marketplace_sort_price_ascending_label
                            ListingSort.PRICE_DESCENDING -> R.string.screen_marketplace_sort_price_descending_label
                            ListingSort.DATE_POSTED_ASCENDING -> R.string.screen_marketplace_sort_date_posted_ascending_label
                            ListingSort.DATE_POSTED_DESCENDING -> R.string.screen_marketplace_sort_date_posted_descending_label
                        }
                    )
                )
            }
        )

        // Filters chip.
        WrapChip(
            onClick = onFiltersChipClick,
            border = if (uniqueFiltersApplied > 0) BorderStroke(
                width = 1.dp,
                MaterialTheme.customColors.errorStatusContainerColors.outlineColor
            ) else null,
            colors = if (uniqueFiltersApplied > 0) ChipDefaults.chipColors(
                backgroundColor = MaterialTheme.customColors.errorStatusContainerColors.backgroundColor,
                contentColor = MaterialTheme.customColors.errorStatusContainerColors.contentColor,
                leadingIconContentColor = MaterialTheme.customColors.errorStatusContainerColors.contentColor
            ) else ChipDefaults.chipColors(leadingIconContentColor = MaterialTheme.customColors.white),
            contentPaddingValues = PaddingValues(SPACING_SMALL.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(UiThemeDrawable.ic_filter),
                    contentDescription = null
                )
            },
            content = {
                Row(
                    //                modifier = Modifier.padding(SPACING_SMALL.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        SPACING_TINY.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.screen_marketplace_filters_label))

                    if (uniqueFiltersApplied > 0) {
                        Badge(
                            backgroundColor = MaterialTheme.customColors.genericRed,
                            contentColor = MaterialTheme.customColors.white
                        ) {
                            Text(
                                text = if (uniqueFiltersApplied > 9) "9+" else "$uniqueFiltersApplied",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onLayoutChangeButtonClick) {
            Icon(
                painter = painterResource(
                    when (listingsLayout) {
                        MarketplaceListingsLayout.GRID -> UiThemeDrawable.ic_list_view
                        MarketplaceListingsLayout.LIST -> UiThemeDrawable.ic_grid
                    }
                ),
                contentDescription = stringResource(
                    when (listingsLayout) {
                        MarketplaceListingsLayout.GRID -> R.string.screen_marketplace_content_description_change_to_list_view_button
                        MarketplaceListingsLayout.LIST -> R.string.screen_marketplace_content_description_change_to_grid_view_button
                    }
                ),
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun DiscoverContent(
    marketplaceUi: MarketplaceUi,
    onEvent: (MarketplaceContract.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_LARGE.dp)
    ) {
        marketplaceUi.promo?.let { promo ->
            PromoSection(
                categoryTitle = promo.title,
                listingsCount = promo.listings.size,
                selectedListingIndex = promo.selectedListingIndex,
                onListingClicked = {
                    onEvent(MarketplaceContract.Event.Listing.OnClicked(it))
                },
                onPageChanged = {
                    onEvent(MarketplaceContract.Event.OnPromoCarouselScrolled(it))
                },
                getListingByPage = { promo.listings.getOrNull(it) }
            )
        }

        marketplaceUi.categories.forEach { categorySectionUi ->
            ListingCategorySection(
                categoryTitle = categorySectionUi.title,
                listings = categorySectionUi.listings,
                onCategoryClicked = {
                    onEvent(
                        MarketplaceContract.Event.OnCategoryClicked(
                            categorySectionUi.categoryCode
                        )
                    )
                },
                onListingClicked = {
                    onEvent(MarketplaceContract.Event.Listing.OnClicked(it))
                }
            )
        }
    }
}

@Composable
private fun PromoSection(
    categoryTitle: String,
    listingsCount: Int,
    selectedListingIndex: Int,
    onListingClicked: (String) -> Unit,
    onPageChanged: (Int) -> Unit,
    getListingByPage: (Int) -> ListingPreviewUi?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        Text(text = categoryTitle)

        WrapHorizontalPager(
            pageCount = listingsCount,
            selectedIndex = selectedListingIndex,
            onPageChanged = onPageChanged
        ) { page ->
            getListingByPage(page)?.let { currentListing ->
                ListingPreviewItem(
                    isPromo = true,
                    listingPreviewUi = currentListing,
                    onListingClick = { onListingClicked(currentListing.listingId) }
                )
            }
        }
    }
}

@Composable
private fun ListingCategorySection(
    categoryTitle: String,
    listings: ImmutableList<ListingPreviewUi>,
    onCategoryClicked: (() -> Unit)? = null,
    onListingClicked: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickableNoRipple(
                        enabled = onCategoryClicked != null,
                        onClick = onCategoryClicked ?: {}
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.body1
                )

                NavigateIcon()
            }

            ListingPreviewItems(listings, onListingClicked)
        }
    }
}

@Composable
private fun ListingPreviewItems(
    listings: ImmutableList<ListingPreviewUi>,
    onListingClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = listings,
            key = { it.listingId }
        ) { listing ->
            ListingPreviewItem(
                listingPreviewUi = listing,
                onListingClick = { onListingClick(listing.listingId) }
            )
        }
    }
}

@Composable
private fun ListingPreviewItem(
    isPromo: Boolean = false,
    listingPreviewUi: ListingPreviewUi,
    onListingClick: () -> Unit
) {
    val columnModifier =
        if (isPromo) {
            Modifier.fillMaxWidth()
        } else {
            Modifier
        }

    val imageModifier =
        if (isPromo) {
            Modifier
                .fillMaxWidth()
                .height(150.dp)
        } else {
            Modifier.size(90.dp)
        }

    Column(
        modifier = columnModifier.then(Modifier.clickableNoRipple(onClick = onListingClick)),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        ListingImage(
            imageUrl = listingPreviewUi.coverImageUrl,
            title = listingPreviewUi.title,
            modifier = imageModifier,
            contentScale = if (isPromo) ContentScale.Fit else ContentScale.Crop
        )

        Text(text = listingPreviewUi.title)

        Text(
            text = listingPreviewUi.priceLabel,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun FilteredListingItem(
    filteredListing: FilteredListingUi,
    listingsLayout: MarketplaceListingsLayout,
    onListingClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onComparisonClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onListingClick),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        // Listing details (only in list view), image & action buttons.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            // Image & action buttons.
            Box(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.customColors.white)
                    .then(
                        if (listingsLayout == MarketplaceListingsLayout.GRID) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier.width(FILTERED_LISTING_GRID_VIEW_WIDTH)
                        }
                    )
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                ListingImage(
                    imageUrl = filteredListing.coverImageUrl,
                    title = filteredListing.title,
                    modifier = Modifier
                        .padding(SPACING_MEDIUM.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .fillMaxSize()
                )

                // Favorite & Compare icons.
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    FilteredListingItemImageActionButton(
                        isLoading = filteredListing.favorite.isLoading,
                        onClick = onFavoriteClick,
                        icon = {
                            if (filteredListing.favorite.isChecked) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_favorite_filled),
                                    contentDescription = null,
                                    tint = MaterialTheme.customColors.genericRed
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_favorite_empty),
                                    contentDescription = null,
                                    tint = MaterialTheme.customColors.white
                                )
                            }
                        }
                    )

                    FilteredListingItemImageActionButton(
                        isLoading = false,
                        onClick = onComparisonClick,
                        icon = {
                            Icon(
                                painter = painterResource(UiThemeDrawable.ic_compare),
                                contentDescription = null,
                                modifier = Modifier.rotate(30f),
                                tint = if (filteredListing.comparison) {
                                    MaterialTheme.customColors.genericRed
                                } else {
                                    MaterialTheme.customColors.white
                                }
                            )
                        }
                    )
                }

                // Add to cart icon.
                AnimatedContent(
                    targetState = filteredListing.cart.isChecked,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    contentAlignment = Alignment.BottomEnd,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 300)) using
                                SizeTransform { _, _ ->
                                    tween(durationMillis = 500)
                                }
                    },
                    label = "AddToCartAnimation"
                ) { cart ->
                    if (cart) {
                        WrapTextButton(
                            onClick = onCartClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            shape = RoundedCornerShape(
                                topStart = SPACING_MEDIUM.dp,
                                topEnd = SPACING_MEDIUM.dp
                            ),
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = Color(
                                    0xD821F33D
                                )
                            )
                        ) {
                            if (filteredListing.cart.isLoading) {
                                FilteredListingItemImageActionLoadingIndicator(indicatorColor = MaterialTheme.customColors.white)
                            } else {
                                Text(text = stringResource(R.string.screen_marketplace_filtered_listing_remove_from_cart_button))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(RoundedCornerShape(topStart = SPACING_MEDIUM.dp))
                                .background(color = Color(0xD821F33D))
                                .clickableNoRipple(onClick = onCartClick, role = Role.Button)
                                .height(32.dp)
                                .padding(horizontal = SPACING_SMALL.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (filteredListing.cart.isLoading) {
                                FilteredListingItemImageActionLoadingIndicator(indicatorColor = MaterialTheme.customColors.white)
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_cart),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF386641)
                                )
                            }
                        }
                    }
                }
            }

            if (listingsLayout == MarketplaceListingsLayout.LIST) {
                Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                    Text(
                        text = filteredListing.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Text(
                        text = filteredListing.price.label,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

        // Listing details.
        if (listingsLayout == MarketplaceListingsLayout.GRID) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                Text(
                    text = filteredListing.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                Text(
                    text = filteredListing.price.label,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
private fun FilteredListingItemImageActionButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.customColors.earthGray.copy(alpha = ContentAlpha.disabled),
                    shape = CircleShape
                )
                .padding(SPACING_TINY.dp),
            content = {
                if (isLoading) {
                    FilteredListingItemImageActionLoadingIndicator()
                } else {
                    icon()
                }
            }
        )
    }
}

@Composable
private fun FilteredListingItemImageActionLoadingIndicator(indicatorColor: Color = MaterialTheme.customColors.genericRed) {
    CircularProgressIndicator(
        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
        color = indicatorColor
    )
}

@Composable
private fun NoListingsContent(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(
            SPACING_MEDIUM.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WrapImage(
            painter = painterResource(
                UiThemeDrawable.ic_magnifying_glass
            ),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = if (searchQuery.isBlank()) stringResource(R.string.screen_marketplace_error_search_no_results_empty) else stringResource(
                R.string.screen_marketplace_error_search_no_results,
                searchQuery
            ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        Text(
            text = stringResource(R.string.screen_marketplace_error_search_no_results_additional_text),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SortBottomSheetContent(
    activeSort: ListingSort,
    onCloseButtonClick: () -> Unit,
    onSortSelect: (ListingSort) -> Unit
) {
    ModalBottomSheetContent(showDragHandle = false) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            BottomSheetStickyHeader(
                title = stringResource(R.string.screen_marketplace_sort_bottom_sheet_title),
                closeIconContentDescription = stringResource(R.string.screen_marketplace_content_description_close_sort_by_bottom_sheet_icon),
                onCloseButtonClick = onCloseButtonClick
            )

            ListingSort.entries.forEach { sort ->
                val isSelected = sort == activeSort
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onSortSelect(sort) }),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = MaterialTheme.customColors.white
                        )
                    } else {
                        Box(modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE))
                    }

                    Text(
                        text = stringResource(
                            when (sort) {
                                ListingSort.PRICE_ASCENDING -> R.string.screen_marketplace_sort_bottom_sheet_item_price_ascending_label
                                ListingSort.PRICE_DESCENDING -> R.string.screen_marketplace_sort_bottom_sheet_item_price_descending_label
                                ListingSort.DATE_POSTED_ASCENDING -> R.string.screen_marketplace_sort_bottom_sheet_item_date_posted_ascending_label
                                ListingSort.DATE_POSTED_DESCENDING -> R.string.screen_marketplace_sort_bottom_sheet_item_date_posted_descending_label
                            }
                        ),
                        color = LocalContentColor.current.copy(
                            alpha = if (isSelected) ContentAlpha.high else ContentAlpha.medium
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FilterMenuBottomSheetContent(
    searchCriteria: MarketplaceSearchCriteriaUi,
    onFilterMenuEvent: (MarketplaceContract.Event.FilterMenu) -> Unit,
    onFilterEvent: (MarketplaceContract.Event.Filter) -> Unit,
) {
    ModalBottomSheetContent(showDragHandle = false) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                BottomSheetStickyHeader(
                    title = stringResource(R.string.screen_marketplace_filter_menu_title),
                    closeIconContentDescription = stringResource(R.string.screen_marketplace_content_description_close_filter_menu_bottom_sheet_icon),
                    onCloseButtonClick = { onFilterMenuEvent(MarketplaceContract.Event.FilterMenu.OnCloseButtonClicked) }
                )

                // Filter sections.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    // Category.
                    FilterSection(
                        title = stringResource(R.string.screen_marketplace_filter_menu_section_category_title),
                        collapsedLabel = searchCriteria.categories
                            .filter { it.isSelected }
                            .joinToString(separator = ", ") { it.option.label }
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                            itemVerticalAlignment = Alignment.CenterVertically,
                        ) {
                            searchCriteria.categories.forEach { category ->
                                FilterChip(
                                    selected = category.isSelected,
                                    onClick = {
                                        onFilterEvent(
                                            MarketplaceContract.Event.Filter.OnCategorySelected(
                                                category.option.code
                                            )
                                        )
                                    },
                                    colors = ChipDefaults.filterChipColors(
                                        selectedBackgroundColor = MaterialTheme.customColors.chipBackgroundColors.selected
                                    ),
                                    content = { Text(text = category.option.label) }
                                )
                            }
                        }
                    }

                    // Condition.
                    FilterSection(
                        title = stringResource(R.string.screen_marketplace_filter_menu_section_condition_title),
                        collapsedLabel = searchCriteria.conditions
                            .filter { it.isSelected }
                            .joinToString(separator = ", ") { it.option.label }
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                            itemVerticalAlignment = Alignment.CenterVertically,
                        ) {
                            searchCriteria.conditions.forEach { condition ->
                                FilterChip(
                                    selected = condition.isSelected,
                                    onClick = {
                                        onFilterEvent(
                                            MarketplaceContract.Event.Filter.OnConditionSelected(
                                                condition.option.code
                                            )
                                        )
                                    },
                                    colors = ChipDefaults.filterChipColors(
                                        selectedBackgroundColor = MaterialTheme.customColors.chipBackgroundColors.selected
                                    ),
                                    content = { Text(text = condition.option.label) }
                                )
                            }
                        }
                    }

                    // Price range.
                    FilterSection(
                        title = stringResource(R.string.screen_marketplace_filter_menu_section_price_range_title),
                        collapsedLabel = when {
                            (searchCriteria.priceRangeMin.parsedValue != null && searchCriteria.priceRangeMin.isValid)
                                    && (searchCriteria.priceRangeMax.parsedValue != null && searchCriteria.priceRangeMax.isValid) -> stringResource(
                                R.string.screen_marketplace_filter_menu_section_price_range_collapsed_label,
                                searchCriteria.priceRangeMin.formattedValue.withEuroSymbol(),
                                searchCriteria.priceRangeMax.formattedValue.withEuroSymbol()
                            )

                            searchCriteria.priceRangeMin.parsedValue != null && searchCriteria.priceRangeMin.isValid -> stringResource(
                                R.string.screen_marketplace_filter_menu_section_price_range_min_collapsed_label,
                                searchCriteria.priceRangeMin.formattedValue.withEuroSymbol()
                            )

                            searchCriteria.priceRangeMax.parsedValue != null && searchCriteria.priceRangeMax.isValid -> stringResource(
                                R.string.screen_marketplace_filter_menu_section_price_range_max_collapsed_label,
                                searchCriteria.priceRangeMax.formattedValue.withEuroSymbol()
                            )

                            else -> ""
                        },
                        collapsedErrorLabel = stringResource(R.string.screen_marketplace_error_price_range_check),
                        hasError = !searchCriteria.priceRangeMin.isValid || !searchCriteria.priceRangeMax.isValid
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                        ) {
                            PriceField(
                                inputField = searchCriteria.priceRangeMin,
                                label = stringResource(R.string.screen_marketplace_filter_menu_section_price_range_min_label),
                                placeholder = stringResource(R.string.screen_marketplace_filter_menu_section_price_range_min_placeholder),
                                isValid = searchCriteria.priceRangeMin.isValid,
                                modifier = Modifier.weight(1f),
                                onValueChanged = { newPrice ->
                                    onFilterEvent(
                                        MarketplaceContract.Event.Filter.OnPriceRangeMinChanged(
                                            newPrice = newPrice
                                        )
                                    )
                                }
                            )

                            PriceField(
                                inputField = searchCriteria.priceRangeMax,
                                label = stringResource(R.string.screen_marketplace_filter_menu_section_price_range_max_label),
                                placeholder = stringResource(R.string.screen_marketplace_filter_menu_section_price_range_max_placeholder),
                                isValid = searchCriteria.priceRangeMax.isValid,
                                modifier = Modifier.weight(1f),
                                onValueChanged = { newPrice ->
                                    onFilterEvent(
                                        MarketplaceContract.Event.Filter.OnPriceRangeMaxChanged(
                                            newPrice = newPrice
                                        )
                                    )
                                }
                            )
                        }
                    }

                    // Date posted.
                    FilterSection(
                        title = stringResource(R.string.screen_marketplace_filter_menu_section_date_posted_title),
                        collapsedLabel = when {
                            searchCriteria.datePosted !is ListingDateRangeOptionUi.Custom -> stringResource(
                                searchCriteria.datePosted.labelResId
                            )

                            searchCriteria.datedPostedCustomRange != null -> searchCriteria.datedPostedCustomRange.label
                            else -> ""
                        },
                        collapsedErrorLabel = stringResource(R.string.screen_marketplace_error_date_range_select),
                        hasError = searchCriteria.datePosted is ListingDateRangeOptionUi.Custom && searchCriteria.datedPostedCustomRange == null
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                        ) {
                            ListingDateRangeOptionUi.entries.forEach { dateRangeOption ->
                                DatedPostedItem(
                                    label = stringResource(dateRangeOption.labelResId),
                                    isSelected = dateRangeOption == searchCriteria.datePosted,
                                    onSelect = {
                                        onFilterEvent(
                                            MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected(
                                                dateRangeOption
                                            )
                                        )
                                    },
                                    extraContent = if (dateRangeOption != ListingDateRangeOptionUi.Custom) {
                                        null
                                    } else {
                                        {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                OutlinedTextFieldValidation(
                                                    value = searchCriteria.datedPostedCustomRange?.label.orEmpty(),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = stringResource(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_label),
                                                    trailingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Filled.CalendarMonth,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.customColors.white // Since the field is not focusable.
                                                        )
                                                    }
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clickableNoRipple(
                                                            role = Role.Button,
                                                            onClick = {
                                                                onFilterEvent(
                                                                    MarketplaceContract.Event.Filter.DatePosted.OnDatePostedCustomRangeClicked
                                                                )
                                                            }
                                                        )
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Final sale.
                    FilterSection(
                        title = stringResource(R.string.screen_marketplace_filter_menu_section_final_title),
                        collapsedLabel = stringResource(if (searchCriteria.finalSale) R.string.screen_marketplace_filter_menu_section_final_collapsed_yes_label else R.string.screen_marketplace_filter_menu_section_final_collapsed_no_label)
                    ) {
                        WrapCheckbox(
                            label = stringResource(R.string.screen_marketplace_filter_menu_section_final_checkbox_label),
                            checked = searchCriteria.finalSale,
                            onCheckedChange = { onFilterEvent(MarketplaceContract.Event.Filter.OnFinalSaleChecked) }
                        )
                    }
                }

                // Sticky bottom.
                AnimatedContent(
                    targetState = searchCriteria.activeFiltersCount > 0,
                    label = "sticky_bottom_animation",
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(600)) togetherWith fadeOut(
                            animationSpec = tween(
                                600
                            )
                        ))
                            .using(
                                SizeTransform(
                                    clip = false,
                                    sizeAnimationSpec = { _, _ -> tween(600) }
                                )
                            )
                    }
                ) { hasActiveFilters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasActiveFilters) {
                            WrapButton(
                                onClick = { onFilterMenuEvent(MarketplaceContract.Event.FilterMenu.OnNegativeButtonClicked) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(R.string.screen_marketplace_filter_menu_negative_button_label))
                            }
                        }

                        WrapButton(
                            onClick = { onFilterMenuEvent(MarketplaceContract.Event.FilterMenu.OnPositiveButtonClicked) },
                            modifier = if (hasActiveFilters) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                            enabled = searchCriteria.areAllFiltersValid,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative,
                                contentColor = MaterialTheme.customColors.buttonContentWhite
                            )
                        ) {
                            Text(text = stringResource(R.string.screen_marketplace_filter_menu_positive_button_label))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonBottomSheetContent(
    comparison: MarketplaceComparisonUi,
    onComparisonEvent: (MarketplaceContract.Event.ComparisonBottomSheet) -> Unit
) {
    ModalBottomSheetContent(showDragHandle = false) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            BottomSheetStickyHeader(
                title = stringResource(
                    R.string.screen_marketplace_comparison_bottom_sheet_title,
                    comparison.comparisonCategory?.label.orEmpty(),
                    comparison.listings.size
                ),
                closeIconContentDescription = stringResource(R.string.screen_marketplace_content_description_close_comparison_bottom_sheet_icon),
                onCloseButtonClick = { onComparisonEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnCloseButtonClicked) },
                otherIconData = ClickableIconData(
                    iconResource = R.drawable.ic_share,
                    contentDescription = stringResource(R.string.screen_marketplace_content_description_share_comparison_listings),
                    onClick = { onComparisonEvent(MarketplaceContract.Event.ComparisonBottomSheet.OnShareButtonClicked) }
                )
            )

            WrapHorizontalPager(
                initialPage = 0,
                pageCount = comparison.carouselPageCount,
                selectedIndex = comparison.currentCarouselPageIndex,
                showPaginationIndicator = comparison.carouselPageCount > 1,
                contentPaddingValues = PaddingValues(),
                onPageChanged = { page ->
                    onComparisonEvent(
                        MarketplaceContract.Event.ComparisonBottomSheet.OnScrolled(
                            page = page
                        )
                    )
                }
            ) { _ ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (first, second) = comparison.currentCarouselItemIndices

                    first?.let { index ->
                        val listing = comparison.listings.getOrNull(index = index) ?: return@let
                        ComparisonBottomSheetListingItem(
                            listing = listing,
                            isSingleItem = second == null,
                            onClick = {
                                onComparisonEvent(
                                    MarketplaceContract.Event.ComparisonBottomSheet.OnListingClicked(
                                        listing.listingId
                                    )
                                )
                            },
                            onDeleteIconClick = {
                                onComparisonEvent(
                                    MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked(
                                        listing.listingId
                                    )
                                )
                            }
                        )
                    }

                    second?.let { index ->
                        val listing = comparison.listings.getOrNull(index = index) ?: return@let
                        ComparisonBottomSheetListingItem(
                            listing = listing,
                            isSingleItem = false,
                            onClick = {
                                onComparisonEvent(
                                    MarketplaceContract.Event.ComparisonBottomSheet.OnListingClicked(
                                        listing.listingId
                                    )
                                )
                            },
                            onDeleteIconClick = {
                                onComparisonEvent(
                                    MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked(
                                        listing.listingId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ComparisonBottomSheetListingItem(
    listing: FilteredListingUi,
    isSingleItem: Boolean,
    onClick: () -> Unit,
    onDeleteIconClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .then(
                if (isSingleItem) Modifier.fillMaxWidth(fraction = 0.5f) else Modifier.weight(1f)
            ),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Image, title, price
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickableNoRipple(onClick = onClick),
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            // Image.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.customColors.white),
                contentAlignment = Alignment.Center
            ) {
                ListingImage(
                    imageUrl = listing.coverImageUrl,
                    title = listing.title,
                    modifier = Modifier
                        .padding(SPACING_MEDIUM.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .fillMaxSize()
                )

                FilteredListingItemImageActionButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    isLoading = false,
                    onClick = onDeleteIconClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.screen_marketplace_content_description_remove_listing_from_comparison),
                            tint = MaterialTheme.customColors.white
                        )
                    }
                )
            }

            Text(
                text = listing.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            Text(
                text = listing.price.label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2
            )
        }

        // Advanced details.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            when (val details = listing.details) {
                is ListingDetailsDomain.ApparelDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_apparel_size),
                        value = details.size.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_color),
                        value = details.color
                    )
                }

                is ListingDetailsDomain.DrinkwareDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_material),
                        value = details.material.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_capacity),
                        value = details.capacityMl.toString()
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_dishwasher_safe),
                        value = details.dishwasherSafe.toString()
                    )
                }

                is ListingDetailsDomain.CollectibleDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_scale),
                        value = details.scale.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_box_condition),
                        value = details.boxCondition.label
                    )
                }

                is ListingDetailsDomain.ArtDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_width),
                        value = details.dimensions.width.value
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_height),
                        value = details.dimensions.height.value
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_framed),
                        value = details.framed.toString()
                    )
                }

                is ListingDetailsDomain.AccessoryDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_material),
                        value = details.material.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_color),
                        value = details.color
                    )
                }

                is ListingDetailsDomain.HomeDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_width),
                        value = details.dimensions.width.value
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_height),
                        value = details.dimensions.height.value
                    )
                }

                is ListingDetailsDomain.ComicDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_grade),
                        value = details.grade.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_publisher),
                        value = details.publisher
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_year),
                        value = details.year.toString()
                    )
                }

                is ListingDetailsDomain.CardDetailsDomain -> {
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_grade),
                        value = details.grade.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_rarity),
                        value = details.rarity.label
                    )
                    ComparisonListingDetailsItem(
                        label = stringResource(R.string.screen_create_listing_advanced_details_label_variant),
                        value = details.variant.label
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonListingDetailsItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SPACING_SMALL.dp, vertical = SPACING_TINY.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body2
        )

        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
                .padding(horizontal = SPACING_SMALL.dp, vertical = SPACING_SMALL.dp),
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun BottomSheetStickyHeader(
    title: String,
    closeIconContentDescription: String,
    onCloseButtonClick: () -> Unit,
    otherIconData: ClickableIconData? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomSheetStickyHeaderIcon(
            clickableIconData = ClickableIconData(
                iconResource = R.drawable.ic_close,
                contentDescription = closeIconContentDescription,
                onClick = onCloseButtonClick
            ),
            contentAlignment = if (otherIconData == null) Alignment.CenterEnd else Alignment.CenterStart
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        if (otherIconData == null) {
            Spacer(modifier = Modifier.width(48.dp)) // Matches the width of the icon button.
        } else {
            BottomSheetStickyHeaderIcon(
                clickableIconData = otherIconData,
                contentAlignment = Alignment.CenterEnd
            )
        }
    }
}

@Composable
private fun BottomSheetStickyHeaderIcon(
    clickableIconData: ClickableIconData,
    contentAlignment: Alignment
) {
    Box(contentAlignment = contentAlignment) {
        IconButton(
            onClick = clickableIconData.onClick
        ) {
            Icon(
                painter = painterResource(clickableIconData.iconResource),
                contentDescription = clickableIconData.contentDescription,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background.copy(alpha = ContentAlpha.medium),
                        shape = CircleShape
                    )
                    .padding(SPACING_SMALL.dp)
            )
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    collapsedLabel: String? = null,
    collapsedErrorLabel: String? = null,
    hasError: Boolean = false,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableNoRipple(onClick = { isExpanded = !isExpanded }),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )

            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) {
                    stringResource(R.string.content_description_collapse)
                } else {
                    stringResource(R.string.content_description_expand)
                },
                modifier = Modifier.rotate(
                    degrees = animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 400)
                    ).value
                )
            )
        }

        if (!isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = if (hasError) Alignment.Bottom else Alignment.CenterVertically
            ) {
                if (hasError && !collapsedErrorLabel.isNullOrBlank()) {
                    WrapImage(
                        painter = painterResource(com.alexgreece45.marvelx.ui_theme.R.drawable.ic_warning),
                        contentDescription = null
                    )

                    Text(text = collapsedErrorLabel, color = MaterialTheme.colors.error)
                } else {
                    if (!collapsedLabel.isNullOrBlank()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                            itemVerticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = collapsedLabel,
                                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            content = { content() }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DatedPostedItem(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    extraContent: (@Composable () -> Unit)? = null
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.onSurface
            .copy(alpha = 0.12f)
            .compositeOver(MaterialTheme.colors.surface) // Same background color as in `FilterChip`.
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACING_SMALL.dp),
                horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = isSelected, onClick = onSelect)

                SectionHeaderTitle(title = label)
            }

            extraContent?.let {
                AnimatedVisibility(
                    visible = isSelected,
                    modifier = Modifier.padding(
                        start = SPACING_MEDIUM.dp,
                        end = SPACING_MEDIUM.dp,
                        bottom = SPACING_MEDIUM.dp
                    ),
                    content = { extraContent() }
                )
            }
        }
    }
}

@Composable
private fun PriceField(
    inputField: InputFieldUi.Price,
    label: String,
    placeholder: String,
    isValid: Boolean,
    modifier: Modifier,
    onValueChanged: (String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextFieldValidation(
        value = inputField.value,
        onValueChange = onValueChanged,
        modifier = modifier.onFocusChanged { focusState ->
            val wasFocused = isFocused
            isFocused = focusState.isFocused

            if (wasFocused && !isFocused) {
                val formatted = inputField.formattedValue
                if (formatted != inputField.value) {
                    onValueChanged(formatted)
                }
            }
        },
        enabled = true,
        readOnly = false,
        label = label,
        placeholder = placeholder,
        errorText = if (!isValid) {
            when (val validationResult = inputField.validationResult) {
                is InputFieldValidationResult.Error -> validationResult.errorMessage
                InputFieldValidationResult.Success,
                InputFieldValidationResult.Uninitialized -> ""
            }
        } else "",
        isError = !isValid,
        visualTransformation = if (isFocused || inputField.value.isEmpty()) {
            VisualTransformation.None
        } else {
            VisualTransformationCurrencySuffix()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        )
    )
}

@ThemePreview
@Composable
private fun MarketplaceScreenSearchPreview() {
    MarvelXTheme {
        val comicsCategory = EnumGeneric(code = "comics", label = "Comic")

        MarketplaceScreen(
            onEvent = {},
            state = MarketplaceContract.State(
                isLoading = false,
                marketplaceUi = MarketplaceUi(
                    promo = null,
                    categories = emptyList(),
                ),
                mode = MarketplaceMode.SEARCH,
                listingsLayout = MarketplaceListingsLayout.LIST,
                searchUiState = UiState.Success(
                    data = persistentListOf(
                        FilteredListingUi(
                            listingId = "1",
                            category = comicsCategory,
                            condition = EnumGeneric(code = "good", label = "Good"),
                            title = "Amazing Spider-Man Vintage Comic #12",
                            price = "15.50".toBigDecimal()
                                .let { BigDecimalUi(value = it, label = it.formatPrice()) },
                            coverImageUrl = "",
                            details = ListingDetailsDomain.ComicDetailsDomain(
                                grade = EnumGeneric(code = "VF", label = "Very Fine"),
                                publisher = "Marvel",
                                year = 2015,
                            ),
                            finalSale = Random.nextBoolean(),
                            postedAt = DateUtil.getCurrentLocalDate()
                                .minusMonths(1)
                                .minusDays(3)
                                .let {
                                    LocalDateUi(
                                        value = it,
                                        label = it.toCustomFormat("dd/MM/YYYY")
                                    )
                                },
                            favorite = ToggleStateUi(isChecked = true),
                            cart = ToggleStateUi(isChecked = false),
                            comparison = true
                        ),
                        FilteredListingUi(
                            listingId = "2",
                            category = comicsCategory,
                            condition = EnumGeneric(code = "very_good", label = "Very Good"),
                            title = "Amazing Spider-Man Classic Comic #15",
                            price = "15.99".toBigDecimal()
                                .let { BigDecimalUi(value = it, label = it.formatPrice()) },
                            coverImageUrl = "",
                            details = ListingDetailsDomain.ComicDetailsDomain(
                                grade = EnumGeneric(code = "VF", label = "Very Fine"),
                                publisher = "Marvel",
                                year = 2015,
                            ),
                            finalSale = Random.nextBoolean(),
                            postedAt = DateUtil.getCurrentLocalDate()
                                .minusMonths(1)
                                .minusDays(5).let {
                                    LocalDateUi(
                                        value = it,
                                        label = it.toCustomFormat("dd/MM/YYYY")
                                    )
                                },
                            favorite = ToggleStateUi(isChecked = true),
                            cart = ToggleStateUi(isChecked = true),
                        ),
                    )
                ),
                searchQuery = "spi",
                activeSearchCriteria = MarketplaceSearchCriteriaUi(
                    categories = listOf(
                        SelectableOption(option = comicsCategory, isSelected = true)
                    )
                )
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigation = {},
            bottomNavigationBarData = { getBottomNavigationBarData(rememberNavController()) },
            snackbarEvents = SingleObserverFlow(emptyFlow()),
            messageBannerEvents = SingleObserverFlow(emptyFlow()),
        )
    }
}