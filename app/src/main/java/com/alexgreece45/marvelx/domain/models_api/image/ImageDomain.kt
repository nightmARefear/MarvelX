package com.alexgreece45.marvelx.domain.models_api.image

import com.alexgreece45.marvelx.data.models_api.character.ImageResponse

data class ImageDomain(
    val path: String,
    val extension: String,
    val downloadUrl: String = "$path.$extension",
    val contentDescription: String
)

fun ImageDomain.toResponse(): ImageResponse =
    ImageResponse(path = this.path, extension = this.extension)

fun List<ImageDomain>.toResponse(): List<ImageResponse> =
    this.map { ImageResponse(path = it.path, extension = it.extension) }
