package com.alexgreece45.marvelx.core.security.model.antiReverseEngineering

import java.util.zip.ZipEntry

/**
 * Custom [ZipEntry] data class that contains the name of a single zip entry and its crc. This data
 * class is used to sort the elements based on the [name], so that the final crc consists of the
 * correct order of [crc]s.
 *
 * @param name  The name of this [ZipEntry].
 * @param crc   The crc of this [ZipEntry].
 */
internal data class MyZipEntry(
    val name: String,
    val crc: Long
)
