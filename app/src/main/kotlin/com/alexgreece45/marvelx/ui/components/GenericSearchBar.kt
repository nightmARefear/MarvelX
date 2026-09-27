package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_SMALL

@Composable
fun GenericSearchBar(
    searchTerm: String,
    hint: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    textColor: Color = contentColorFor(backgroundColor),
    deleteButtonTestTag: String? = null,
    onValueChange: (String) -> Unit,
    onDeleteIconClicked: () -> Unit
) {
    TextField(
        modifier = modifier,
        value = searchTerm,
        placeholder = {
            Text(
                text = hint,
                fontStyle = FontStyle.Italic,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.h6
            )
        },
        leadingIcon = {
            Icon(
                painter = rememberVectorPainter(image = Icons.Filled.Search),
                contentDescription = null,
                tint = MaterialTheme.customColors.white
            )
        },
        trailingIcon = if (searchTerm.isBlank()) {
            null
        } else {
            {
                val circleBackgroundColor = MaterialTheme.colors.background
                IconButton(
                    onClick = onDeleteIconClicked,
                    modifier = deleteButtonTestTag?.let { Modifier.testTag(it) } ?: Modifier
                ) {
                    Icon(
                        modifier = Modifier
                            .size(SPACING_LARGE.dp)
                            .drawBehind {
                                drawCircle(
                                    color = circleBackgroundColor,
                                    radius = this.size.maxDimension.div(2) + SPACING_SMALL
                                )
                            },
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.content_description_delete_search_term),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        },
        onValueChange = onValueChange,
        singleLine = true,
        shape = CircleShape,
        textStyle = MaterialTheme.typography.h6,
        colors = TextFieldDefaults.textFieldColors(
            textColor = textColor,
            backgroundColor = backgroundColor,
            cursorColor = MaterialTheme.customColors.genericRed,
            focusedIndicatorColor = MaterialTheme.customColors.transparent,
            unfocusedIndicatorColor = MaterialTheme.customColors.transparent,
            disabledIndicatorColor = MaterialTheme.customColors.transparent,
            errorIndicatorColor = MaterialTheme.customColors.transparent,
            placeholderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )
    )
}