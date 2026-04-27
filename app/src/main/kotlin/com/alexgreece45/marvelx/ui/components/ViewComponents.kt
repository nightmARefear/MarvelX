package com.alexgreece45.marvelx.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.primarySurface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.model.SnackbarStateData
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MAX_ITEMS
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MIN_ITEMS
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.constants.ConstantsZIndex
import com.alexgreece45.marvelx.util.extensions.ClickState
import com.alexgreece45.marvelx.util.extensions.applyPadding
import com.alexgreece45.marvelx.util.extensions.applyPaddingValues
import com.alexgreece45.marvelx.util.extensions.filterTouchesWhenObscured
import com.alexgreece45.marvelx.util.extensions.screenPaddingsNone
import com.alexgreece45.marvelx.util.extensions.throttledClickableIcon
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

// ======== START OF ========
//     PRIVATE COMPOSABLES
// ==========================

/**
 * A custom implementation of [IconButton] providing throttled clickable functionality to ensure
 * that this ThrottledIconButton is clicked only once per second, and also, protection is provided
 * against tap-jacking attacks from other applications.
 *
 * This component is typically used inside an App Bar for navigation icon / actions. It is strongly advised
 * to explicitly use this component as the App Bar's back IconButton, if it exists.
 *
 * ### Example usages:
 * #### 1. Back icon in App Bar for navigating to the previous screen
 * ```
 *ThrottledIconButton(
 *  onClick = navController.popBackStack()
 *) {
 *  Icon(
 *      imageVector = Icons.Filled.ArrowBack,
 *      contentDescription = stringResource(id = R.string.content_description_back)
 *  )
 *}
 * ```
 *
 * #### 2. Save action in App Bar for saving changes
 * ```
 *ThrottledIconButton(
 *  onClick = { viewModel.setEvent(Event.OnSaveChangesButtonClick) }
 *) {
 *  Icon(
 *      imageVector = Icons.Filled.Save,
 *      contentDescription = stringResource(id = R.string.save)
 *  )
 *}
 * ```
 *
 * @param onClick the lambda to be invoked when this icon is pressed
 * @param modifier optional [Modifier] for this ThrottledIconButton
 * @param enabled whether or not this ThrottledIconButton will handle input events and appear enabled for
 * semantics purposes
 * @param interactionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for this ThrottledIconButton. You can create and pass in your own remembered
 * [MutableInteractionSource] if you want to observe [Interaction]s and customize the
 * appearance / behavior of this ThrottledIconButton in different [Interaction]s.
 * @param content the content (icon) to be drawn inside the ThrottledIconButton. This is typically an
 * [Icon].
 * */
@Composable
fun ThrottledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .throttledClickableIcon(
                view = LocalView.current,
                interactionSource = interactionSource,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}

@Composable
private fun ThrottledBackHandler(
    enabled: Boolean,
    delay: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    onBack: () -> Unit
) {
    var lastClicked: Long by remember { mutableLongStateOf(0) }

    val debounceState = remember {
        MutableSharedFlow<ClickState>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(Unit) {
        debounceState
            .collect {
                if (lastClicked <= 0 || (it.currentTimeInMillis - lastClicked) >= delay) {
                    it.event()
                    lastClicked = it.currentTimeInMillis
                }
            }
    }

    if (enabled) {
        BackHandler(
            enabled = true, onBack = {
                debounceState.tryEmit(
                    ClickState(
                        onBack,
                        System.currentTimeMillis()
                    )
                )
            }
        )
    }
}

// ======== END OF ========
//     PRIVATE COMPOSABLES
// ==========================

data class BottomBarItem(
    val route: String,
    @StringRes val label: Int,
    val iconData: IconData,
)

data class BottomBarData(
    val navController: NavController,
    val items: List<BottomBarItem>,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val cutoutShape: Shape? = null,
    val elevation: Dp = AppBarDefaults.BottomAppBarElevation,
    val contentPadding: PaddingValues = AppBarDefaults.ContentPadding
)

data class BottomNavigationBarData(
    val navController: NavController,
    val items: List<BottomBarItem>,
    val backgroundColor: Color? = null,
    val topHorizontalColor: Color? = null,
    val selectedItemTopHorizontalColor: Color? = null,
    val elevation: Dp = BottomNavigationDefaults.Elevation,
)

/**
 * An extension of the [BottomAppBar] providing additional functionality.
 * */
@Composable
private fun BottomBar(
    navController: NavController,
    items: List<BottomBarItem>,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    cutoutShape: Shape? = null,
    elevation: Dp = AppBarDefaults.BottomAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding
) {
    if (items.size < 3) throw RuntimeException("The minimum allowed bottom bar actions is 3!")
    if (items.size > 5) throw RuntimeException("The maximum allowed bottom bar actions is 5!")

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        cutoutShape = cutoutShape,
        elevation = elevation,
        contentPadding = contentPadding
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.high,
            LocalTextStyle provides MaterialTheme.typography.caption
        ) {
            items.forEach { bottomBarAction ->
                IconButton(onClick = { navController.navigate(bottomBarAction.route) }) {
                    Column {
                        Icon(
                            painter = painterResource(bottomBarAction.iconData.iconResource),
                            contentDescription = stringResource(bottomBarAction.iconData.contentDescription),
                            tint = bottomBarAction.iconData.tint
                                ?: LocalContentColor.current.copy(
                                    alpha = LocalContentAlpha.current
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * An extension of [BottomNavigation] providing additional functionality.
 *
 * @param navController the [NavController] for navigating to other items
 * @param items the items of this bottom navigation bar
 * @param backgroundColor the background [Color] of all the items
 * @param topHorizontalColor the [Color] of the whole top horizontal axis of this bottom navigation bar
 * @param selectedItemTopHorizontalColor the [Color] of the top horizontal axis of the selected item
 * */
@Composable
private fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomBarItem>,
    backgroundColor: Color? = null,
    topHorizontalColor: Color? = null,
    selectedItemTopHorizontalColor: Color? = null,
    elevation: Dp = BottomNavigationDefaults.Elevation
) {
    if (items.size < BOTTOM_NAVIGATION_BAR_MIN_ITEMS) throw RuntimeException("The minimum allowed bottom navigation bar items is $BOTTOM_NAVIGATION_BAR_MIN_ITEMS!")
    if (items.size > BOTTOM_NAVIGATION_BAR_MAX_ITEMS) throw RuntimeException("The maximum allowed bottom navigation bar items is $BOTTOM_NAVIGATION_BAR_MAX_ITEMS!")
    if (items.any { stringResource(it.label).length > BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH }) throw RuntimeException(
        "The maximum allowed length for a bottom navigation bar's item label is $BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH characters."
    )

    BottomNavigation(
        backgroundColor = backgroundColor ?: MaterialTheme.colors.primarySurface,
        contentColor = if (backgroundColor == null) contentColorFor(MaterialTheme.colors.primarySurface) else contentColorFor(
            backgroundColor
        ),
        elevation = elevation
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high
                ) {
                    // TODO: make these values configurable
                    val topHorizontalStrokeWidth = 1.dp
                    val selectedItemTopHorizontalStrokeWidth = topHorizontalStrokeWidth + 2.dp
                    val selectedItemTopHorizontalVerticalOffset =
                        selectedItemTopHorizontalStrokeWidth - 0.75.dp
                    BottomNavigationItem(
                        modifier = Modifier
                            .then(
                                Modifier
                                    .drawWithContent {
                                        // Draws this item.
                                        drawContent()

                                        // Draws a horizontal line at the top of all the items.
                                        topHorizontalColor?.let { lineColor ->
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = topHorizontalStrokeWidth.toPx(),
                                                cap = StrokeCap.Butt
                                            )
                                        }

                                        // Draws a horizontal line at the top of the selected item.
                                        selectedItemTopHorizontalColor?.let { lineColor ->
                                            // TODO: Make these values configurable.
                                            val startPositionX = size.width / 33
                                            val endPositionX = size.width - startPositionX
                                            if (isSelected) {
                                                drawLine(
                                                    color = lineColor,
                                                    start = Offset(
                                                        x = startPositionX.toDp()
                                                            .toPx(), // Indent the start of the line by the 1/33th (3.3%) of the total length of the top horizontal line of the item.
                                                        y = selectedItemTopHorizontalVerticalOffset.toPx()
                                                    ),
                                                    end = Offset(
                                                        x = endPositionX.toDp()
                                                            .toPx(), // Indent the end of the line by total length of the top horizontal line of the item minus the start indent.
                                                        y = selectedItemTopHorizontalVerticalOffset.toPx()
                                                    ),
                                                    strokeWidth = selectedItemTopHorizontalStrokeWidth.toPx(),
                                                    cap = StrokeCap.Round
                                                )
                                            }
                                        }
                                    }
                            ),
                        icon = {
                            Icon(
                                painter = painterResource(item.iconData.iconResource),
                                contentDescription = stringResource(item.iconData.contentDescription),
                                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                                tint = item.iconData.tint
                                    ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.label),
                                maxLines = 1,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.1.sp
                                )
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            // Navigate to different route ONLY.
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    // TODO: Make the start destination configurable either by the provided value if it's not null, otherwise by the first item's route.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        selectedContentColor = MaterialTheme.customColors.genericRed,
                        unselectedContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium)
                    )
                }
            }
        }
    }
}

/**
 * Defines the required data to draw an icon.
 *
 * @param iconResource The resource ID of the icon.
 * @param contentDescription The resource ID of the content description of the icon.
 * @param tint The color to paint this icon.
 * @param enabled Whether the icon is enabled.
 */
data class IconData(
    @DrawableRes val iconResource: Int,
    @StringRes val contentDescription: Int,
    val tint: Color? = null,
    val enabled: Boolean = true
)

sealed class ScreenNavigableAction {
    /**
     * Indicates that the toolbar has no icon button at all.
     */
    data object None : ScreenNavigableAction()

    /**
     * Indicates that the toolbar has a `back` icon button, and its on-click functionality is
     * controlled by [onBack]; this action is most commonly used for navigating to the previous
     * screen.
     */
    data class Backable(val onBack: () -> Unit) : ScreenNavigableAction()

    /**
     * Indicates that the toolbar has a `cancel` icon button, and its on-click functionality is
     * controlled by [onClose]; this action is most commonly used for exiting from an
     * erroneous screen.
     */
    data class Closeable(val onClose: () -> Unit) : ScreenNavigableAction()
}

data class ToolbarAction(
    val label: String,
    val order: Int,
    val iconData: IconData,
    val onClick: () -> Unit
)

interface FabData

data class NormalFabData(
    val label: String = "",
    val iconLabel: String? = null,
    @DrawableRes val iconResource: Int? = null,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val onClick: () -> Unit
) : FabData

data class ExtendedFabData(
    val extended: Boolean,
    val normalFabData: NormalFabData
) : FabData

// ======== START OF ========
//        Generic Screens
// ==========================
/**
 * Base builder screen responsible for creating consistent UI layout screens throughout the application.
 *
 * @param toolbarActions The list of toolbar actions to display. [toolbarActionsContent] must be
 * `null`.
 * @param toolbarActionsContent The composable function to display as the toolbar actions.
 * [toolbarActions] must be `null`.
 * @param paddingValues The padding values to apply on top of the scaffold's ones. Provide a non-null
 * value to override the default padding values that are provided within this composable.
 */
@Composable
fun ContentScreen(
    isLoading: Boolean,
    navigableAction: ScreenNavigableAction = ScreenNavigableAction.None,
    onBack: (() -> Unit)? = null,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    toolbarActionsContent: (@Composable RowScope.() -> Unit)? = null,
    bottomBarData: BottomBarData? = null,
    bottomNavigationBarData: BottomNavigationBarData? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarStateData: SnackbarStateData? = null,
    fabData: FabData? = null,
    stickyBottom: @Composable (BoxScope.() -> Unit)? = null,
    paddingValues: PaddingValues? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    if (toolbarActions != null && toolbarActionsContent != null) {
        throw IllegalArgumentException("You cannot use `toolbarActions` and `toolbarActionsContent` at the same time. Set either argument.")
    }

    LaunchedEffect(snackbarStateData) {
        snackbarStateData?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                actionLabel = it.actionLabel,
                duration = it.duration
            )
        }
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            if (showToolbar) {
                Toolbar(
                    navigableAction = navigableAction,
                    toolbarTitle = toolbarTitle,
                    toolbarActions = if (!isLoading) toolbarActions else emptyList(),
                    toolbarActionsContent = toolbarActionsContent
                )
            }
        },
        bottomBar = {
            /*bottomBarData?.let { bottomBarData ->
                // Depending on whether the provided background color is null or not, call the BottomBar
                // composable with the appropriate parameters, since we cannot provide null as a color.
                bottomBarData.backgroundColor?.let { backgroundColor ->
                    // Provide the content color only if it's not null.
                    if (bottomBarData.contentColor != null) {
                        BottomBar(
                            navigator = bottomBarData.navigator,
                            items = bottomBarData.items,
                            backgroundColor = backgroundColor,
                            contentColor = bottomBarData.contentColor
                        )
                    } else {
                        BottomBar(
                            navigator = bottomBarData.navigator,
                            items = bottomBarData.items,
                            backgroundColor = backgroundColor
                        )
                    }
                } ?: run {
                    // Content color not is null, so provide the items and the content color.
                    if (bottomBarData.contentColor != null) {
                        BottomBar(
                            navigator = bottomBarData.navigator,
                            items = bottomBarData.items,
                            contentColor = bottomBarData.contentColor
                        )
                    } else {
                        // Content color is null, so only provide the items.
                        BottomBar(
                            navigator = bottomBarData.navigator,
                            items = bottomBarData.items
                        )
                    }
                }
            }*/

            bottomNavigationBarData?.let { bottomNavigationBarData ->
                BottomNavigationBar(
                    navController = bottomNavigationBarData.navController,
                    items = bottomNavigationBarData.items,
                    backgroundColor = bottomNavigationBarData.backgroundColor,
                    topHorizontalColor = bottomNavigationBarData.topHorizontalColor,
                    selectedItemTopHorizontalColor = bottomNavigationBarData.selectedItemTopHorizontalColor,
                    elevation = bottomNavigationBarData.elevation
                )
            }
        },
        floatingActionButton = {
            if (!isLoading) {
                fabData?.let { fabData ->
                    when (fabData) {
                        is NormalFabData -> {
                            NormalFloatingActionButton(normalFabData = fabData)
                        }

                        is ExtendedFabData -> {
                            CollapsibleExtendedFloatingActionButton(extendedFabData = fabData)
                        }
                    }
                }
            }
        }
    ) { scaffoldPaddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    LoadingScreen()
                }
                content(
                    if (paddingValues != null) scaffoldPaddingValues.applyPaddingValues(
                        paddingValues = paddingValues
                    ) else scaffoldPaddingValues.applyPadding()
                )
            }

            // Sticky bottom.
            stickyBottom?.let {
                if (!isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(ConstantsZIndex.STICKY_BOTTOM)
                            .padding(
                                if (paddingValues != null) scaffoldPaddingValues.applyPaddingValues(
                                    paddingValues = paddingValues
                                ) else scaffoldPaddingValues.applyPadding()
                            )
                    ) {
                        stickyBottom()
                    }
                }
            }
        }
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }
}

/**
 * A wrapper [Composable] for native [TopAppBar]. It offers additional functionality and custom
 * styling options.
 */
@Composable
private fun Toolbar(
    navigableAction: ScreenNavigableAction,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    toolbarActionsContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        contentPadding = screenPaddingsNone()
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Navigable action button; either `Close` or `Back`.
                if (navigableAction !is ScreenNavigableAction.None) {
                    ThrottledIconButton(
                        onClick = {
                            keyboard?.hide()
                            when (navigableAction) {
                                is ScreenNavigableAction.Closeable -> navigableAction.onClose()
                                is ScreenNavigableAction.Backable -> navigableAction.onBack()
                                else -> {}
                            }
                        }
                    ) {
                        Icon(
                            imageVector = when (navigableAction) {
                                is ScreenNavigableAction.Closeable -> Icons.Filled.Close
                                else -> Icons.AutoMirrored.Filled.ArrowBack
                            },
                            contentDescription = when (navigableAction) {
                                is ScreenNavigableAction.Closeable -> stringResource(R.string.content_description_close)
                                else -> stringResource(R.string.content_description_back)
                            }
                        )
                    }
                    HSpacer.Tiny()
                }

                when (toolbarTitle == null) {
                    true -> {
                        // Ensures that the toolbar actions are pinned to the right-most of the screen.
                        Box(modifier = Modifier.weight(1f))
                    }

                    false -> {
                        if (navigableAction is ScreenNavigableAction.None) {
                            HSpacer.Custom(DEFAULT_SCREEN_PADDING)
                        }

                        Text(
                            text = toolbarTitle,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = if (toolbarActions?.isEmpty() == true) SPACING_TINY.dp else 0.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.h5
                        )
                    }
                }

                toolbarActionsContent?.let { content -> content() }

                toolbarActions?.sortedBy { it.order }?.forEach { toolbarAction ->
                    IconButton(
                        onClick = toolbarAction.onClick,
                        enabled = toolbarAction.iconData.enabled
                    ) {
                        val iconTintAlpha =
                            if (toolbarAction.iconData.enabled) LocalContentAlpha.current else ContentAlpha.disabled
                        Icon(
                            painter = painterResource(toolbarAction.iconData.iconResource),
                            contentDescription = stringResource(toolbarAction.iconData.contentDescription),
                            modifier = Modifier.size(SPACING_LARGE.dp),
                            // TODO: To be reviewed.
                            tint = toolbarAction.iconData.tint?.copy(alpha = iconTintAlpha)
                                ?: LocalContentColor.current.copy(alpha = iconTintAlpha)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A [Composable] that sticks the [content] to the bottom of a screen. This function should be used
 * outside of [ContentScreen] only.
 *
 * @param paddingValues     The padding values to be applied to this composable. If a value isn't
 * provided, then all the sides will have a padding of `0.`[dp].
 * @param content           The content to be displayed.
 */
@Composable
fun StickyBottom(
    paddingValues: PaddingValues = screenPaddingsNone(),
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .zIndex(ConstantsZIndex.STICKY_BOTTOM)
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

/**
 * Defines the header section of the [ContentScreen]. This [Composable] is optional to use inside a
 * [ContentScreen].
 *
 * @param title        The title of the header.
 * @param subtitle     The subtitle of the header. It is optional.
 */
@Composable
fun ContentScreenHeader(
    title: String,
    subtitle: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title.
        Text(
            text = title,
            style = MaterialTheme.typography.h5
        )
        VSpacer.Large()

        // Subtitle.
        subtitle?.let {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
            )
        }
    }
}

@Composable
private fun NormalFloatingActionButton(
    normalFabData: NormalFabData
) {
    val backgroundColor = normalFabData.backgroundColor ?: MaterialTheme.colors.secondary

    FloatingActionButton(
        backgroundColor = backgroundColor,
        contentColor = normalFabData.contentColor
            ?: contentColorFor(backgroundColor = backgroundColor),
        onClick = { normalFabData.onClick.invoke() }
    ) {
        normalFabData.iconResource?.let { iconResource ->
            Icon(
                modifier = Modifier.size(SPACING_LARGE.dp),
                painter = painterResource(iconResource),
                contentDescription = normalFabData.iconLabel
            )
        }
    }
}

@Composable
private fun CollapsibleExtendedFloatingActionButton(
    extendedFabData: ExtendedFabData
) {
    val fabData = extendedFabData.normalFabData
    val backgroundColor = fabData.backgroundColor ?: MaterialTheme.colors.secondary

    // The default fab paddings as provided by the official implementation of the ExtendedFloatingActionButton.
    val extendedFabSize = 48.dp
    val extendedFabIconPadding = 12.dp
    val extendedFabTextPadding = 20.dp

    FloatingActionButton(
        onClick = fabData.onClick,
        modifier = Modifier.sizeIn(
            minWidth = extendedFabSize,
            minHeight = extendedFabSize
        ),
        backgroundColor = backgroundColor,
        contentColor = fabData.contentColor ?: contentColorFor(backgroundColor = backgroundColor)
    ) {
        val startPadding =
            if (fabData.iconResource == null) extendedFabIconPadding else extendedFabTextPadding
        Row(
            modifier = Modifier.padding(
                start = startPadding,
                end = extendedFabTextPadding
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fabData.iconResource?.let { iconResource ->
                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = fabData.iconLabel,
                    modifier = Modifier.size(SPACING_LARGE.dp)
                )
                // Apply horizontal padding only if the label is visible.
                if (extendedFabData.extended) {
                    HSpacer.Custom(width = extendedFabIconPadding)
                }
            }

            AnimatedVisibility(visible = extendedFabData.extended) {
                Text(
                    text = fabData.label,
                    style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchTerm: String,
    hint: String,
    backgroundColor: Color = MaterialTheme.colors.surface,
    textColor: Color = contentColorFor(backgroundColor),
    onValueChange: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = searchTerm,
        placeholder = {
            Text(
                text = hint,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.h6
            )
        },
        trailingIcon = {
            if (searchTerm.isNotBlank()) {
                val circleBackgroundColor = MaterialTheme.colors.background
                IconButton(
                    onClick = onCloseClick
                ) {
                    Icon(
                        modifier = Modifier
                            .size(SPACING_LARGE.dp)
                            .drawBehind {
                                drawCircle(
                                    color = circleBackgroundColor,
                                    radius = this.size.maxDimension.div(2) + SPACING_SMALL
                                )
                            },
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.content_description_close),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        },
        onValueChange = { newSearchTerm -> onValueChange(newSearchTerm) },
        keyboardOptions = KeyboardOptions(

        ),
        singleLine = true,
        shape = CircleShape,
        textStyle = MaterialTheme.typography.h6,
        colors = TextFieldDefaults.textFieldColors(
            textColor = textColor,
            backgroundColor = backgroundColor,
            focusedIndicatorColor = MaterialTheme.customColors.transparent,
            unfocusedIndicatorColor = MaterialTheme.customColors.transparent,
            disabledIndicatorColor = MaterialTheme.customColors.transparent,
            errorIndicatorColor = MaterialTheme.customColors.transparent,
            placeholderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )
    )
}

/**
 * Pull-to-refresh functionality. This [Composable] is used in scrollable lists where if you pull at
 * the top of the list, the items refresh.
 *
 * @param onRefresh     The action(s) to be invoked when the pull-to-refresh functionality has been
 * invoked.
 * @param isRefreshing  Defines whether the [content] is in a loading state.
 * @param content       The content to display when [isRefreshing] is `false`.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshable(
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        content()

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(
                Alignment.TopCenter
            ),
            contentColor = MaterialTheme.customColors.genericRed
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheetScaffold(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    sheetContent: @Composable () -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    sheetGesturesEnabled: Boolean = true,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = BottomSheetScaffoldDefaults.SheetElevation,
    content: @Composable (PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        modifier = modifier,
        topBar = {
            if (showToolbar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    contentPadding = PaddingValues(0.dp).applyPadding()
                ) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        LocalTextStyle provides MaterialTheme.typography.h6
                    ) {
                        onBack?.let {
                            IconButton(onClick = { it.invoke() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.content_description_back),
                                )
                            }
                        }

                        toolbarTitle?.let { toolbarTitle ->
                            Text(
                                modifier = Modifier.weight(1f),
                                text = toolbarTitle,
                                maxLines = 1
                            )
                        }

                        toolbarActions?.sortedBy { it.order }?.forEach { toolbarAction ->
                            IconButton(onClick = { toolbarAction.onClick() }) {
                                Icon(
                                    painter = painterResource(toolbarAction.iconData.iconResource),
                                    contentDescription = stringResource(toolbarAction.iconData.contentDescription),
                                    tint = toolbarAction.iconData.tint
                                        ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                                )
                            }
                        }
                    }
                }
            }
        },
        sheetContent = { sheetContent() },
        scaffoldState = bottomSheetScaffoldState,
        sheetGesturesEnabled = sheetGesturesEnabled,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content(paddingValues/*.applyPadding()*/)
        }
    }
}

@Composable
fun LoadingScreen() {
    LottieComposable(
        resourceId = R.raw.loading_animation,
        size = 140.dp,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(ConstantsZIndex.LOADING_SCREEN),
    ) {
        Text(
            text = stringResource(R.string.loading),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
fun ItemDescription(
    description: String,
    emptyDescriptionMessage: String
) {
    if (description.isBlank()) {
        Text(
            text = emptyDescriptionMessage,
            style = MaterialTheme.typography.body1.copy(
                color = if (MaterialTheme.colors.isLight) Color.Gray
                else LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
                fontStyle = FontStyle.Italic
            )
        )
    } else {
        Text(
            text = description,
            style = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.Justify
            )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextFieldValidation(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String = "",
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.medium
) {
    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue != value) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.caption
                )
            },
            placeholder = {
                placeholder?.let { placeholder ->
                    Text(
                        text = placeholder,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape
        )

        // Error
        AnimatedVisibility(
            visible = isError && errorMessage.isNotBlank(),
            modifier = Modifier.padding(start = SPACING_SMALL.dp, top = SPACING_TINY.dp),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

/**
 * Simple Exposed Dropdown menu constructed using [ExposedDropdownMenuBox], [OutlinedTextField], and
 * [DropdownMenu]. Avoid using [ExposedDropdownMenuBox]. Use this wrapper instead. This is required
 * since the native [ExposedDropdownMenuBox] does not provide an implementation for the
 * [OutlinedTextField], nor the [ExposedDropdownMenu].
 *
 * @param label             A placeholder text when no item has been selected.
 * @param expanded          Indicates if the dropdown menu is visible.
 * @param modifier          The modifier to be applied.
 * @param value             The currently selected value.
 * @param maxLines          The maximum number of lines that [value] can be displayed to; set to `1`
 * by default.
 * @param onDismissRequest  The action(s) to invoke when the dropdown menu is expanded, and the user
 * clicks outside of it.
 * @param onExpandedChange  The action(s) to invoke when the user clicks on this dropdown menu.
 * @param content           The content to be shown when the dropdown menu is visible.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExposedDropdownMenu(
    label: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    value: String? = "",
    enabled: Boolean = true,
    maxLines: Int = 1,
    onDismissRequest: (() -> Unit)? = null,
    onExpandedChange: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var outlinedTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange?.invoke() },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = TextFieldValue(
                text = value.orEmpty()
            ),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    outlinedTextFieldSize = coordinates.size.toSize()
                },
            enabled = enabled,
            readOnly = true,
            label = {
                Text(
                    text = label
                )
            },
            placeholder = {
                placeholder?.let {
                    Text(text = placeholder)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            maxLines = maxLines
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismissRequest?.invoke() },
            modifier = Modifier.width(with(density) { outlinedTextFieldSize.width.toDp() }),
            content = content
        )
    }
}

/**
 * A simple composable button. Avoid using the native [Button]. Use this wrapper instead as it
 * provides custom padding if none has been specified, multiple-clicks protection, and UI overlay
 * attack protection.
 */
@Composable
fun WrapButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    throttleClicks: Boolean = true,
    throttleDuration: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    var lastClicked by remember { mutableLongStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val debounceState = remember {
        MutableSharedFlow<Long>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(Unit) {
        debounceState.collect { currentTime ->
            if (currentTime - lastClicked >= throttleDuration) {
                onClick()
                lastClicked = currentTime
            }
        }
    }

    Button(
        onClick = {
            if (throttleClicks) {
                coroutineScope.launch {
                    debounceState.emit(System.currentTimeMillis())
                }
            } else {
                onClick()
            }
        },
        modifier = modifier.filterTouchesWhenObscured(LocalView.current),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * A simple composable outlined button. Avoid using the native [OutlinedButton]. Use this wrapper
 * instead as it provides custom padding and multiple clicks protection.
 */
@Composable
fun WrapOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    throttleClicks: Boolean = true,
    throttleDuration: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    WrapButton(
        onClick = onClick,
        modifier = modifier,
        throttleClicks = throttleClicks,
        throttleDuration = throttleDuration,
        enabled = enabled,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * A wrapper [Composable] for the native [TextButton]. Use this implementation instead as it
 * provides custom padding, and multiple clicks protection.
 */
@Composable
fun WrapTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    throttleClicks: Boolean = true,
    throttleDuration: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    WrapButton(
        onClick = onClick,
        modifier = modifier,
        throttleClicks = throttleClicks,
        throttleDuration = throttleDuration,
        enabled = enabled,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}


/**
 * The button to be used in [AlertDialogError].
 *
 * @param label The text label of the button.
 * @param buttonType The button type. For the dismiss button, the type is always
 * [ErrorButtonType.Text].
 * @param onClick Callback to be invoked when the button is clicked.
 */
data class AlertDialogButton(
    val label: String,
    val buttonType: ErrorButtonType = ErrorButtonType.defaultType,
    val onClick: () -> Unit
)

/**
 * A wrapper for [AlertDialog] providing additional functionality and custom styling. Avoid using the
 * native one. Use this wrapper instead.
 *
 * @param isVisible         Defines whether this alert is visible.
 * @param title             The title of this dialog.
 * @param errorMessage      The error message of this dialog.
 * @param dismissButton     The dismiss button of this dialog. Can be `null`.
 * @param confirmButton     The confirm button of this dialog. It is always required.
 * @param shape             The shape of this dialog. It has a default value of
 * [MaterialTheme.shapes.medium].
 * @param backgroundColor   The background color of this dialog. It has a default value of
 * [MaterialTheme.colors.surface].
 * @param contentColor      The content color of this dialog. Has a default color based on the
 * [backgroundColor].
 * @param properties        The dialog properties of this dialog. It has a default value of
 * [DialogProperties].
 * @param onDismissRequest  The action(s) to be invoked when the user clicks outside of this dialog.
 */
@Composable
fun AlertDialogError(
    isVisible: Boolean,
    title: String,
    errorMessage: @Composable () -> Unit,
    dismissButton: AlertDialogButton? = null,
    confirmButton: AlertDialogButton,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: (() -> Unit)? = null
) {
    if (!isVisible) return
    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6 // Overrides the default style of "subtitle1".
            )
        },
        text = {
            // Overrides the provided content alpha of "ContentAlpha.medium".
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.high,
                LocalTextStyle provides MaterialTheme.typography.body1 // Overrides the provided style of "body2".
            ) {
                errorMessage()
            }
        },
        dismissButton = {
            dismissButton?.let { button ->
                WrapTextButton(onClick = button.onClick) {
                    Text(
                        text = button.label
                    )
                }
            }
        },
        confirmButton = {
            confirmButton.let { button ->
                when (button.buttonType) {
                    ErrorButtonType.Contained -> {
                        WrapButton(onClick = button.onClick) {
                            Text(
                                text = button.label
                            )
                        }
                    }

                    ErrorButtonType.Outlined -> {
                        WrapOutlinedButton(onClick = button.onClick) {
                            Text(
                                text = button.label
                            )
                        }
                    }

                    ErrorButtonType.Text -> {
                        WrapTextButton(onClick = button.onClick) {
                            Text(
                                text = button.label
                            )
                        }
                    }
                }
            }
        },
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        properties = properties,
        onDismissRequest = { onDismissRequest?.invoke() }
    )
}

// ======== END OF ========
//        Generic Screens
// ==========================

sealed class SortingType(val order: Int, val label: String) {

    data object Random : SortingType(order = 0, label = "Random")
    data object TextAscending : SortingType(order = 1, label = "A-Z Asc")

    data object TextDescending : SortingType(order = 2, label = "A-Z Desc")

    companion object {
        fun asList(): List<SortingType> = listOf(Random, TextAscending, TextDescending)

        val DEFAULT_SORTING_TYPE: SortingType = Random
    }
}

@Composable
fun SortingItems(
    startPadding: Dp = 0.dp,
    selectedItem: SortingType,
    onItemClick: (SortingType) -> Unit
) {
    SortingType.asList().forEachIndexed { index, sortingType ->
        val isSelected = selectedItem == sortingType

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = startPadding)
                // Disables the on tap ripple effect for both single and long clicks.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onItemClick(sortingType)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = Icons.Filled.Check.name,
                    tint = MaterialTheme.colors.onBackground
                )
            } else {
                // Placeholder for space between the icon and the labels; this ensures that all the labels are perfectly vertically aligned.
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.Transparent
                )
            }

            HSpacer.Small()

            Text(
                text = sortingType.label,
                style = MaterialTheme.typography.h6,
                color = if (isSelected) LocalContentColor.current else LocalContentColor.current.copy(
                    alpha = 0.6f
                )
            )
        }

        // Adds vertical space at the bottom of each item, except for the last one.
        if (index != SortingType.asList().lastIndex) VSpacer.Medium()
    }
}

@Composable
fun SortingModalBottomSheetContent(
    contentPadding: Dp = SPACING_SMALL.dp,
    selectedItem: SortingType,
    onItemClick: (SortingType) -> Unit,
    onCloseClick: () -> Unit
) {
    val circleBackgroundColor = MaterialTheme.colors.background.copy(
        alpha = 0.7f
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(contentPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.h4
            )

            IconButton(onClick = onCloseClick) {
                Icon(
                    modifier = Modifier
                        .size(SPACING_LARGE.dp)
                        .drawBehind {
                            drawCircle(
                                color = circleBackgroundColor,
                                radius = this.size.maxDimension.div(2) + SPACING_MEDIUM
                            )
                        },
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close)
                )
            }
        }

        VSpacer.Medium()

        SortingItems(
            startPadding = 10.dp,
            selectedItem = selectedItem
        ) { sortingType ->
            onItemClick(sortingType)
        }
    }
}

@Composable
fun GenericInfoScreen(
    title: String,
    titleColor: Color = Color.Unspecified,
    buttonLabel: String,
    modifier: Modifier,
    onButtonClicked: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = titleColor
            )
        )

        WrapButton(onClick = onButtonClicked) {
            Text(
                text = buttonLabel,
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable
fun LazyItemScope.GenericLazyItemInfoScreen(
    title: String,
    titleColor: Color = Color.Unspecified,
    buttonLabel: String,
    onButtonClicked: () -> Unit
) {
    GenericInfoScreen(
        title = title,
        titleColor = titleColor,
        buttonLabel = buttonLabel,
        modifier = Modifier.fillParentMaxSize(),
        onButtonClicked = onButtonClicked
    )
}

// ======== START OF ========
//        Error Screens
// ==========================
@Composable
fun GenericErrorScreen(
    errorMessage: String,
    modifier: Modifier,
    onRetryClick: () -> Unit
) {
    GenericInfoScreen(
        title = errorMessage,
        titleColor = MaterialTheme.colors.error,
        buttonLabel = stringResource(R.string.generic_retry_button),
        modifier = modifier,
        onButtonClicked = onRetryClick
    )
}

@Composable
fun LazyItemScope.LazyItemErrorScreen(
    errorMessage: String,
    modifier: Modifier,
    onRetryClick: () -> Unit
) {
    GenericErrorScreen(
        errorMessage = errorMessage,
        modifier = modifier,
        onRetryClick = onRetryClick
    )
}

@Composable
fun LazyItemScope.LazyItemContentErrorScreen(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    GenericErrorScreen(
        errorMessage = errorMessage,
        modifier = Modifier.fillParentMaxSize(),
        onRetryClick = onRetryClick
    )
}

@Composable
fun LazyItemScope.NoInternetErrorScreen(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection_error_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.error
            )
        )

        VSpacer.Medium()

        Button(
            onClick = { onRetryClick() }
        ) {
            Text(
                text = stringResource(R.string.generic_retry_button).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

// ======== END OF ========
//        Error Screens
// ==========================

/**
 * Detects scrolling up motion in a [LazyGrid]. Returns `true` if detected, otherwise `false`.
 */
@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableIntStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) {
        mutableIntStateOf(firstVisibleItemScrollOffset)
    }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableIntStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) {
        mutableIntStateOf(firstVisibleItemScrollOffset)
    }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
