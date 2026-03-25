package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.model.ShadowData
import com.alexgreece45.marvelx.util.extensions.noCache

/**
 * A wrapper composable for [AsyncImage] providing custom styling options, including optional
 * shadows.
 *
 * @param imageUrl              The URL of the image to load.
 * @param contentDescription    Text used by accessibility services to describe what this image
 * represents.
 * @param modifier              Modifier used to adjust the layout algorithm or draw decoration
 * content.
 * @param placeholder           The placeholder Painter to be displayed while the image is loading.
 * @param error                 The Painter to be displayed if the image request fails.
 * @param onLoading             Callback invoked when the image is being loaded.
 * @param onSuccess             Callback invoked when the image is successfully loaded.
 * @param onError               Callback invoked when the image request fails.
 * @param contentScale          How to scale the image content.
 * @param shadowData            Optional [ShadowData] to apply a shadow effect to the image.
 * @param isCacheDisabled       Whether the cache is disabled.
 */
@Composable
fun WrapAsyncImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = painterResource(R.drawable.ic_broken_image),
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    shadowData: ShadowData? = null,
    isCacheDisabled: Boolean = false,
) {
    val shadowModifier = if (shadowData != null) {
        Modifier
            .shadow(
                elevation = shadowData.elevation,
                shape = shadowData.shape,
                clip = shadowData.clip,
                ambientColor = shadowData.ambientColor,
                spotColor = shadowData.spotColor
            )
    } else Modifier

    Box(modifier = boxModifier.then(shadowModifier)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .apply {
                    if (isCacheDisabled) {
                        noCache()
                    }
                }
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = placeholder,
            error = error,
            onLoading = { onLoading?.invoke(it) },
            onSuccess = { onSuccess?.invoke(it) },
            onError = { onError?.invoke(it) },
            contentScale = contentScale
        )
    }
}
