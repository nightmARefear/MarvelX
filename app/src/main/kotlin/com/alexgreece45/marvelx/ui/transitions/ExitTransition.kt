package com.alexgreece45.marvelx.ui.transitions

import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

/**
 * Contains all the `exit` transitions used for navigation purposes.
 */
object ExitTransition {
    /**
     * Exit transition animation that slides from the left to the right.
     */
    val LeftToRight = slideOutHorizontally { it }

    /**
     * Exit transition that slides from the top to the bottom.
     */
    val TopToBottom = slideOutVertically { it }
}
