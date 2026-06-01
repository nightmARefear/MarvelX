package com.alexgreece45.marvelx.controllers

import android.text.format.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single

/**
 * Provides a wide range of time-related utility functions.
 */
interface TimerController {
    /**
     * This function is used to accurately measure time by utilizing [delay]. All the required
     * operations are performed on the `IO` thread, so that the main thread doesn't get blocked.
     *
     * @param duration  The number of seconds that the countdown timer should last.
     *
     * @return          Emits every clock tick the current time formatted (i.e., `03:20`, `01:05`,
     * `00:25`, `00:05`, etc.), as well as, the timer status (i.e., `Ticking`, `Completed`).
     */
    suspend fun startCountdown(duration: Long): Flow<TimerStatus>
}

@Single
class TimerControllerImpl : TimerController {
    override suspend fun startCountdown(duration: Long): Flow<TimerStatus> = flow {
        for (remainingSeconds in duration downTo 1) {
            if (remainingSeconds <= 1) {
                emit(TimerStatus.Completed)
                break
            }
            emit(
                TimerStatus.Ticking(
                    remainingTime = DateUtils.formatElapsedTime(remainingSeconds)
                )
            )
            delay(1000L)
        }
    }.flowOn(Dispatchers.IO)
}

sealed interface TimerStatus {
    data class Ticking(val remainingTime: String) : TimerStatus
    data object Completed : TimerStatus
}