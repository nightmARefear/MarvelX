package com.alexgreece45.marvelx.ui.components.hightlightedText

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.alexgreece45.marvelx.ui.components.model.ClickableString

/**
 * A wrapper for [ClickableText] providing an easier way to locate [String]s and provide styling and
 * on-click functionality on them.
 *
 * This composable creates a [ClickableText] with many individual clickable [String]s.
 * Each clickable [String] is unique from each other (distinguished internally by the "tag"), and
 * also has its own custom styling and on-click functionality.
 *
 * The order of [clickableStrings] doesn't matter as it's handled internally by sorting this list in
 * ascending order based on the start index of the first occurrence of [ClickableString.text] in
 * [text].
 *
 * @param text              The whole text to be displayed.
 * @param modifier          The modifier to be applied to the whole text.
 * @param textStyle         The [TextStyle] to be applied to the non-clickable text.
 * @param clickableStrings  The list of [ClickableString]s to be found in [text].
 */
@Composable
fun WrapClickableText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    clickableStrings: List<ClickableString>
) {
    data class StringData(
        val text: String,
        val tag: String? = null,
        val data: String? = null,
        val fontSize: TextUnit? = null,
        val fontWeight: FontWeight? = null,
        val color: Color? = null,
        val onClick: ((data: AnnotatedString.Range<String>) -> Unit)? = null
    )

    // Sorts the clickable strings in ascending order, so that they can work in any order that they're inserted as.
    val sortedClickableTexts =
        clickableStrings.sortedBy { clickableText -> text.indexOf(clickableText.text) }

    val stringData = mutableListOf<StringData>()
    if (sortedClickableTexts.isEmpty()) {
        stringData.add(
            StringData(
                text = text
            )
        )
    } else {
        var startIndex = 0
        sortedClickableTexts.forEachIndexed { index, clickableText ->
            val endIndex = text.indexOf(clickableText.text)
            if (endIndex == -1) {
                throw RuntimeException("Fatal error: $clickableText does not exist in $text")
            }
            stringData.add(
                // Non-clickable string.
                StringData(
                    text = text.substring(startIndex, endIndex)
                )
            )
            stringData.add(
                // Clickable string.
                StringData(
                    text = clickableText.text,
                    tag = "${clickableText.text}_TAG",
                    data = clickableText.data,
                    color = clickableText.color,
                    onClick = {
                        clickableText.onClick(it.item)
                    }
                )
            )
            startIndex = endIndex + clickableText.text.length
            if (index == sortedClickableTexts.lastIndex && startIndex < text.length) {
                // The remaining string after the last clickable string.
                stringData.add(
                    StringData(
                        text = text.substring(startIndex, text.length)
                    )
                )
            }
        }
    }

    val annotatedString = buildAnnotatedString {
        stringData.forEach { linkStringData ->
            if (linkStringData.tag != null && linkStringData.data != null) {
                // Clickable string.
                pushStringAnnotation(
                    tag = linkStringData.tag,
                    annotation = linkStringData.data,
                )
                withStyle(
                    style = SpanStyle(
                        color = linkStringData.color ?: MaterialTheme.colors.primaryVariant,
                        fontSize = linkStringData.fontSize ?: textStyle.fontSize,
                        fontWeight = linkStringData.fontWeight ?: textStyle.fontWeight
                    )
                ) {
                    append(linkStringData.text)
                }
                pop()
            } else {
                // Non-clickable string.
                append(linkStringData.text)
            }
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = textStyle,
        onClick = { offset ->
            stringData.forEach { annotatedStringData ->
                if (annotatedStringData.tag != null && annotatedStringData.data != null) {
                    annotatedString.getStringAnnotations(
                        tag = annotatedStringData.tag,
                        start = offset,
                        end = offset,
                    ).firstOrNull()?.let {
                        annotatedStringData.onClick?.invoke(it)
                    }
                }
            }
        }
    )
}