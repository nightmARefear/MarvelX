package com.alexgreece45.marvelx.util.extensions

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard

fun Clipboard.setText(text: String) {
    this.nativeClipboard.setPrimaryClip(ClipData.newPlainText("copied text", text))
}