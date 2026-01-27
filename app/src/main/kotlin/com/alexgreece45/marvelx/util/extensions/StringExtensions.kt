package com.alexgreece45.marvelx.util.extensions

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Patterns
import androidx.core.net.toUri
import com.alexgreece45.marvelx.util.constants.ConstantsContentTypes
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Hashes the given [String] with the `MD5` algorithm.
 *
 * @return Returns the `MD5` hash of the given [String] as a [String].
 */
fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}

/**
 * Checks if the given [String] is a valid email address.
 *
 * @return Returns `true` if the given [String] is a valid email address, otherwise `false`.
 */
fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Masks the characters of the given [String] from [startIndex] to
 * [endIndex] with [maskChar].
 */
fun String.maskCharactersWith(startIndex: Int, endIndex: Int, maskChar: Char = '*'): String {
    if (startIndex < 0 || startIndex > endIndex || endIndex >= this.length) return this

    var finalString = ""
    return try {
        this.forEachIndexed { index, character ->
            finalString += if (index < startIndex || index > endIndex) character
            else maskChar
        }
        finalString
    } catch (e: Exception) {
        return this
    }
}

/**
 * Masks the first [n] characters of the given [String] with [maskChar].
 * If the length of the string is smaller or equal to [n], then all the
 * characters are masked.
 */
fun String.maskFirstCharacters(n: Int, maskChar: Char = '*'): String {
    if (this.length <= n) return this.maskCharactersWith(0, this.length - 1, maskChar)
    return this.maskCharactersWith(0, n - 1, maskChar)
}

/**
 * Masks the given email based on the following way:
 *
 * - Before at sign (`@`): masks the characters between the 2nd
 * and 3rd to last.
 *
 * - After at sign (`@`) and before the comma (`.`): masks the
 * characters between the 2nd and 2nd to last.
 *
 * - After the comma (`.`): masks the first character if the
 * length is 2, otherwise the first 2.
 *
 * ### Example:
 *
 * Initial email: johndoe@exampledomain.com
 *
 *
 * johndoe -> j****oe
 *
 * exampledomain -> e***********n
 *
 * gr -> *r
 *
 * com -> **m
 *
 * Masked email: j****oe@e***********n.**m
 */
fun String.maskEmail(): String {
    var maskedEmail = ""

    val parts = this.split("@")
    if (parts.size != 2) return this

    val beforeAtSign = parts[0]
    val afterAtSign = parts[1]

    // Separate the "afterAtSign" string so we get the parts before the comma ('.'), and after it.
    val afterAtSignParts = afterAtSign.split(".")
    if (afterAtSignParts.size != 2) return this

    val afterAtSignAndBeforeComma = afterAtSignParts[0]
    val afterAtSignAndAfterComma = afterAtSignParts[1]

    // Mask before at sign characters.
    maskedEmail += beforeAtSign.maskCharactersWith(1, beforeAtSign.lastIndex - 1) + "@"

    // Mask after at sign and before the comma characters.
    maskedEmail += afterAtSignAndBeforeComma.maskCharactersWith(
        1,
        afterAtSignAndBeforeComma.lastIndex - 1
    ) + "."

    // Mask after at sign and after the comma characters.
    maskedEmail += if (afterAtSignAndAfterComma.length == 2) afterAtSignAndAfterComma.maskCharactersWith(
        0,
        0
    ) else afterAtSignAndAfterComma.maskCharactersWith(0, afterAtSignAndAfterComma.lastIndex - 1)

    return maskedEmail
}

fun String.maskGreekPhoneNumber(): String {
    if (this.length < 10) return this
    return this.maskCharactersWith(3, 7)
}

/**
 * Returns the scheme (either `http` or `https`) for this [String] if it begins with it, otherwise
 * an empty [String].
 */
fun String.getSchemeFromUrl(): String =
    when {
        this.startsWith("https") -> "https"
        this.startsWith("http") -> "http"
        else -> ""
    }

/**
 * Converts this pair to a full name.
 */
fun Pair<String, String>.toFullName(): String = "$first $second"

/**
 * Encodes the given string to a url encoding. This is safe for navigation arguments.
 */
fun String.toUrlEncoding(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

/**
 * Decodes this string from url encoding to UTF-8.
 */
fun String.decodeFromUrl(): String = this.toUri().toString()

/**
 * Checks whether this instance ends with any of the specified suffixes.
 *
 * @param suffixes The suffixes to check against.
 * @param ignoreCase Whether the case should be ignored. By default it's `true`.
 */
fun String.endsWithAnySuffix(suffixes: List<String>, ignoreCase: Boolean = true): Boolean {
    return suffixes.any { suffix ->
        this.endsWith(
            suffix = suffix,
            ignoreCase = ignoreCase
        )
    }
}

/**
 * Converts this file name into a [Bitmap.CompressFormat] based on the extension type. Only JPG and
 * PNG images are supported.
 */
fun String.toCompressFormat(): CompressFormat? {
    return when {
        this.endsWithAnySuffix(
            suffixes = ConstantsContentTypes.JPEG_FORMATS
        ) -> CompressFormat.JPEG

        this.endsWithAnySuffix(ConstantsContentTypes.PNG_FORMATS) -> CompressFormat.PNG

        else -> null
    }
}

/**
 * Returns the file name extension.
 */
fun String.getFileNameExtension(): String =
    this.substringAfterLast(delimiter = ".", missingDelimiterValue = "").lowercase()

/**
 * Returns the media type from the given file name. If the extension is not supported, `null` is
 * returned instead.
 */
fun String.getMediaTypeFromFileName(): MediaType? {
    return when (this.getFileNameExtension()) {
        "jpg", "jpeg" -> "image/jpeg".toMediaTypeOrNull()
        "png" -> "image/png".toMediaTypeOrNull()
        else -> null
    }
}
