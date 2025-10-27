package com.alexgreece45.marvelx.util.extensions

import android.text.Spanned
import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

fun Spanned.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString.toString())
        getSpans(0, length, Any::class.java).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)
            when (span) {
                is StyleSpan -> when (span.style) {
                    android.graphics.Typeface.BOLD -> addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start,
                        end
                    )

                    android.graphics.Typeface.ITALIC -> addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                        start,
                        end
                    )

                    android.graphics.Typeface.BOLD_ITALIC -> addStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        ), start, end
                    )
                }
            }
        }
    }
}
