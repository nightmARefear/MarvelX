package com.alexgreece45.marvelx.ui.transitions

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically

/**
 * Contains all the `enter` transitions used for navigation purposes.
 */
object EnterTransition {

    /**
     * Enter transition animation that slides from the right to the left.
     */
    val RightToLeft = slideInHorizontally { it }

    /**
     * Enter transition that slides from the bottom to the top.
     */
    val BottomToTop = slideInVertically { it }
}
