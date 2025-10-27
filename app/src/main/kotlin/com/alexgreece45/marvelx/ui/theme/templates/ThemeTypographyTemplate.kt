package com.alexgreece45.marvelx.ui.theme.templates

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

abstract class ThemeTypographyTemplate {

    val typo: Typography
        get() {
            return Typography(
                defaultFontFamily = defaultFontFamily,
                h1 = h1,
                h2 = h2,
                h3 = h3,
                h4 = h4,
                h5 = h5,
                h6 = h6,
                subtitle1 = subtitle1,
                subtitle2 = subtitle2,
                body1 = body1,
                body2 = body2,
                button = button,
                caption = caption,
                overline = overline
            )
        }

    abstract val defaultFontFamily: FontFamily
    abstract val secondaryFontFamily: FontFamily
    abstract val h1: TextStyle
    abstract val h2: TextStyle
    abstract val h3: TextStyle
    abstract val h4: TextStyle
    abstract val h5: TextStyle
    abstract val h6: TextStyle
    abstract val body1: TextStyle
    abstract val body2: TextStyle
    abstract val subtitle1: TextStyle
    abstract val subtitle2: TextStyle
    abstract val button: TextStyle
    abstract val caption: TextStyle
    abstract val overline: TextStyle
}
