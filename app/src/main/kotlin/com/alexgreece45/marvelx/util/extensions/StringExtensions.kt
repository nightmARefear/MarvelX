package com.alexgreece45.marvelx.util.extensions

import androidx.core.net.toUri
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
 * Masks the characters of the given [String] from [startIndex] to
 * [endIndex] with [maskChar].
 */
fun String.maskCharactersWith(startIndex: Int, endIndex: Int, maskChar: Char = '*'): String {
    if (startIndex !in 0..endIndex || endIndex >= this.length) return this

    var finalString = ""
    return try {
        this.forEachIndexed { index, character ->
            finalString += if (index !in startIndex..endIndex) character
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
 * Safely encodes the given string using URL encoding. This is safe for navigation arguments. If an
 * exception occurs, [defaultValue] is returned instead.
 */
fun String.toSafeUrlEncoding(defaultValue: String): String =
    runCatching { this.toUrlEncoding() }.getOrDefault(defaultValue)

/**
 * Decodes this string from url encoding to UTF-8.
 */
fun String.decodeFromUrl(): String = this.toUri().toString()

/**
 * Safely decodes this url encoded string to UTF-8. If an exception occurs, [defaultValue] is
 * returned instead.
 */
fun String.safeDecodeFromUrl(defaultValue: String): String =
    runCatching { this.decodeFromUrl() }.getOrDefault(defaultValue)

/**
 * Returns this string with the euro symbol at the end.
 */
fun String.withEuroSymbol(): String = "$this€"
