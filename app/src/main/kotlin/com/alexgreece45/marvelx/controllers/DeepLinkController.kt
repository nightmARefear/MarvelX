package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.NavDeepLinkItem
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

interface DeepLinkController {
    /**
     * Handles the given [deepLinkUri] and if it's valid it returns the corresponding [DeepLinkAction].
     * This function is used for handling web redirects.
     */
    suspend fun handleDeepLinkUri(deepLinkUri: Uri): Flow<HandleDeepLinkActionResult>

    /**
     * Attempts to match the given [webUrl] to a deep link action and return it. Otherwise, an error
     * is returned. This function is used for handling QR code web urls.
     */
    suspend fun handleQrCodeWebUrl(webUrl: String): Flow<HandleQrCodeWebUrlResult>

    /**
     * Attempts to extract data of type [dataType] from the given [deepLinkAction]. If the operation
     * fails, an error is returned instead.
     */
    suspend fun extractDataFromDeepLinkAction(
        deepLinkAction: DeepLinkAction,
        dataType: Any
    ): Flow<ExtractDataFromDeepLinkAction>

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

    override suspend fun handleDeepLinkUri(deepLinkUri: Uri) = flow {
        parseDeepLinkUri(deepLinkUri).collect { parseDeepLinkUriResult ->
            when (parseDeepLinkUriResult) {
                ParseDeepLinkUriResult.Failed -> {
                    emit(HandleDeepLinkActionResult.Failed)
                }

                is ParseDeepLinkUriResult.Success -> {
                    validateDeepLinkAction(parseDeepLinkUriResult.deepLinkAction).collect {
                        when (it) {
                            ValidateDeepLinkActionResult.Failed -> {
                                emit(HandleDeepLinkActionResult.Failed)
                            }

                            is ValidateDeepLinkActionResult.Success -> {
                                emit(HandleDeepLinkActionResult.Success(it.deepLinkAction))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun handleQrCodeWebUrl(webUrl: String): Flow<HandleQrCodeWebUrlResult> = flow {
        if (!webUrl.startsWith(DeepLinkWebUrl.WEB_BASE_URL)) {
            emit(HandleQrCodeWebUrlResult.InvalidUrl)
            return@flow
        }
        val deepLinkWebUrl = DeepLinkWebUrl.asList()
            .firstOrNull { webUrl.replace(DeepLinkWebUrl.WEB_BASE_URL, "").startsWith(it.suffix) }
        if (deepLinkWebUrl == null) {
            emit(HandleQrCodeWebUrlResult.InvalidUrl)
            return@flow
        }

        // *** Construct deep link action. ***

        // Isolate parameters from web URL.
        val isolatedParameters =
            webUrl.replace(DeepLinkWebUrl.WEB_BASE_URL, "").replace(deepLinkWebUrl.webUrl, "")

        // Extract parameter values.
        val parameterValues = isolatedParameters.split("/")

        when (deepLinkWebUrl) {
            DeepLinkWebUrl.ProfileDetails -> {
                if (parameterValues.isEmpty()) {
                    emit(HandleQrCodeWebUrlResult.InvalidUrl)
                    return@flow
                }
                val userId = try {
                    Integer.parseInt(parameterValues.last()).toString()
                } catch (_: NumberFormatException) {
                    appLogger.e("DeepLinkController") { "(handleQrCodeWebUrl) The parsed userId \"${parameterValues.last()}\" is not of type Int!" }
                    emit(HandleQrCodeWebUrlResult.InvalidUrl)
                    return@flow
                }

                when (
                    val result = createDeepLinkAction(
                        screenRoute = deepLinkWebUrl.screenBaseRoute,
                        deepLinkMap = mapOf(
                            Pair(NavDeepLinkItem.UserId, userId)
                        )
                    )
                ) {
                    is CreateDeepLinkActionResult.Success -> emit(
                        HandleQrCodeWebUrlResult.Success(
                            result.deepLinkAction
                        )
                    )

                    CreateDeepLinkActionResult.Failed -> emit(HandleQrCodeWebUrlResult.ErrorCreatingDeepLinkAction)
                }
            }

            DeepLinkWebUrl.ListingDetails -> TODO("Not implemented yet.")
        }
    }

    override suspend fun extractDataFromDeepLinkAction(
        deepLinkAction: DeepLinkAction,
        dataType: Any
    ): Flow<ExtractDataFromDeepLinkAction> =
        flow {
            try {
                val json = gson.toJson(deepLinkAction.data)
                val extractedData = gson.fromJson(json, dataType::class.java)
                emit(ExtractDataFromDeepLinkAction.Success(extractedData))
            } catch (_: Exception) {
                emit(ExtractDataFromDeepLinkAction.Failed)
            }
        }

    override fun createDeepLinkAction(
        screenRoute: String,
        deepLinkMap: Map<NavDeepLinkItem, String>
    ): CreateDeepLinkActionResult {
        val screen = Screen.matchDeepLinkScreenRoute(screenRoute)
        if (screen == null) {
            appLogger.e("DeepLinkController") { "(createDeepLinkAction) The provided $screenRoute does not match a deep-link supported screen." }
            return (CreateDeepLinkActionResult.Failed)
        }
        val doDeepLinkArgsMatch = Screen.doDeepLinkArgsMatch(screen, deepLinkMap.keys.toList())
        if (!doDeepLinkArgsMatch) {
            appLogger.e("DeepLinkController") { "(createDeepLinkAction) The provided deep-link arguments do not match the ones of $screenRoute." }
            return (CreateDeepLinkActionResult.Failed)
        }

        // We can now safely create the corresponding deep link action.
        val stringifiedMap = mutableMapOf<String, Any>().apply {
            deepLinkMap.forEach { (deepLinkItem, data) ->
                put(deepLinkItem.key, data)
            }
        }

        return (
                CreateDeepLinkActionResult.Success(
                    deepLinkAction = DeepLinkAction(
                        screenRoute = screenRoute,
                        data = stringifiedMap
                    )
                )
                )
    }

    /**
     * Attempts to parse the given [uri], convert it into a [DeepLinkAction] and return it.
     * If this operation fails, then an error is returned.
     */
    private fun parseDeepLinkUri(uri: Uri): Flow<ParseDeepLinkUriResult> = flow {
        try {
            val deepLinkAction = gson.fromJson(uri.pathSegments[1], DeepLinkAction::class.java)
            emit(ParseDeepLinkUriResult.Success(deepLinkAction))
        } catch (e: Exception) {
            appLogger.e("DeepLinkController") { "(parseDeepLinkUri) An error occurred whilst parsing the received deep link URI:\n ${e.message.orEmpty()}" }
            emit(ParseDeepLinkUriResult.Failed)
        }
    }

    /**
     * Validates the given [deepLinkAction] based on whether the screen route matches a deep-link
     * supported screen and the arguments' names are exactly the same.
     */
    private fun validateDeepLinkAction(deepLinkAction: DeepLinkAction): Flow<ValidateDeepLinkActionResult> =
        flow {
            when (val screen = Screen.matchDeepLinkScreenRoute(deepLinkAction.screenRoute)) {
                null -> {
                    appLogger.e("DeepLinkController") { "(handleDeepLinkAction) No deep link supported screen name \"${deepLinkAction.screenRoute}\" was found." }
                    emit(ValidateDeepLinkActionResult.Failed)
                }

                else -> {
                    // Check if the required data is present.
                    if (deepLinkAction.data.keys.toList() == screen.deepLinkArgs.map { it.key }) {
                        emit(ValidateDeepLinkActionResult.Success(deepLinkAction))
                    } else {
                        appLogger.e("DeepLinkController") { "(handleDeepLinkAction) Data is missing from the deep link." }
                        emit(ValidateDeepLinkActionResult.Failed)
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
