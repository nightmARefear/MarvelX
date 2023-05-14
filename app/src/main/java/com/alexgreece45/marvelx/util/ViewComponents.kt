package com.alexgreece45.marvelx.util

import android.graphics.drawable.Icon
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar() {
    //Bottom
}

@Composable
fun ContentScreen(
    toolBar: Boolean = false,
    @StringRes toolbarTitle: Int? = null,
    fab: Boolean = false,
    fabIcon: Icon? = null,
    fabAction: (() -> Unit)? = null,
    screenBackgroundColor: Color = Color.White,
    contentColor: Color = Color.White,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (toolBar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primaryVariant,
                    elevation = 0.dp
                ) {
                    toolbarTitle?.let {
                        Text(
                          modifier = Modifier.weight(1f),
                          text = stringResource(toolbarTitle),
                          style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (fab) {
                FloatingActionButton(onClick = { fabAction?.invoke() }) {

                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackgroundColor)
        ) {
            content(padding)
        }
    }
}

@Composable
fun GeneralErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    )
}
