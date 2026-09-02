package com.alexgreece45.marvelx.util.extensions

import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import java.time.LocalDate

/**
 * Converts this to a short Order format.
 */
internal fun LocalDate.toOrderFormatShort(): String = this.toCustomFormat("MMM dd yyyy")