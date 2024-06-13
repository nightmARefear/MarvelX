package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.alexgreece45.marvelx.ui.components.WrapClickableText

/**
 * Defines a single clickable text to be found in a [String]. This data class is used exclusively in
 * [WrapClickableText].
 *
 * @param text          The [String] to be displayed & found.
 * @param data          The link to be opened when this [String] is clicked. Set to `empty` by default.
 * @param color         The [Color] of this [String]. Set to `null` by default.
 * @param fontSize      The size of glyphs (in logical pixels) to use when painting this [String].
 * @param fontWeight    The typeface thickness to use when painting this [String] (e.g., bold).
 * @param onClick       The action(s) to be invoked when this [String] is clicked.
 */
data class ClickableString(
    val text: String,
    val data: String = "",
    val color: Color? = null,
    val fontSize: TextUnit? = null,
    val fontWeight: FontWeight? = null,
    val onClick: (data: String) -> Unit
)