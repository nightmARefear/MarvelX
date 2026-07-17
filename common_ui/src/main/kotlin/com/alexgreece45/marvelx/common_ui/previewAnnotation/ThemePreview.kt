package com.alexgreece45.marvelx.common_ui.previewAnnotation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    apiLevel = 37,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true, showBackground = true
)
annotation class ThemePreview