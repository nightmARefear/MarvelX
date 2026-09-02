package com.alexgreece45.marvelx.controllers

import android.text.format.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Provides a wide range of time-related utility functions.
 */
interface TimerController {

    /**
     * This function is used to accurately measure time by utilizing [delay].
     *
     * @param duration  The number of seconds that the countdown timer should last.
     *
     * @return          Emits every clock tick the current time formatted (i.e., `03:20`, `01:05`,
     * `00:25`, `00:05`, etc.), as well as, the timer status (i.e., `Ticking`, `Completed`).
     */
    fun startCountdown(duration: Long): Flow<TimerStatus>

    /**
     * This function is used to trigger an indefinite countdown at a set interval by utilizing [delay].
     *
     * @param delayMillis       The interval between each emission, represented in milliseconds.
     * @param startImmediately  Whether the first emission should occur instantly, or wait for the
     *                          initial delay to elapse.
     *
     * @return                  Emits a [Unit] value every time the specified delay passes, continuing
     *                          indefinitely until the flow collection is cancelled.
     */
    fun startIndefiniteCountdown(delayMillis: Long, startImmediately: Boolean): Flow<Unit>
}

@Single
internal class TimerControllerImpl : TimerController {
    override fun startCountdown(duration: Long): Flow<TimerStatus> = flow {
        val delayDuration = 1.seconds

        for (remainingSeconds in duration downTo 1) {
            emit(
                TimerStatus.Ticking(
                    remainingTime = DateUtils.formatElapsedTime(remainingSeconds)
                )
            )
            delay(delayDuration)
        }
        emit(TimerStatus.Completed)
    }

    override fun startIndefiniteCountdown(
        delayMillis: Long,
        startImmediately: Boolean
    ): Flow<Unit> = flow {
        val delayDuration = delayMillis.milliseconds
        if (!startImmediately) {
            delay(delayDuration)
        }
        while (true) {
            emit(Unit)
            delay(delayDuration)
        }
    }
}

sealed interface TimerStatus {
    data class Ticking(val remainingTime: String) : TimerStatus
    data object Completed : TimerStatus
}