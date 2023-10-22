package com.alexgreece45.marvelx.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
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
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.primarySurface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MAX_ITEMS
import com.alexgreece45.marvelx.util.BOTTOM_NAVIGATION_BAR_MIN_ITEMS
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.applyPadding

data class BottomBarAction(
    val route: String,
    @StringRes val label: Int,
    val iconData: IconData,
) {
    companion object {
        fun getDefaultBottomBarActions(): List<BottomBarAction> = listOf(
            BottomBarAction(
                route = Screen.BottomNavigationScreens.AllCharactersScreen.route,
                label = R.string.bottom_navigation_label_characters,
                iconData = IconData(
                    iconResource = R.drawable.ic_superhero_32,
                    contentDescription = R.string.bottom_navigation_label_characters
                )
            ),
            BottomBarAction(
                route = Screen.BottomNavigationScreens.AllFavoriteCharactersScreen.route,
                label = R.string.bottom_navigation_label_favorites,
                iconData = IconData(
                    iconResource = R.drawable.ic_favorite_filled_24,
                    contentDescription = R.string.bottom_navigation_label_favorites
                )
            ),
            BottomBarAction(
                route = Screen.BottomNavigationScreens.ProfileScreen.route,
                label = R.string.bottom_navigation_label_profile,
                iconData = IconData(
                    iconResource = R.drawable.ic_person_32,
                    contentDescription = R.string.bottom_navigation_label_profile
                )
            )
        )
    }
}

data class BottomBarData(
    val navController: NavController,
    val items: List<BottomBarAction>,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val cutoutShape: Shape? = null,
    val elevation: Dp = AppBarDefaults.BottomAppBarElevation,
    val contentPadding: PaddingValues = AppBarDefaults.ContentPadding
)

data class BottomNavigationBarData(
    val navController: NavController,
    val items: List<BottomBarAction>,
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
    items: List<BottomBarAction>,
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
    items: List<BottomBarAction>,
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

        items.forEach { bottomBarAction ->
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.high
            ) {
                // TODO: make these values configurable
                val topHorizontalStrokeWidth = 1.dp

                val selectedItemTopHorizontalStrokeWidth = topHorizontalStrokeWidth + 2.dp
                val selectedItemTopHorizontalVerticalOffset = topHorizontalStrokeWidth + 1.dp
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
                                        if (currentRoute == bottomBarAction.route) {
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(
                                                    0f,
                                                    selectedItemTopHorizontalVerticalOffset.toPx()
                                                ),
                                                end = Offset(
                                                    size.width,
                                                    selectedItemTopHorizontalVerticalOffset.toPx()
                                                ),
                                                strokeWidth = selectedItemTopHorizontalStrokeWidth.toPx(),
                                                cap = StrokeCap.Butt
                                            )
                                        }
                                    }
                                }
                        ),
                    icon = {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(bottomBarAction.iconData.iconResource),
                            contentDescription = stringResource(bottomBarAction.iconData.contentDescription),
                            tint = bottomBarAction.iconData.colorTint
                                ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(bottomBarAction.label),
                            style = MaterialTheme.typography.caption
                        )
                    },
                    selected = currentRoute == bottomBarAction.route,
                    onClick = {
                        // Navigate ONLY to different route.
                        if (currentRoute != bottomBarAction.route) {
                            navController.navigate(bottomBarAction.route) {
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
    onBack: (() -> Unit)? = null,
    isLoading: Boolean,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    toolbarActions: List<ToolbarAction>? = null,
    bottomBarData: BottomBarData? = null,
    bottomNavigationBarData: BottomNavigationBarData? = null,
    fabData: FabData? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showToolbar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    contentPadding = PaddingValues(
                        top = 0.dp,
                        end = DEFAULT_SCREEN_PADDING,
                        bottom = 0.dp,
                        start = DEFAULT_SCREEN_PADDING
                    )
                ) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        LocalTextStyle provides MaterialTheme.typography.h6
                    ) {
                        onBack?.let {
                            IconButton(onClick = { it.invoke() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back_32),
                                    contentDescription = stringResource(R.string.back),
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
                            IconButton(
                                onClick = { toolbarAction.onClick() },
                                enabled = toolbarAction.iconData.enabled
                            ) {
                                Icon(
                                    modifier = Modifier.size(SPACING_LARGE.dp),
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
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                LoadingScreen()
            }
            content(paddingValues.applyPadding())
        }
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
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.h6,
        LocalContentAlpha provides ContentAlpha.high
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchTerm,
            placeholder = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.h6, // TODO: may need to be removed due to compositionlocalprovider
                    fontStyle = FontStyle.Italic
                )
            },
            trailingIcon = {
                if (searchTerm.isNotBlank()) {
                    val circleBackgroundColor = MaterialTheme.colors.background
                    IconButton(
                        onClick = { onCloseClick() }
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
                            painter = painterResource(R.drawable.ic_close_32),
                            contentDescription = stringResource(R.string.close),
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
            colors = TextFieldDefaults.textFieldColors(
                textColor = textColor,
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                placeholderColor = MaterialTheme.colors.onBackground.copy(
                    alpha = LocalContentAlpha.current.minus(
                        0.3f
                    )
                )
            )
        )
    }
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
                                    painter = painterResource(R.drawable.ic_back_32),
                                    contentDescription = stringResource(R.string.back),
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

sealed interface ValueType {
    object Text : ValueType

    object Email : ValueType

    data class Password(
        val isPasswordVisible: Boolean = false,
        val onVisibilityIconClick: () -> Unit
    ) : ValueType
}

@Composable
fun OutlinedTextFieldValidation(
    modifier: Modifier = Modifier,
    value: String,
    valueType: ValueType = ValueType.Text,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String = "",
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small
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
            trailingIcon = {
                /** Default trailing Icon color as defined in [TextFieldDefaults.textFieldColors].
                 *  It should be used by [ValueType.Password] only, as the trailing icon's color
                 *  is changed if an error has occurred.
                 * */
                val defaultIconColor =
                    MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)

                when (valueType) {
                    ValueType.Text, ValueType.Email -> {
                        if (isError)
                            Icon(
                                modifier = Modifier.size(SPACING_LARGE.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.ic_error_32),
                                contentDescription = stringResource(R.string.error),
                            )
                    }

                    is ValueType.Password -> {
                        IconButton(onClick = { valueType.onVisibilityIconClick() }) {
                            if (valueType.isPasswordVisible) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = stringResource(R.string.is_password_visible_content_description),
                                    tint = defaultIconColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = stringResource(R.string.is_password_not_visible_content_description),
                                    tint = defaultIconColor
                                )
                            }
                        }
                    }
                }
            },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = when (valueType) {
                ValueType.Text -> {
                    keyboardOptions
                }

                ValueType.Email -> {
                    keyboardOptions.copy(
                        keyboardType = KeyboardType.Email
                    )
                }

                is ValueType.Password -> {
                    // Restricts the implementation so as to avoid exposing the password in the user's keyboard.
                    keyboardOptions.copy(
                        keyboardType = KeyboardType.Password,
                    )
                }
            },
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
                modifier = Modifier.padding(start = SPACING_SMALL.dp, top = 68.dp),
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
fun ContainedButton(
    modifier: Modifier,
    enabled: Boolean = true,
    label: String,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        onClick = { onClick.invoke() }
    ) {
        Text(
            text = label
        )
    }
}

sealed class AlertDialogErrorButtonType {
    object Text : AlertDialogErrorButtonType()

    object Outlined : AlertDialogErrorButtonType()

    object Contained : AlertDialogErrorButtonType()

    companion object {
        fun getDefaultType(): AlertDialogErrorButtonType = Text
    }
}


/**
 * @param buttonType The button type. For the dismiss button, the type is
 * always [AlertDialogErrorButtonType.Text].
 * */
data class AlertDialogButton(
    val value: String,
    val buttonType: AlertDialogErrorButtonType = AlertDialogErrorButtonType.getDefaultType(),
    val onClick: () -> Unit
)

/**
 * @param isVisible if set to true the Alert Dialog will appear on the screen, otherwise it will be invincible
 * @param title the title of the Alert Dialog
 * @param errorMessage the error message of the Alert Dialog
 * */
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
    if (isVisible) {
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
                    TextButton(onClick = { button.onClick() }) {
                        Text(
                            text = button.value
                        )
                    }
                }
            },
            confirmButton = {
                confirmButton.let { button ->
                    when (button.buttonType) {
                        AlertDialogErrorButtonType.Contained -> {
                            Button(onClick = { button.onClick() }) {
                                Text(
                                    text = button.value
                                )
                            }
                        }

                        AlertDialogErrorButtonType.Outlined -> {
                            OutlinedButton(onClick = { button.onClick() }) {
                                Text(
                                    text = button.value
                                )
                            }
                        }

                        AlertDialogErrorButtonType.Text -> {
                            TextButton(onClick = { button.onClick() }) {
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
            onDismissRequest = { onDismissRequest?.invoke() },
        )
    }
}

// ======== END OF ========
//        Generic Screens
// ==========================

sealed class SortingType(val order: Int, val label: String) {

    object Random : SortingType(order = 0, label = "Random")
    object TextAscending : SortingType(order = 1, label = "A-Z Asc")

    object TextDescending : SortingType(order = 2, label = "A-Z Desc")

    companion object {
        fun asList(): List<SortingType> = listOf(Random, TextAscending, TextDescending)

        fun getDefaultSortingType(): SortingType = Random
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
                // Disables the on tap ripple effect for both simple and long clicks.
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
                    alpha = LocalContentAlpha.current.minus(0.3f)
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
                    painter = painterResource(R.drawable.ic_close_32),
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
fun LazyItemScope.GenericErrorScreen(
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
                text = stringResource(R.string.retry).uppercase(),
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
                text = stringResource(R.string.retry).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable
fun GenericErrorScreen(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(MaterialTheme.colors.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
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
                text = stringResource(R.string.retry).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable
fun NoInternetErrorScreen(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(MaterialTheme.colors.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
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
                text = stringResource(R.string.retry).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

// ======== END OF ========
//        Error Screens
// ==========================

/*
@Composable
fun LazyStaggeredGridItemScope.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) {
        mutableStateOf(firstVisibleItemScrollOffset)
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
}*/

@Composable
fun LazyStaggeredGridState.isScrollingUp(): Boolean {
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
