package com.alexgreece45.marvelx.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
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
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.primarySurface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MAX_ITEMS
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MIN_ITEMS
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.STICKY_BOTTOM_Z_INDEX
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.ClickState
import com.alexgreece45.marvelx.util.extensions.applyPadding
import com.alexgreece45.marvelx.util.extensions.screenPaddingsNone
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import com.alexgreece45.marvelx.util.extensions.throttledClickableIcon
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

// ======== START OF ========
//     PRIVATE COMPOSABLES
// ==========================

/**
 * A custom implementation of [IconButton] providing throttled clickable functionality to ensure that
 * this ThrottledIconButton is clicked only once per second.
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
private fun ThrottledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .throttledClickableIcon(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource
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
                            tint = bottomBarAction.iconData.colorTint
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
fun BottomNavigationBar(
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
                                                        .toPx(),// Indent the end of the line by total length of the top horizontal line of the item minus the start indent.
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
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(item.iconData.iconResource),
                            contentDescription = stringResource(item.iconData.contentDescription),
                            tint = item.iconData.colorTint
                                ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.label)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        // Navigate to different route ONLY.
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // TODO: Make the start destination configurable either by the provided value if it's not null, otherwise by the first item's route.
                                popUpTo(Screen.BottomNavigationScreens.getStartDestination()) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    selectedContentColor = MaterialTheme.colors.primaryVariant,
                    unselectedContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}

data class IconData(
    @DrawableRes val iconResource: Int,
    @StringRes val contentDescription: Int,
    val colorTint: Color? = null,
    val enabled: Boolean = true
)

sealed class ScreenNavigableAction() {
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

@Composable
fun ContentScreen(
    isLoading: Boolean,
    navigableAction: ScreenNavigableAction = ScreenNavigableAction.None,
    onBack: (() -> Unit)? = null,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    bottomBarData: BottomBarData? = null,
    bottomNavigationBarData: BottomNavigationBarData? = null,
    fabData: FabData? = null,
    stickyBottom: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showToolbar) {
                Toolbar(
                    navigableAction = navigableAction,
                    toolbarTitle = toolbarTitle,
                    toolbarActions = toolbarActions
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
            // Display FAB only when loading is false.
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
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    LoadingScreen()
                }
                content(paddingValues.applyPadding())
            }

            // Sticky bottom.
            stickyBottom?.let {
                if (!isLoading) {
                    Box(
                        modifier = Modifier
                            .zIndex(STICKY_BOTTOM_Z_INDEX)
                            .padding(paddingValues.applyPadding())
                            .fillMaxWidth()
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
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Toolbar(
    navigableAction: ScreenNavigableAction,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        contentPadding = screenPaddingsNone()
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            // Navigable action button; either `Close` or `Back`.
            if (navigableAction !is ScreenNavigableAction.None) {
                ThrottledIconButton(
                    onClick = {
                        keyboard?.hide()
                        when (navigableAction) {
                            is ScreenNavigableAction.Closeable -> navigableAction.onClose.invoke()
                            is ScreenNavigableAction.Backable -> navigableAction.onBack.invoke()
                            else -> {}
                        }
                    }
                ) {
                    Icon(
                        imageVector = when (navigableAction) {
                            is ScreenNavigableAction.Closeable -> Icons.Filled.Close
                            else -> Icons.Filled.ArrowBack
                        },
                        contentDescription = when (navigableAction) {
                            is ScreenNavigableAction.Closeable -> stringResource(R.string.content_description_close)
                            else -> stringResource(R.string.content_description_back)
                        }
                    )
                }
                HSpacer.Medium()
            }

            toolbarTitle?.let { toolbarTitle ->
                if (navigableAction is ScreenNavigableAction.None) {
                    HSpacer.Custom(DEFAULT_SCREEN_PADDING)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = toolbarTitle,
                    maxLines = 1,
                    style = MaterialTheme.typography.h5
                )
            }

            toolbarActions?.sortedBy { it.order }?.forEach { toolbarAction ->
                IconButton(
                    onClick = { toolbarAction.onClick() },
                    enabled = toolbarAction.iconData.enabled
                ) {
                    val iconTintAlpha =
                        if (toolbarAction.iconData.enabled) LocalContentAlpha.current else ContentAlpha.disabled
                    Icon(
                        modifier = Modifier.size(SPACING_LARGE.dp),
                        painter = painterResource(toolbarAction.iconData.iconResource),
                        contentDescription = stringResource(toolbarAction.iconData.contentDescription),
                        // TODO: To be reviewed.
                        tint = toolbarAction.iconData.colorTint?.copy(alpha = iconTintAlpha)
                            ?: LocalContentColor.current.copy(alpha = iconTintAlpha)
                    )
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
                .zIndex(STICKY_BOTTOM_Z_INDEX)
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
 * @param subtitle     The subtitle of the header.
 */
@Composable
fun ContentScreenHeader(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title.
        Text(
            text = title,
            style = MaterialTheme.typography.h4
        )
        VSpacer.Large()

        // Subtitle.
        Text(
            text = subtitle,
            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
        )
    }
}

@Composable
fun NormalFloatingActionButton(
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
fun CollapsibleExtendedFloatingActionButton(
    extendedFabData: ExtendedFabData
) {
    val fabData = extendedFabData.normalFabData
    val backgroundColor = fabData.backgroundColor ?: MaterialTheme.colors.secondary

    // The default fab paddings as provided by the official implementation of the ExtendedFloatingActionButton.
    val extendedFabSize = 48.dp
    val extendedFabIconPadding = 12.dp
    val extendedFabTextPadding = 20.dp

    FloatingActionButton(
        modifier = Modifier.sizeIn(
            minWidth = extendedFabSize,
            minHeight = extendedFabSize
        ),
        backgroundColor = backgroundColor,
        contentColor = fabData.contentColor ?: contentColorFor(backgroundColor = backgroundColor),
        onClick = { fabData.onClick() }
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
                    modifier = Modifier.size(SPACING_LARGE.dp),
                    painter = painterResource(iconResource),
                    contentDescription = fabData.iconLabel
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
                style = MaterialTheme.typography.h6,
                fontStyle = FontStyle.Italic
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
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            placeholderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheet(
    sheetState: ModalBottomSheetState,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        sheetContent = { sheetContent() },
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        scrimColor = scrimColor,
    ) {
        content()
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
                                    tint = toolbarAction.iconData.colorTint
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
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = R.raw.loading_animation
        )
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = Modifier.size(140.dp),
                composition = composition,
                progress = { progress }
            )

            Text(
                text = stringResource(R.string.loading),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4,
            )
        }
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

@Composable
fun OutlinedTextFieldValidation(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.medium
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = { newValue ->
                if (newValue != value) {
                    onValueChange(newValue)
                }
            },
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
            interactionSource = interactionSource,
            shape = shape
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomStart),
            visible = isError && errorMessage.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 66.dp),
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
 * @param modifier          The modifier to be applied.
 * @param expanded          Indicates if the dropdown menu is visible.
 * @param value             The currently selected value.
 * @param label             A placeholder text when no item has been selected.
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
    modifier: Modifier = Modifier,
    expanded: Boolean,
    value: String? = "",
    label: String,
    maxLines: Int = 1,
    onDismissRequest: (() -> Unit)? = null,
    onExpandedChange: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onExpandedChange?.invoke() },
    ) {
        OutlinedTextField(
            value = TextFieldValue(
                text = value.orEmpty()
            ),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = label
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            maxLines = maxLines
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { onDismissRequest?.invoke() }
        ) {
            content()
        }
    }
}

/**
 * A simple composable button. Avoid using the native [Button]. Use this wrapper instead
 * as it provides custom padding and multiple clicks protection.
 */
@Composable
fun ContainedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.throttledClickable(delay = 1500L, onClick = onClick),
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 32.dp),
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
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.throttledClickable(delay = 1500L, onClick = onClick),
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 32.dp),
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
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.throttledClickable(delay = 1500L, onClick = onClick),
        enabled = enabled,
        content = content
    )
}

sealed class ErrorButtonType {
    data object Text : ErrorButtonType()

    data object Outlined : ErrorButtonType()

    data object Contained : ErrorButtonType()

    companion object {
        val defaultType: ErrorButtonType = Contained
    }
}


/**
 * @param buttonType The button type. For the dismiss button, the type is
 * always [ErrorButtonType.Text].
 * */
data class AlertDialogButton(
    val value: String,
    val buttonType: ErrorButtonType = ErrorButtonType.defaultType,
    val onClick: () -> Unit
)

/**
 * A wrapper for [AlertDialog] providing additional functionality and custom styling. Avoid using the
 * native one. Use this wrapper instead.
 *
 * @param modifier          The modifier to be applied.
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
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    title: String,
    errorMessage: String,
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
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6 // Overrides the default style of "subtitle1".
            )
        },
        text = {
            // Overrides the provided content alpha of "ContentAlpha.medium".
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.high
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.body1 // Overrides the provided style of "body2".
                )
            }
        },
        dismissButton = {
            dismissButton?.let { button ->
                WrapTextButton(onClick = button.onClick) {
                    Text(
                        text = button.value
                    )
                }
            }
        },
        confirmButton = {
            confirmButton.let { button ->
                when (button.buttonType) {
                    ErrorButtonType.Contained -> {
                        ContainedButton(onClick = button.onClick) {
                            Text(
                                text = button.value
                            )
                        }
                    }

                    ErrorButtonType.Outlined -> {
                        WrapOutlinedButton(onClick = button.onClick) {
                            Text(
                                text = button.value
                            )
                        }
                    }

                    ErrorButtonType.Text -> {
                        WrapTextButton(onClick = button.onClick) {
                            Text(
                                text = button.value
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
                    indication = rememberRipple(radius = 0.dp)
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

            IconButton(
                onClick = { onCloseClick() }
            ) {
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

// ======== START OF ========
//        Error Screens
// ==========================
@Composable
fun LazyItemScope.ContentErrorScreen(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
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
 * Detects scrolling up motion in a [LazyColumn]. Returns `true` if detected, otherwise `false`.
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