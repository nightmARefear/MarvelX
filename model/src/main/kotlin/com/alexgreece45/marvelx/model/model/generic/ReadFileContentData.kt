package com.alexgreece45.marvelx.model.model.generic

/**
 * Contains all the data for a file that has been read.
 */
data class ReadFileContentData(
    val fileName: String,
    val fileContent: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadFileContentData

        if (fileName != other.fileName) return false
        if (!fileContent.contentEquals(other.fileContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + fileContent.contentHashCode()
        return result
    }
}