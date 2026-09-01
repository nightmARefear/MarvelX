package com.alexgreece45.marvelx.ui.visual_transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class VisualTransformationCurrencySuffix : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Render the original text plus the currency symbol
        val result = AnnotatedString(text.text + "€")

        // The OffsetMapping ensures the cursor never accidentally jumps into the "€" part
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset > text.length) text.length else offset
            }
        }
        return TransformedText(result, offsetMapping)
    }
}