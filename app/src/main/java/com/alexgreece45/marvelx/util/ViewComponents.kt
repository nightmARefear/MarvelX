package com.alexgreece45.marvelx.util

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (toolBar) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    contentPadding = PaddingValues(0.dp).applyPadding()
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        onBack?.let {
                            IconButton(onClick = { it.invoke() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back_32),
                                    contentDescription = null,
//                                tint = MaterialTheme.colors.primary
                                )
                            }
                        }

                        toolbarTitle?.let { toolbarTitle ->
                            Text(
                                modifier = Modifier.weight(1f),
                                text = toolbarTitle,
                                style = MaterialTheme.typography.h6,
                                maxLines = 1
                            )
                        }
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "loading.....",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3,
                )
            }
        }
    }
}

@Composable
fun ItemDescription(
    description: String,
    emptyDescriptionMessage: String
) {
    if (description.isBlank()) {
        Text(
            text = emptyDescriptionMessage,
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

data class ActionButtonData(
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun LazyItemScope.GenericErrorScreen(
    errorMessage: String,
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.error
            )
        )
    }
}

@Composable
fun LazyItemScope.NoInternetErrorScreen(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection_error_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.error
            )
        )

        VSpacer.Medium()

        Button(
            onClick = { onRetryClick() }
        ) {
            Text(
                text = stringResource(R.string.retry).uppercase(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}
