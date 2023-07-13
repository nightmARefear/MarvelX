package com.alexgreece45.marvelx.util

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.extensions.applyPadding

@Composable
fun BottomBar() {
    //Bottom
}

@Composable
fun ContentScreen(
    onBack: (() -> Unit)? = null,
    toolBar: Boolean = false,
    toolbarTitle: String? = null,
    fab: Boolean = false,
    fabIcon: Icon? = null,
    fabAction: (() -> Unit)? = null,
    screenBackgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = MaterialTheme.colors.onBackground,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (toolBar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp
                ) {
                    onBack?.let {
                        IconButton(onClick = { it.invoke() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back_32),
                                contentDescription = null,
//                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }

                    HSpacer.Medium()

                    toolbarTitle?.let { toolbarTitle ->
                        Text(
                            modifier = Modifier.weight(1f),
                            text = toolbarTitle,
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
            content(padding.applyPadding())
        }
    }
}

@Composable
fun LoadingScreen(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (isLoading) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "loading.....",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
fun CharacterDescription(
    description: String,
) {
    if (description.isBlank()) {
        Text(
            text = stringResource(R.string.no_character_description),
            style = MaterialTheme.typography.body1.copy(
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
        )
    } else {
        Text(
            text = description,
            style = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colors.onBackground
            )
        )
    }
}

@Composable
fun GeneralErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    )
}
