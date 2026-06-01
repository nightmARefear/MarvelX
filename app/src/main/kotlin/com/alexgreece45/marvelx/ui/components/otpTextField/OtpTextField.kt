package com.alexgreece45.marvelx.ui.components.otpTextField

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity

/**
 * This composable represents an `OTP` text-field capable of receiving 6 digits. Each digit is
 * visually represented in its each own "container".
 *
 * @param modifier          The modifier to be applied.
 * @param enabled           Whether this OTP text-field is enabled.
 * @param otp               The current OTP.
 * @param onValueChange     The action(s) to be invoked when the OTP changes.
 * @param onDoneClick       The action(s) to be invoked when the on-done button of the keyboard is
 * clicked.
 */
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    otp: String,
    onValueChange: (String, Boolean) -> Unit,
    onDoneClick: () -> Unit
) {
    BasicTextField(
        modifier = modifier,
        enabled = enabled,
        value = TextFieldValue(
            otp,
            selection = TextRange(otp.length)
        ),
        onValueChange = {
            if (it.text.length <= ConstantsSecurity.OTP.LENGTH) {
                onValueChange.invoke(it.text, it.text.length == ConstantsSecurity.OTP.LENGTH)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClick.invoke()
            }
        ),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(ConstantsSecurity.OTP.LENGTH) { index ->
                    CharView(
                        index = index,
                        text = otp
                    )
                    HSpacer.Small()
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }

    val lineColor = when (isFocused) {
        true -> MaterialTheme.customColors.genericRed
        false -> MaterialTheme.colors.onBackground
    }

    Box(
        modifier = Modifier
            .size(45.dp)
            .background(MaterialTheme.colors.surface.copy(alpha = 0.6f))
            /*  .border(
                  width = 2.dp,
                  color = when {
                      isFocused -> MaterialTheme.colors.onPrimary
                      else -> MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                  },
                  shape = MaterialTheme.shapes.medium
              )*/
            .drawWithContent {
                // Draws this field.
                drawContent()

                // Draws a horizontal line at the bottom of this field.
                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = 0f,
                        y = size.height
                    ),
                    end = Offset(
                        x = size.width,
                        y = size.height
                    ),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.h4,
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun OtpTextFieldPreview() {
    var otp by remember { mutableStateOf("") }
    MarvelXTheme {
        OtpTextField(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            otp = otp,
            onValueChange = { value, _ ->
                otp = value
            },
            onDoneClick = {
            }
        )
    }
}