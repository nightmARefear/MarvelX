package com.alexgreece45.marvelx.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alexgreece45.marvelx.R

class ThemeTypography : ThemeTypographyTemplate() {
    override val defaultFontFamily: FontFamily
        get() = FontFamily(
            Font(
                R.font.roboto_regular,
                style = FontStyle.Normal,
                weight = FontWeight.Normal
            ),
            Font(
                R.font.roboto_bold,
                style = FontStyle.Normal,
                weight = FontWeight.Bold
            ),
            Font(
                R.font.roboto_italic,
                style = FontStyle.Italic,
                weight = FontWeight.Thin
            ),
            Font(
                R.font.roboto_bold_italic,
                style = FontStyle.Italic,
                weight = FontWeight.Bold
            )
        )
    override val secondaryFontFamily: FontFamily
        get() = FontFamily(
            Font(
                R.font.pf_highway_gothic,
                style = FontStyle.Normal,
                weight = FontWeight.Normal
            ),
        )
    override val h1: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 94.sp,
            letterSpacing = 0.sp
        )
    override val h2: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 59.sp,
            letterSpacing = 0.sp
        )
    override val h3: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 47.sp,
            letterSpacing = 0.sp
        )
    override val h4: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 33.sp,
            letterSpacing = 0.25.sp
        )
    override val h5: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            letterSpacing = 0.sp
        )
    override val h6: TextStyle
        get() = TextStyle(
            fontFamily = secondaryFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            letterSpacing = 0.15.sp
        )
    override val subtitle1: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            letterSpacing = 0.15.sp
        )
    override val subtitle2: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.1.sp
        )
    override val body1: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        )
    override val body2: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.25.sp
        )
    override val button: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.25.sp
        )
    override val caption: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp
        )
    override val overline: TextStyle
        get() = TextStyle(
            fontFamily = defaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
}