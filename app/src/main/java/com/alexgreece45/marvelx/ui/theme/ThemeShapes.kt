package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

class ThemeShapes : ThemeShapesTemplate() {
    override val smallShape: CornerBasedShape get() = RoundedCornerShape(4.dp)
    override val mediumShape: CornerBasedShape get() = RoundedCornerShape(4.dp)
    override val largeShape: CornerBasedShape get() = RoundedCornerShape(0.dp)
}