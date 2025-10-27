package com.alexgreece45.marvelx.domain.model_generic

import java.util.UUID

/**
 * Represents an image that is reloadable. By the uuid changing, the image is forced to be retrieved
 * again.
 */
data class ReloadableImageUrlData(
    val imageUrl: String,
    val uuid: UUID = UUID.randomUUID()
)
