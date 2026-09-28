package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavDeepLinkItem
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.google.gson.Gson
import org.koin.core.annotation.Single

interface DeepLinkController {
    /**
     * Handles the given [deepLinkUri] and if it's valid it returns the corresponding [DeepLinkAction].
     * This function is used for handling web redirects.
     */
    suspend fun handleDeepLinkUri(deepLinkUri: Uri): HandleDeepLinkActionResult

    /**
     * Attempts to match the given [webUrl] to a deep link action and return it. Otherwise, an error
     * is returned. This function is used for handling QR code web urls.
     */
    suspend fun handleQrCodeWebUrl(webUrl: String): HandleQrCodeWebUrlResult

    /**
     * Attempts to extract data of type [dataType] from the given [deepLinkAction]. If the operation
     * fails, an error is returned instead.
     */
    suspend fun extractDataFromDeepLinkAction(
        deepLinkAction: DeepLinkAction,
        dataType: Any
    ): ExtractDataFromDeepLinkAction

    /**
     * Attempts to create a [DeepLinkAction] based on the given arguments.
     */
    fun createDeepLinkAction(
        screenRoute: String,
        deepLinkMap: Map<NavDeepLinkItem, String>
    ): CreateDeepLinkActionResult
}

@Single
class DeepLinkControllerImpl(
    private val appLogger: AppLogger,
    private val gson: Gson,
) : DeepLinkController {

    private val loggerTag = "DeepLinkController"

    override suspend fun handleDeepLinkUri(deepLinkUri: Uri): HandleDeepLinkActionResult {
        parseDeepLinkUri(deepLinkUri).let { parseDeepLinkUriResult ->
            return when (parseDeepLinkUriResult) {
                ParseDeepLinkUriResult.Failed -> {
                    HandleDeepLinkActionResult.Failed
                }

                is ParseDeepLinkUriResult.Success -> {
                    validateDeepLinkAction(parseDeepLinkUriResult.deepLinkAction).let {
                        when (it) {
                            ValidateDeepLinkActionResult.Failed -> {
                                HandleDeepLinkActionResult.Failed
                            }

                            is ValidateDeepLinkActionResult.Success -> {
                                HandleDeepLinkActionResult.Success(it.deepLinkAction)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun handleQrCodeWebUrl(webUrl: String): HandleQrCodeWebUrlResult {
        if (!webUrl.startsWith(DeepLinkWebUrl.WEB_BASE_URL)) {
            return HandleQrCodeWebUrlResult.InvalidUrl
        }
        val deepLinkWebUrl = DeepLinkWebUrl.asList()
            .firstOrNull { webUrl.replace(DeepLinkWebUrl.WEB_BASE_URL, "").startsWith(it.suffix) }
        if (deepLinkWebUrl == null) {
            return HandleQrCodeWebUrlResult.InvalidUrl
        }

        // *** Construct deep link action. ***

        // Isolate parameters from web URL.
        val isolatedParameters =
            webUrl.replace(DeepLinkWebUrl.WEB_BASE_URL, "").replace(deepLinkWebUrl.webUrl, "")

        // Extract parameter values.
        val parameterValues = isolatedParameters.split("/")

        // TODO: Create util function to handle the full deep link creation flow (validation + creation).
        when (deepLinkWebUrl) {
            DeepLinkWebUrl.ProfileDetails -> {
                if (parameterValues.isEmpty()) {
                    return HandleQrCodeWebUrlResult.InvalidUrl
                }

                // User ids are strings (e.g. numeric or UUIDs), so only reject blank ids or ids
                // containing characters other than letters, digits, '-' and '_'.
                val userId = parameterValues.last()
                if (userId.isBlank() || !userId.all { character -> character.isLetterOrDigit() || character == '-' || character == '_' }) {
                    appLogger.e(loggerTag) { "(handleQrCodeWebUrl) The parsed userId \"$userId\" is not valid!" }
                    return HandleQrCodeWebUrlResult.InvalidUrl
                }

                return when (
                    val result = createDeepLinkAction(
                        screenRoute = deepLinkWebUrl.screenBaseRoute,
                        deepLinkMap = mapOf(
                            Pair(NavDeepLinkItem.UserId, userId)
                        )
                    )
                ) {
                    is CreateDeepLinkActionResult.Success -> HandleQrCodeWebUrlResult.Success(
                        result.deepLinkAction
                    )

                    CreateDeepLinkActionResult.Failed -> HandleQrCodeWebUrlResult.ErrorCreatingDeepLinkAction
                }
            }

            DeepLinkWebUrl.ListingDetails -> {
                if (parameterValues.isEmpty()) {
                    return HandleQrCodeWebUrlResult.InvalidUrl
                }

                val listingId =
                    parameterValues.last().filter { character -> character.isLetterOrDigit() }
                if (listingId.isBlank()) {
                    return HandleQrCodeWebUrlResult.InvalidUrl
                }

                return when (
                    val result = createDeepLinkAction(
                        screenRoute = deepLinkWebUrl.screenBaseRoute,
                        deepLinkMap = mapOf(
                            Pair(NavDeepLinkItem.ListingId, listingId)
                        )
                    )
                ) {
                    is CreateDeepLinkActionResult.Success -> HandleQrCodeWebUrlResult.Success(
                        result.deepLinkAction
                    )

                    CreateDeepLinkActionResult.Failed -> HandleQrCodeWebUrlResult.ErrorCreatingDeepLinkAction
                }
            }
        }
    }

    override suspend fun extractDataFromDeepLinkAction(
        deepLinkAction: DeepLinkAction,
        dataType: Any
    ): ExtractDataFromDeepLinkAction {
        return try {
            val json = gson.toJson(deepLinkAction.data)
            val extractedData = gson.fromJson(json, dataType::class.java)
            ExtractDataFromDeepLinkAction.Success(extractedData)
        } catch (_: Exception) {
            ExtractDataFromDeepLinkAction.Failed
        }
    }

    override fun createDeepLinkAction(
        screenRoute: String,
        deepLinkMap: Map<NavDeepLinkItem, String>
    ): CreateDeepLinkActionResult {
        val deepLinkArgKeys = getDeepLinkArgKeys(screenRoute)
        if (deepLinkArgKeys == null) {
            appLogger.e(loggerTag) { "(createDeepLinkAction) The provided $screenRoute does not match a deep-link supported screen." }
            return (CreateDeepLinkActionResult.Failed)
        }
        val doDeepLinkArgsMatch = deepLinkMap.keys.map { it.key } == deepLinkArgKeys
        if (!doDeepLinkArgsMatch) {
            appLogger.e(loggerTag) { "(createDeepLinkAction) The provided deep-link arguments do not match the ones of $screenRoute." }
            return (CreateDeepLinkActionResult.Failed)
        }

        // We can now safely create the corresponding deep link action.
        val stringifiedMap = mutableMapOf<String, Any>().apply {
            deepLinkMap.forEach { (deepLinkItem, data) ->
                put(deepLinkItem.key, data)
            }
        }

        val deepLinkAction = DeepLinkAction(
            screenRoute = screenRoute,
            data = stringifiedMap
        )

        return CreateDeepLinkActionResult.Success(deepLinkAction)
    }

    /**
     * Attempts to parse the given [uri], convert it into a [DeepLinkAction] and return it.
     * If this operation fails, then an error is returned.
     */
    private fun parseDeepLinkUri(uri: Uri): ParseDeepLinkUriResult {
        return try {
            val deepLinkAction = gson.fromJson(uri.pathSegments[1], DeepLinkAction::class.java)
            ParseDeepLinkUriResult.Success(deepLinkAction)
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "(parseDeepLinkUri) An error occurred whilst parsing the received deep link URI:\n ${e.message.orEmpty()}" }
            ParseDeepLinkUriResult.Failed
        }
    }

    /**
     * Returns the deep link argument keys, in URI order, of the deep-link supported screen named
     * [screenRoute], or `null` if there's none. Type-safe routes aren't [Screen]s, so the ones that
     * web links and QR codes open are listed here.
     */
    private fun getDeepLinkArgKeys(screenRoute: String): List<String>? =
        Screen.matchDeepLinkScreenRoute(screenRoute)?.deepLinkArgs?.map { it.key }
            ?: when (screenRoute) {
                DeepLinkWebUrl.ListingDetails.screenBaseRoute -> listOf(NavDeepLinkItem.ListingId.key)
                else -> null
            }

    /**
     * Validates the given [deepLinkAction] based on whether the screen route matches a deep-link
     * supported screen and the arguments' names are exactly the same.
     */
    private fun validateDeepLinkAction(deepLinkAction: DeepLinkAction): ValidateDeepLinkActionResult {
        return when (val deepLinkArgKeys = getDeepLinkArgKeys(deepLinkAction.screenRoute)) {
            null -> {
                appLogger.e(loggerTag) { "(handleDeepLinkAction) No deep link supported screen name \"${deepLinkAction.screenRoute}\" was found." }
                ValidateDeepLinkActionResult.Failed
            }

            else -> {
                // Check if the required data is present.
                if (deepLinkAction.data.keys.toList() == deepLinkArgKeys) {
                    ValidateDeepLinkActionResult.Success(deepLinkAction)
                } else {
                    appLogger.e(loggerTag) { "(handleDeepLinkAction) Data is missing from the deep link." }
                    ValidateDeepLinkActionResult.Failed
                }
            }
        }
    }
}

private sealed interface ParseDeepLinkUriResult {
    data class Success(val deepLinkAction: DeepLinkAction) : ParseDeepLinkUriResult
    data object Failed : ParseDeepLinkUriResult
}

private sealed interface ValidateDeepLinkActionResult {
    data class Success(val deepLinkAction: DeepLinkAction) : ValidateDeepLinkActionResult
    data object Failed : ValidateDeepLinkActionResult
}

sealed interface HandleDeepLinkActionResult {
    data class Success(val deepLinkAction: DeepLinkAction) : HandleDeepLinkActionResult
    data object Failed : HandleDeepLinkActionResult
}

sealed interface HandleQrCodeWebUrlResult {
    data class Success(val deepLinkAction: DeepLinkAction) : HandleQrCodeWebUrlResult
    data object InvalidUrl : HandleQrCodeWebUrlResult
    data object ErrorCreatingDeepLinkAction : HandleQrCodeWebUrlResult
}

sealed interface ExtractDataFromDeepLinkAction {
    data class Success(val extractedData: Any) : ExtractDataFromDeepLinkAction
    data object Failed : ExtractDataFromDeepLinkAction
}

sealed interface CreateDeepLinkActionResult {
    data class Success(val deepLinkAction: DeepLinkAction) : CreateDeepLinkActionResult
    data object Failed : CreateDeepLinkActionResult
}
