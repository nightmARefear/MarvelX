package com.alexgreece45.marvelx.ui.screens.mainActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.notifications.constants.ConstantsNotifications
import com.alexgreece45.marvelx.notifications.model.NotificationChannelData
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.notifications.model.NotificationData
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.NavBackStackLogger
import com.alexgreece45.marvelx.ui.navigation.NavGraph
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.AllCharactersRoute
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation.AllFavoriteCharactersRoute
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.navigation.CheckoutSuccessRoute
import com.alexgreece45.marvelx.ui.screens.cart.navigation.CartRoute
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation.AllCharacterComicsRoute
import com.alexgreece45.marvelx.ui.screens.character.navigation.CharacterRoute
import com.alexgreece45.marvelx.ui.screens.comic.navigation.ComicRoute
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEvent
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.CreateListingWizardRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.CreateListingSuccessRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.ListingDetailsRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.MarketplaceRoute
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.navigation.OrderDetailsRoute
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.OrderItemManagementRoute
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.OrderItemTrackingHistoryRoute
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.RequestItemReturnRoute
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.navigateToRequestItemReturnScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.navigation.RequestItemReturnSuccessRoute
import com.alexgreece45.marvelx.ui.screens.profile.navigation.ProfileRoute
import com.alexgreece45.marvelx.ui.screens.splash.SplashContract.AlertDialogErrorType.InsecureApplication
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.util.SystemBarsThemeUtil
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import com.chuckerteam.chucker.api.Chucker
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.time.Duration.Companion.milliseconds

internal class MainActivity : AppCompatActivity() {

    private val loggerTag = "MainActivity"

    private val appLogger: AppLogger by inject()
    private val securityConfigLogic: SecurityConfigLogic by inject()
    private val viewModel: MainActivityViewModel by viewModels()
    private val gson: Gson by inject()

    private val navBackStackLogger: NavBackStackLogger by inject()

    var pendingDeepLinkAction: DeepLinkAction? = null

    /**
     * Indicates whether the navigation flow has started. It has the initial value of `false`.
     */
    var flowStarted: Boolean = false

    @SuppressLint("RestrictedApi")
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appLogger.d(loggerTag) { "onCreate called." }

        setWindowSecurityFlags()

        setContent {
            val navController = rememberNavController()
            flowStarted = true

            LaunchedEffect(Unit) {
                viewModel.setEvent(MainActivityEvent.OnFlowStarted)
            }

            DisposableEffect(Unit) {
                val destinationChangedListener =
                    NavController.OnDestinationChangedListener { _, destination, _ ->
                        viewModel.setEvent(MainActivityEvent.OnDestinationChanged(destination.route.orEmpty()))
                    }

                navController.addOnDestinationChangedListener(destinationChangedListener)

                onDispose {
                    navController.removeOnDestinationChangedListener(destinationChangedListener)
                }
            }

            navBackStackLogger.TrackBackStack(navController = navController, showRouteId = false)

            MarvelXTheme {
                SystemBarsThemeUtil.applyDarkSystemBarsTheme(activity = this)

                Surface(color = MaterialTheme.colors.background) {
                    NavGraph(navController = navController)

                    // TODO: To be removed.
                    /*Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(
                            onClick = {
//                            navController.navigate(Screen.OrderManagementScreen.route)
//                            return@IconButton
                                navController.navigateToRequestItemReturnScreen(
                                    orderId = "1",
                                    orderItemId = "B28"
                                )
//                                navController.navigateToRequestItemReturnSuccessScreen(orderItemReturnId = "RET-1003")
                            }, modifier = Modifier
                                .size(32.dp)
                                .clip(shape = CircleShape)
                                .background(Color.Cyan)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BugReport,
                                contentDescription = null,
                                tint = MaterialTheme.customColors.white
                            )
                        }
                    }*/

                    ChuckerPermissions()

                    OneTimeLaunchedEffect {
                        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnCreate)
                    }

                    ObserveUiEffect(flow = viewModel.uiEffect, tag = "MainActivity") { effect ->
                        when (effect) {
                            is MainActivityEffect.Navigation.NavigateToHomeScreen -> {
                                // TODO: Resolve.
//                                NavArgHelper.setArgument(
//                                    NavArg.HomeAlertDialogErrorType,
//                                    HomeAlertDialogErrorType.SessionExpired
//                                )
                                navController.navigate(Screen.HomeScreen.route) {
                                    popUpToTop(navController)
                                }
                            }

                            is MainActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation -> {
                                NavArgHelper.setArgument(
                                    NavArg.SplashAlertDialogErrorType,
                                    InsecureApplication(
                                        securityViolation = effect.securityViolation
                                    )
                                )
                                navController.navigate(Screen.SplashScreen.route) {
                                    popUpToTop(navController)
                                }
                            }

                            is MainActivityEffect.OpenOrCacheDeepLink -> {
                                if (canCurrentRouteRunDeepLink(navController)) {
                                    appLogger.d(loggerTag) { "opening deep link..." }
                                    navController.openDeepLinkAction(effect.deepLinkAction)
                                } else {
                                    appLogger.d(loggerTag) { "caching deep link..." }
                                    pendingDeepLinkAction = effect.deepLinkAction
                                }
                            }

                            is MainActivityEffect.ShowNotification -> {
                                showFcmNotification(effect.notificationData, this@MainActivity)
                            }
                        }
                    }

                    ShouldAppDrawOverlayWindows()

                    RetrieveFcmToken(
                        onFcmTokenReceived = {
                            viewModel.setEvent(MainActivityEvent.OnFcmTokenReceived(it))
                        }
                    )

                    registerNotificationChannels()
                }
            }
        }
        handleDeepLink(intent)
    }

    override fun onPause() {
        super.onPause()

        appLogger.d(loggerTag) { "onPause called." }
        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnPause)
    }

    override fun onResume() {
        super.onResume()

        appLogger.d(loggerTag) { "onResume called." }
        if (flowStarted) {
            viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnResume)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        appLogger.d(loggerTag) { "onDestroy called." }
        viewModel.setEvent(MainActivityEvent.LifecycleEvent.OnDestroy(willBeRecreated = !isFinishing))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        appLogger.d(loggerTag) { "onUserInteraction called." }
        viewModel.setEvent(MainActivityEvent.OnUserInteraction)
    }

    override fun isInMultiWindowMode(): Boolean {
        appLogger.d(loggerTag) { "isInMultiWindowMode called." }

        val isInMultiWindowMode = super.isInMultiWindowMode()
        if (isInMultiWindowMode && !securityConfigLogic.applicationMultiWindowMode) {
            viewModel.setEvent(MainActivityEvent.ApplicationInMultiWindowModeViolation)
        }

        return isInMultiWindowMode
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (flowStarted) {
            handleDeepLink(intent)
        } else {
            delayDeepLinkHandling(intent)
        }
    }

    @Composable
    private fun RetrieveFcmToken(onFcmTokenReceived: (String?) -> Unit) {
        OneTimeLaunchedEffect {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    try {
                        appLogger.d(loggerTag) { "Retrieved FCM token: ${task.result}." }

                        onFcmTokenReceived(task.result)
                    } catch (e: Exception) {
                        appLogger.e(loggerTag) { "Failed to get FCM token:\n" + e.localizedMessage.orEmpty() }
                    }
                }.addOnFailureListener { _ ->
                    onFcmTokenReceived(null)
                }
            } catch (e: Exception) {
                appLogger.e(loggerTag) { "Failed to get FCM token:\n" + e.localizedMessage.orEmpty() }
                onFcmTokenReceived(null)
            }
        }
    }

    /**
     * Registers all the notification channels.
     */
    private fun registerNotificationChannels() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val allChannelsData = NotificationChannelData.getAllChannels()

        val channels = allChannelsData.map { channelData ->
            NotificationChannel(
                channelData.id,
                channelData.name,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
        }

        // Deletes nonexistent channels.
        val allChannelIds = allChannelsData.map { it.id }
        notificationManager.notificationChannels.forEach { notificationChannel ->
            val notificationChannelId = notificationChannel.id
            if (notificationChannelId !in allChannelIds) {
                notificationManager.deleteNotificationChannel(notificationChannel.id)
            }
        }

        // Adds channels.
        notificationManager.createNotificationChannels(channels)
    }

    private fun showFcmNotification(notificationData: NotificationData, context: Context) {
        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                ConstantsNotifications.DEEP_LINK_ACTION,
                notificationData.getStringifiedDeepLinkAction(gson)
            )
        }

        val requestCode = DateUtil.getEpochSeconds().toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(context, notificationData.channelType.channelData.id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationData.title)
                .setContentText(notificationData.body)
                .apply {
                    val imageBytes = notificationData.imageBytes

                    if (imageBytes != null) {
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        setLargeIcon(bitmap)
                        setStyle(
                            NotificationCompat.BigPictureStyle()
                                .setBigContentTitle(notificationData.title)
                                .setSummaryText(notificationData.body)
                                .bigPicture(bitmap)
                                .bigLargeIcon(null as Bitmap?)
                        )
                    } else {
                        setStyle(
                            NotificationCompat.BigTextStyle()
                                .setBigContentTitle(notificationData.title)
                                .bigText(notificationData.body)
                        )
                    }
                }
                .setShowWhen(true)
                .setWhen(DateUtil.getEpochMillis())
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .apply {
                    setPriority(
                        when (notificationData.channelType) {
                            NotificationChannelType.AcceptFriendRequest -> NotificationCompat.PRIORITY_LOW
                            NotificationChannelType.IncomingFriendRequest -> NotificationCompat.PRIORITY_HIGH
                            NotificationChannelType.ListingPurchase -> NotificationCompat.PRIORITY_MAX
                        }
                    )
                }
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationData.notificationId, notification)
    }

    /**
     * Delays the handling of the deep link if the navigation graph has not been set yet.
     */
    private fun delayDeepLinkHandling(intent: Intent) {
        lifecycleScope.launch {
            val maxCount = 20
            var count = 0
            while (!flowStarted && count <= maxCount) {
                count++
                delay(250L.milliseconds)
            }

            // Ensures that no more than 5 seconds have elapsed.
            if (count <= maxCount) {
                handleDeepLink(intent)
            }
        }
    }

    /**
     * Sets security flags for the [Window] of [MainActivity].
     */
    private fun setWindowSecurityFlags() {
        if (securityConfigLogic.blockScreenCapture) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        @SuppressLint("MissingPermission", "NewApi")
        if (securityConfigLogic.hideOverlayWindows) {
            window.setHideOverlayWindows(true)
        }
    }

    /**
     * Checks whether this application, through [SecurityConfigLogic.applicationDrawOverlayWindows]
     * should have the ability to draw overlay windows over other applications, and if it doesn't it
     * checks if the application has this ability. If it has it, then
     * [MainActivityEvent.ApplicationDrawOverlayWindowsViolation] is set.
     */
    @Composable
    private fun ShouldAppDrawOverlayWindows() {
        if (!securityConfigLogic.applicationDrawOverlayWindows) {
            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (Settings.canDrawOverlays(this@MainActivity)) {
                            viewModel.setEvent(MainActivityEvent.ApplicationDrawOverlayWindowsViolation)
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the user has the "POST_NOTIFICATIONS" permission, and if not, requests it, so that
     * [Chucker] can be installed if the app is debuggable.
     *
     * ### Note: Without this composable, [Chucker] cannot be installed as it is necessary that the app has the "POST_NOTIFICATIONS" permission.
     */
    @Composable
    private fun ChuckerPermissions() {
        if (Chucker.isOp
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    /**
     * Handles the received deep link.
     */
    private fun handleDeepLink(intent: Intent?) {
        appLogger.d(loggerTag) { "(handleDeepLink) triggered." }

        intent?.let {
            // FCM handling.
            intent.getStringExtra(ConstantsNotifications.DEEP_LINK_ACTION)?.let { deepLinkAction ->
                viewModel.setEvent(
                    MainActivityEvent.OnFcmDeepLinkActionReceived(
                        deepLinkAction
                    )
                )
            }

            // External URL handling.
            it.data?.let { uri ->
                viewModel.setEvent(MainActivityEvent.OnDeepLinkReceived(uri))
            }
        }
    }

    private val typeSafeRoutesIncludedInAutoLogout = listOf(
        MarketplaceRoute::class,
        ListingDetailsRoute::class,
        CreateListingWizardRoute::class,
        CreateListingSuccessRoute::class,
        CharacterRoute::class,
        CartRoute::class,
        CheckoutSuccessRoute::class,
        OrderDetailsRoute::class,
        OrderItemTrackingHistoryRoute::class,
        OrderItemManagementRoute::class,
        RequestItemReturnRoute::class,
        AllCharactersRoute::class,
        ComicRoute::class,
        AllCharactersRoute::class,
        AllCharacterComicsRoute::class,
        AllFavoriteCharactersRoute::class,
        ProfileRoute::class,
        RequestItemReturnRoute::class,
        RequestItemReturnSuccessRoute::class,
    )

    /**
     * Returns `true` if the current route is included in the auto-logout mechanism, otherwise
     * `false`.
     */
    private fun isCurrentRouteIncludedInAutoLogout(navController: NavHostController): Boolean {
        val isCurrentRouteIncludedInAutoLogout =
            typeSafeRoutesIncludedInAutoLogout.any { routeClass ->
                navController.currentDestination?.hasRoute(
                    routeClass
                ) == true
            }
        if (isCurrentRouteIncludedInAutoLogout) {
            return true
        }

        return Screen.fromRoute(navController.currentDestination?.route.orEmpty())?.isIncludedInAutoLogout
            ?: false
    }

    /**
     * Examines whether the current route can open a deep link and returns the result as a [Boolean].
     */
    private fun canCurrentRouteRunDeepLink(navController: NavHostController): Boolean =
        flowStarted && isCurrentRouteIncludedInAutoLogout(navController)
}