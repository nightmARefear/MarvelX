package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

class ThemeShapes : ThemeShapesTemplate() {
    override val smallShape: CornerBasedShape get() = RoundedCornerShape(SPACING_TINY.dp)
    override val mediumShape: CornerBasedShape get() = RoundedCornerShape(SPACING_SMALL.dp)
    override val largeShape: CornerBasedShape get() = RoundedCornerShape(SPACING_LARGE.dp)
}