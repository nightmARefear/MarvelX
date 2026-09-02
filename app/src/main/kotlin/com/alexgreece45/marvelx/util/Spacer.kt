package com.alexgreece45.marvelx.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val SPACING_TINY = 4
const val SPACING_SMALL = 8
const val SPACING_MEDIUM = 16
const val SPACING_LARGE = 24

object VSpacer {
    @Composable
    fun Tiny() = Spacer(modifier = Modifier.height(height = SPACING_TINY.dp))

    @Composable
    fun Small() = Spacer(modifier = Modifier.height(height = SPACING_SMALL.dp))

    @Composable
    fun Medium() = Spacer(modifier = Modifier.height(height = SPACING_MEDIUM.dp))

    @Composable
    fun Large() = Spacer(modifier = Modifier.height(height = SPACING_LARGE.dp))

    @Composable
    fun Custom(height: Dp) = Spacer(modifier = Modifier.height(height = height))
}

object HSpacer {
    @Composable
    fun Tiny() = Spacer(modifier = Modifier.width(width = SPACING_TINY.dp))

    @Composable
    fun Small() = Spacer(modifier = Modifier.width(width = SPACING_SMALL.dp))

    @Composable
    fun Medium() = Spacer(modifier = Modifier.width(width = SPACING_MEDIUM.dp))

    @Composable
    fun Large() = Spacer(modifier = Modifier.width(width = SPACING_LARGE.dp))

    @Composable
    fun Custom(width: Dp) = Spacer(modifier = Modifier.width(width = width))
}