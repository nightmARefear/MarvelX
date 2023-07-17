package com.alexgreece45.marvelx.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val TINY = 4
private const val SMALL = 8
private const val MEDIUM = 16
private const val LARGE = 24

object VSpacer {
    @Composable
    fun Tiny() = Spacer(modifier = Modifier.height(height = TINY.dp))

    @Composable
    fun Small() = Spacer(modifier = Modifier.height(height = SMALL.dp))

    @Composable
    fun Medium() = Spacer(modifier = Modifier.height(height = MEDIUM.dp))

    @Composable
    fun Large() = Spacer(modifier = Modifier.height(height = LARGE.dp))

    @Composable
    fun Custom(height: Int) = Spacer(modifier = Modifier.height(height = height.dp))

    @Composable
    fun Custom(height: Double) = Spacer(modifier = Modifier.height(height = height.dp))

    @Composable
    fun Custom(height: Float) = Spacer(modifier = Modifier.height(height = height.dp))
}

object HSpacer {
    @Composable
    fun Tiny() = Spacer(modifier = Modifier.height(height = TINY.dp))

    @Composable
    fun Small() = Spacer(modifier = Modifier.width(width = SMALL.dp))

    @Composable
    fun Medium() = Spacer(modifier = Modifier.width(width = MEDIUM.dp))

    @Composable
    fun Large() = Spacer(modifier = Modifier.width(width = LARGE.dp))

    @Composable
    fun Custom(width: Int) = Spacer(modifier = Modifier.width(width = width.dp))

    @Composable
    fun Custom(width: Double) = Spacer(modifier = Modifier.width(width = width.dp))

    @Composable
    fun Custom(width: Float) = Spacer(modifier = Modifier.width(width = width.dp))
}