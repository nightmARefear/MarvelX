package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketConnectionState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketInbound
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketTerminationCode
import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.config.WebsocketConfigLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.di.model.WebsocketOkHttpClient
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketChatReadPayload
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketEnvelope
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketErrorCode
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketErrorPayload
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketFrameType
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketNotificationDeletePayload
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketTimestampRequestPayload
import com.alexgreece45.marvelx.core.network.model.websocket.WebsocketTimestampResultPayload
import com.alexgreece45.marvelx.data.repository.RefreshTokenRepository
import com.alexgreece45.marvelx.data.repository.TokenRefreshRepositoryResult
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.UUID
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Owns the single app-wide websocket connection.
 *
 * The connection is opened while the user has a session and the app is in the foreground, and
 * reconnects on its own with exponential backoff. A dropped transport is never treated as a reason
 * to log the user out. Frames pushed by the server are exposed through [inbound].
 */
interface WebsocketController {

    val connectionState: StateFlow<WebsocketConnectionState>

    /**
     * Frames pushed by the server. Buffered, so a burst arriving while a collector is busy is not
     * dropped; collectors that fall far behind suspend the emitter rather than lose frames.
     */
    val inbound: SharedFlow<WebsocketInbound>

    /**
     * Opens the connection and keeps it open (reconnecting as needed) until [disconnect].
     */
    fun connect()

    /**
     * Closes the connection and stops any reconnection attempts.
     */
    fun disconnect()

    /**
     * The socket is only kept open while the app is visible; backgrounding closes it and
     * foregrounding reopens it if [connect] is still in effect.
     */
    fun onAppForegroundChanged(isForeground: Boolean)

    /**
     * Asks the server for its current time. Returns `null` if the socket is not connected or the
     * server does not answer within [timeoutMillis].
     */
    suspend fun requestServerTimestamp(timeoutMillis: Long = DEFAULT_TIMESTAMP_TIMEOUT_MILLIS): Instant?

    companion object {
        const val DEFAULT_TIMESTAMP_TIMEOUT_MILLIS = 3_000L
    }
}

@Single
class WebsocketControllerImpl(
    private val websocketConfigLogic: WebsocketConfigLogic,
    @WebsocketOkHttpClient private val okHttpClient: OkHttpClient,
    private val userSessionRepository: UserSessionRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val configOauthLogic: ConfigOauthLogic,
    private val appLogger: AppLogger,
    private val gson: Gson,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : WebsocketController {

    private val loggerTag = "WebsocketController"

    private val _connectionState = MutableStateFlow(WebsocketConnectionState.Disconnected)
    override val connectionState: StateFlow<WebsocketConnectionState> =
        _connectionState.asStateFlow()

    private val _inbound = MutableSharedFlow<WebsocketInbound>(
        replay = 0,
        extraBufferCapacity = INBOUND_BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    override val inbound: SharedFlow<WebsocketInbound> = _inbound.asSharedFlow()

    // Everything below is guarded by [mutex]; the OkHttp listener callbacks arrive on OkHttp's
    // own threads and hop into [applicationScope] before touching it.
    private val mutex = Mutex()
    private var webSocket: WebSocket? = null
    private var shouldStayConnected = false
    private var isAppInForeground = true
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0
    private var refreshTokenBeforeReconnect = false

    override fun connect() {
        applicationScope.launch {
            mutex.withLock {
                shouldStayConnected = true
                reconnectAttempt = 0
            }
            openIfNeeded()
        }
    }

    override fun disconnect() {
        applicationScope.launch {
            mutex.withLock {
                shouldStayConnected = false
                cancelScheduledReconnectLocked()
                closeSocketLocked()
                _connectionState.value = WebsocketConnectionState.Disconnected
            }
        }
    }

    override fun onAppForegroundChanged(isForeground: Boolean) {
        applicationScope.launch {
            val shouldReopen = mutex.withLock {
                isAppInForeground = isForeground
                if (!isForeground) {
                    cancelScheduledReconnectLocked()
                    closeSocketLocked()
                    _connectionState.value = WebsocketConnectionState.Disconnected
                }
                isForeground && shouldStayConnected
            }

            if (shouldReopen) {
                mutex.withLock { reconnectAttempt = 0 }
                openIfNeeded()
            }
        }
    }

    override suspend fun requestServerTimestamp(timeoutMillis: Long): Instant? {
        val socket = mutex.withLock { webSocket } ?: return null
        if (!_connectionState.value.isConnected) return null

        val requestId = UUID.randomUUID().toString()
        val frame = WebsocketEnvelope(
            type = WebsocketFrameType.TIMESTAMP_GET,
            payload = gson.toJsonTree(WebsocketTimestampRequestPayload(requestId))
        )

        return withTimeoutOrNull(timeoutMillis.milliseconds) {
            // Subscribe before sending so a fast reply cannot slip past the collector.
            inbound
                .onSubscription { socket.send(gson.toJson(frame)) }
                .filterIsInstance<WebsocketInbound.TimestampResult>()
                .first { it.requestId == requestId }
                .timestamp
        }
    }

    // Connection lifecycle.

    private suspend fun openIfNeeded() {
        mutex.withLock {
            if (!shouldStayConnected || !isAppInForeground || webSocket != null) return
            if (!userSessionRepository.hasUserSession()) {
                appLogger.d(loggerTag) { "Not opening the websocket: no user session." }
                return
            }

            _connectionState.value = WebsocketConnectionState.Connecting

            val request = Request.Builder()
                .url(websocketConfigLogic.websocketUrl)
                .addHeader(
                    name = ConstantsSecurity.HeaderName.UserAgent.name,
                    value = ConstantsSecurity.HeaderValue.APP_USER_AGENT
                )
                .addHeader(
                    name = ConstantsSecurity.HeaderName.Authorization.name,
                    value = userSessionRepository.getAccessTokenAndType()
                )
                .build()

            webSocket = okHttpClient.newWebSocket(request, listener)
        }
    }

    private val listener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            applicationScope.launch {
                mutex.withLock {
                    if (this@WebsocketControllerImpl.webSocket !== webSocket) return@withLock
                    reconnectAttempt = 0
                    refreshTokenBeforeReconnect = false
                    _connectionState.value = WebsocketConnectionState.Connected
                }
                appLogger.d(loggerTag) { "Websocket connected." }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            applicationScope.launch { handleFrame(webSocket, text) }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            // Acknowledge the server's close handshake; onClosed follows and handles reconnection.
            webSocket.close(code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            appLogger.d(loggerTag) { "Websocket closed. code=$code reason=$reason" }
            applicationScope.launch { onConnectionLost(webSocket, isUnauthorized = false) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            appLogger.e(loggerTag) { "Websocket failure. httpCode=${response?.code} cause=${t.message}" }
            applicationScope.launch {
                onConnectionLost(webSocket, isUnauthorized = response?.code == HTTP_UNAUTHORIZED)
            }
        }
    }

    private suspend fun onConnectionLost(lostSocket: WebSocket, isUnauthorized: Boolean) {
        mutex.withLock {
            // A socket we already replaced or deliberately closed carries no lifecycle meaning.
            if (webSocket !== lostSocket) return
            webSocket = null

            if (!shouldStayConnected || !isAppInForeground) {
                _connectionState.value = WebsocketConnectionState.Disconnected
                return
            }

            if (isUnauthorized) refreshTokenBeforeReconnect = true
            _connectionState.value = WebsocketConnectionState.Reconnecting
            scheduleReconnectLocked()
        }
    }

    private fun scheduleReconnectLocked() {
        cancelScheduledReconnectLocked()
        val delayMillis = backoffDelayMillis(reconnectAttempt++)
        appLogger.d(loggerTag) { "Reconnecting in ${delayMillis}ms (attempt $reconnectAttempt)." }

        reconnectJob = applicationScope.launch {
            val needsRefresh = mutex.withLock { refreshTokenBeforeReconnect }
            if (needsRefresh) refreshAccessToken()
            delay(delayMillis.milliseconds)
            openIfNeeded()
        }
    }

    private fun cancelScheduledReconnectLocked() {
        reconnectJob?.cancel()
        reconnectJob = null
    }

    private fun closeSocketLocked() {
        val socket = webSocket ?: return
        // Null first so the listener's onClosed sees a stale socket and does not reconnect.
        webSocket = null
        socket.close(WebsocketTerminationCode.Client.code, null)
    }

    private fun backoffDelayMillis(attempt: Int): Long {
        val exponential = INITIAL_BACKOFF_MILLIS shl min(attempt, MAX_BACKOFF_SHIFT)
        val capped = min(exponential, MAX_BACKOFF_MILLIS)
        val jitter = (capped * BACKOFF_JITTER_FRACTION * Random.nextDouble()).toLong()
        return capped + jitter
    }

    /**
     * Mirrors the refresh performed by RefreshTokenWorker so an expired access token does not
     * keep the socket in a 401 loop between the worker's periodic runs.
     */
    private suspend fun refreshAccessToken() {
        val refreshToken = userSessionRepository.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) return

        when (val result =
            refreshTokenRepository.refreshAccessToken(configOauthLogic.clientId, refreshToken)) {
            is TokenRefreshRepositoryResult.Success -> {
                val body = result.responseToken ?: return
                userSessionRepository.setAuthenticationTokens(
                    AuthenticationTokens(
                        accessToken = body.accessToken,
                        accessExpiresIn = body.accessExpiresIn.toLong(),
                        refreshToken = body.refreshToken,
                        refreshExpiresIn = body.refreshExpiresIn.toLong(),
                        tokenType = body.tokenType
                    )
                )
                mutex.withLock { refreshTokenBeforeReconnect = false }
                appLogger.d(loggerTag) { "Access token refreshed for websocket reconnection." }
            }

            is TokenRefreshRepositoryResult.Failed -> {
                appLogger.e(loggerTag) { "Token refresh for websocket failed: ${result.failureMessage}" }
            }
        }
    }

    // Inbound frames.

    private suspend fun handleFrame(source: WebSocket, text: String) {
        val envelope = try {
            gson.fromJson(text, WebsocketEnvelope::class.java)
        } catch (e: JsonSyntaxException) {
            appLogger.e(loggerTag) { "Dropping malformed websocket frame: ${e.message}" }
            return
        } ?: return

        val inbound = when (envelope.type) {
            WebsocketFrameType.CHAT_MESSAGE ->
                decode<ResponseChatMessage>(envelope.payload)?.let { WebsocketInbound.ChatMessage(it) }

            WebsocketFrameType.CHAT_READ ->
                decode<WebsocketChatReadPayload>(envelope.payload)?.let {
                    WebsocketInbound.ChatRead(
                        conversationId = it.conversationId,
                        readByUserId = it.readByUserId,
                        lastReadMessageId = it.lastReadMessageId
                    )
                }

            WebsocketFrameType.NOTIFICATION_DELETE ->
                decode<WebsocketNotificationDeletePayload>(envelope.payload)?.let {
                    WebsocketInbound.NotificationDeleted(it.notificationId)
                }

            WebsocketFrameType.TIMESTAMP_RESULT ->
                decode<WebsocketTimestampResultPayload>(envelope.payload)?.let { payload ->
                    parseInstant(payload.timestamp)?.let { instant ->
                        WebsocketInbound.TimestampResult(payload.requestId, instant)
                    }
                }

            WebsocketFrameType.ERROR -> {
                val payload = decode<WebsocketErrorPayload>(envelope.payload)
                if (payload?.code == WebsocketErrorCode.UNAUTHORIZED) {
                    // The server will close the socket; make sure the reconnect refreshes first.
                    mutex.withLock { refreshTokenBeforeReconnect = true }
                    source.close(WebsocketTerminationCode.Client.code, null)
                }
                payload?.let { WebsocketInbound.Error(code = it.code, message = it.message) }
            }

            else -> WebsocketInbound.Unknown(envelope.type)
        }

        if (inbound == null) {
            appLogger.e(loggerTag) { "Dropping websocket frame of type '${envelope.type}' with an undecodable payload." }
            return
        }
        _inbound.emit(inbound)
    }

    private inline fun <reified T> decode(payload: JsonElement?): T? {
        if (payload == null || payload.isJsonNull) return null
        return try {
            gson.fromJson(payload, T::class.java)
        } catch (e: JsonSyntaxException) {
            appLogger.e(loggerTag) { "Failed to decode websocket payload as ${T::class.simpleName}: ${e.message}" }
            null
        }
    }

    private fun parseInstant(value: String): Instant? =
        try {
            Instant.parse(value)
        } catch (e: DateTimeParseException) {
            appLogger.e(loggerTag) { "Unparseable timestamp from websocket: $value" }
            null
        }

    private companion object {
        const val INBOUND_BUFFER_CAPACITY = 64
        const val HTTP_UNAUTHORIZED = 401
        const val INITIAL_BACKOFF_MILLIS = 1_000L
        const val MAX_BACKOFF_MILLIS = 30_000L
        const val MAX_BACKOFF_SHIFT = 5
        const val BACKOFF_JITTER_FRACTION = 0.2
    }
}
