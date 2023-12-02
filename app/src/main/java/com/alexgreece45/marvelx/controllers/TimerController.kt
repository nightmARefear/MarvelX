package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.extensions.pad
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * This controller is used to accurately measure time by utilizing [Timer].
 *
 * Upon starting a new timer, the old one is automatically cancelled.
 */
interface TimerController {
    /**
     * Initializes a new timer.
     *
     * @param expiresIn     The amount of seconds till the timer must stop.
     * @param onTick        In every clock tick, it returns the current time formatted (i.e., `3:20`,
     * `1:05`, `25`, `5`, etc.).
     */
    fun startTimer(expiresIn: Long, onTick: (formattedTime: String, isLastSecond: Boolean) -> Unit)

    /**
     * Stops the timer.
     */
    fun stopTimer()
}

class TimerControllerImpl @Inject constructor(
    private val logger: AppLoggerController
) : TimerController {
    private var remainingNumberOfSeconds: Duration = ZERO
    private lateinit var timer: Timer

    private var minutes = "00"

    private var seconds = "00"

    /**
     * Returns the formatted time for [minutes] and [seconds] in a human readable format.
     */
    private val formattedTime: String
        get() = DateUtil.formatTime(
            minutes.toInt(),
            seconds.toInt()
        )

    /**
     * Returns `true` if the current second is the last one, otherwise `false`. This is calculated
     * as follows:
     *
     *
     * If minutes are 0, and there are no seconds left, then the current second is the last one.
     */
    private val isLastSecond: Boolean
        get() = minutes.toInt() == 0 && seconds.toInt() == 0

    override fun startTimer(
        expiresIn: Long,
        onTick: (formattedTime: String, isLastSecond: Boolean) -> Unit
    ) {
        // First of all, stop the timer if it has already been initialized.
        stopTimer()

        // Set the duration.
        remainingNumberOfSeconds = expiresIn.seconds

        // Create a new timer.
        timer = fixedRateTimer(initialDelay = 0L, period = 1000L) {
            // Stop timer, if remaining number of seconds is 0.
            if (remainingNumberOfSeconds.toInt(DurationUnit.SECONDS) == 0) {
                stopTimer()
                return@fixedRateTimer
            }

            remainingNumberOfSeconds = remainingNumberOfSeconds.minus(1.seconds)

            updateTimeUnits()
            onTick(
                formattedTime.also { logger.d { "remaining time: $it" } },
                isLastSecond
            )
        }
    }

    override fun stopTimer() {
        if (isInitialized) {
            timer.cancel()
            remainingNumberOfSeconds = ZERO
        }
    }

    private fun updateTimeUnits() {
        remainingNumberOfSeconds.toComponents { minutes, seconds, _ ->
            this@TimerControllerImpl.minutes = minutes.toInt().pad()
            this@TimerControllerImpl.seconds = seconds.pad()
        }
    }

    /**
     * Returns `true` if the timer has been initialized, otherwise `false`.
     */
    private val isInitialized: Boolean
        get() = this::timer.isInitialized
}