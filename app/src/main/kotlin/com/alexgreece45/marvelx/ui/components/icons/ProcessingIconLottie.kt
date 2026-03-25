package com.alexgreece45.marvelx.ui.components.icons

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.LottieComposable
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.constants.ConstantsUi

@Composable
fun ProcessingRequestIconLottie() {
    LottieComposable(
        resourceId = R.raw.lottie_processing_request,
        size = ConstantsUi.MIN_ICON_SIZE,
        backgroundColor = MaterialTheme.customColors.transparent,
        clipLottie = true
    )
}
