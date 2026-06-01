package com.alexgreece45.marvelx.domain.model_api.image

import com.alexgreece45.marvelx.core.network.model.api.character.CharacterResponse
import com.alexgreece45.marvelx.core.network.model.api.character.ImageResponse
import kotlin.reflect.KClass

data class ImageDomain(
    val path: String,
    val extension: String,
    val downloadUrl: String = "$path.$extension",
    val contentDescription: String
)

/**
 * Converts an [ImageResponse] to an [ImageDomain] as follows:
 *
 * - the variables "path" and "extension" remain the same
 *
 * - [ImageDomain.downloadUrl] is created as follows; "path" + "." + "extension"
 *
 * - [ImageDomain.contentDescription] has a value of "characterName's Image" only if the "containerClassType" parameter is of type [CharacterResponse];
 * this is to ensure that the proper content description is given based on what class the [ImageResponse] is in (i.e., [CharacterResponse],[ComicResponse], etc.)
 *
 * @param containerClassType the class type that this [ImageResponse] is in
 * @param characterName denotes the character name if "containerClassType" is of type [CharacterResponse], or else empty [String] is given
 *
 * @throws IllegalStateException If the container class type is [CharacterResponse] and the parameter "characterName" is null
 *
 * ## Example Usages
 *
 * ### 1. If the parent class of this [ImageResponse] is of type [CharacterResponse]
 * ```
 * this.thumbnail.toDomain(
 * containerClassType = this::class,
 * characterName = this.name
 * )
 * ```
 *
 * ### 2. If the parent class of this [ImageResponse] is of type other than [CharacterResponse], such as [ComicResponse], etc.
 * ```
 * this.thumbnail.toDomain(
 * containerClassType = this::class
 * )
 * ```
 */
fun <T : Any> ImageResponse.toDomain(
    containerClassType: KClass<T>,
    characterName: String? = null
): ImageDomain {
    var actualCharacterName = ""
    if (containerClassType == CharacterResponse::class) {
        if (characterName == null) {
            throw IllegalStateException(
                "Character name cannot be null if container class is of type \"CharacterResponse\". " +
                        "Please make sure that you assign the correct value. Note: blank values are accepted."
            )
        } else {
            actualCharacterName =
                if (characterName.endsWith("s")) "$characterName' Image" else "$characterName's Image"
        }
    }

    return ImageDomain(
        path = this.path,
        extension = this.extension,
        contentDescription = actualCharacterName
    )
}

fun ImageDomain.toResponse(): ImageResponse =
    ImageResponse(path = this.path, extension = this.extension)

fun List<ImageDomain>.toResponse(): List<ImageResponse> =
    this.map { ImageResponse(path = it.path, extension = it.extension) }