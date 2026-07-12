package com.alexgreece45.marvelx.ui_theme.theme.values

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui_theme.theme.templates.ThemeShapesTemplate

class ThemeShapes : ThemeShapesTemplate() {
    override val smallShape: CornerBasedShape get() = RoundedCornerShape(4.dp)
    override val mediumShape: CornerBasedShape get() = RoundedCornerShape(8.dp)
    override val largeShape: CornerBasedShape get() = RoundedCornerShape(24.dp)
}