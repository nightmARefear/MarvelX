package com.alexgreece45.marvelx.ui.model_generic

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class BigDecimalUi(
    val value: BigDecimal,
    val label: String
)