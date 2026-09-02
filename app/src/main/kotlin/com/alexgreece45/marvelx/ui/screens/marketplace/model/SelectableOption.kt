package com.alexgreece45.marvelx.ui.screens.marketplace.model

/**
 * Represents a single selectable option for multi-option selectable groups.
 *
 * @param option The option data.
 * @param isSelected Whether this option is selected.
 */
data class SelectableOption<T>(
    val option: T,
    val isSelected: Boolean = false
)