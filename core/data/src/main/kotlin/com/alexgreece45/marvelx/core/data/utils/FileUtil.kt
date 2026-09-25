package com.alexgreece45.marvelx.core.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.graphics.scale
import com.alexgreece45.marvelx.core.common.constants.ConstantsContentTypes
import com.alexgreece45.marvelx.core.common.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.model.model.generic.ReadFileContentData
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

object FileUtil {

    /**
     * The maximum width or height, in pixels, of an image prepared for upload. Larger images are
     * scaled down so that neither dimension exceeds it.
     */
    private const val MAX_UPLOAD_IMAGE_DIMENSION = 2048

    /**
     * Returns whether the file with the specified URI is a decodable image. Only the image header is
     * read, so no memory is allocated for the pixels.
     */
    fun isImageFile(context: Context, uri: Uri): Boolean = decodeImageBounds(context, uri) != null

    /**
     * Attempts to locate the file with the specified file URI and if found decodes it into a bitmap
     * whose longest side is at most [maxDimension], rotated upright according to its EXIF
     * orientation.
     */
    fun decodeSampledBitmapFromFileUri(
        context: Context,
        uri: Uri,
        maxDimension: Int = MAX_UPLOAD_IMAGE_DIMENSION
    ): Bitmap? {
        return try {
            val bounds = decodeImageBounds(context, uri) ?: return null
            val options = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(
                    width = bounds.outWidth,
                    height = bounds.outHeight,
                    maxDimension = maxDimension
                )
            }
            val sampledBitmap = context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
                ?: return null

            sampledBitmap
                .scaledDownTo(maxDimension)
                .rotatedBy(readExifRotationDegrees(context, uri))
        } catch (_: Exception) {
            null
        } catch (_: OutOfMemoryError) {
            null
        }
    }

    /**
     * Retrieves advanced data of a file. More specifically, its raw bytes.
     */
    fun getAdvancedFileContentData(
        file: AttachedFileContentData,
        context: Context
    ): ReadFileContentData? {
        val compressFormat = file.name.toCompressFormat() ?: return null

        val bitmap = decodeSampledBitmapFromFileUri(context, file.uri) ?: return null
        val fileContent = bitmap.compressImage(
            compressFormat = compressFormat
        )
        bitmap.recycle()

        return ReadFileContentData(
            fileName = file.name,
            fileContent = fileContent
        )
    }

    /**
     * Reads only the dimensions of the image with the specified URI. Returns `null` if the file
     * could not be read or isn't an image.
     */
    private fun decodeImageBounds(context: Context, uri: Uri): BitmapFactory.Options? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            // With `inJustDecodeBounds` the returned bitmap is always null; only `options` is filled.
            inputStream.use { BitmapFactory.decodeStream(it, null, options) }
            options.takeIf { it.outWidth > 0 && it.outHeight > 0 }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Returns the largest power-of-two sample size that still keeps the longest side at least
     * [maxDimension], so that the final scaling step only ever shrinks the image.
     */
    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        val longestSide = maxOf(width, height)
        var inSampleSize = 1
        while (longestSide / (inSampleSize * 2) >= maxDimension) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    /**
     * Returns the clockwise rotation, in degrees, stored in the EXIF orientation of the image with
     * the specified URI, or `0` if there is none.
     */
    private fun readExifRotationDegrees(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                when (
                    ExifInterface(inputStream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                ) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (_: Exception) {
            0
        }
    }

    /**
     * Scales this instance down so that its longest side is at most [maxDimension]. The original
     * bitmap is recycled if a new one is created.
     */
    private fun Bitmap.scaledDownTo(maxDimension: Int): Bitmap {
        val longestSide = maxOf(width, height)
        if (longestSide <= maxDimension) return this

        val scale = maxDimension.toFloat() / longestSide
        val scaledBitmap = this.scale(
            (width * scale).roundToInt().coerceAtLeast(1),
            (height * scale).roundToInt().coerceAtLeast(1)
        )
        if (scaledBitmap !== this) recycle()
        return scaledBitmap
    }

    /**
     * Rotates this instance clockwise by [degrees]. The original bitmap is recycled if a new one is
     * created.
     */
    private fun Bitmap.rotatedBy(degrees: Int): Bitmap {
        if (degrees == 0) return this

        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (rotatedBitmap !== this) recycle()
        return rotatedBitmap
    }

    /**
     * Converts this file name into a [Bitmap.CompressFormat] based on the extension type. Only JPG and
     * PNG images are supported.
     */
    private fun String.toCompressFormat(): Bitmap.CompressFormat? {
        return when {
            this.endsWithAnySuffix(
                suffixes = ConstantsContentTypes.JPEG_FORMATS
            ) -> CompressFormat.JPEG

            this.endsWithAnySuffix(ConstantsContentTypes.PNG_FORMATS) -> Bitmap.CompressFormat.PNG

            else -> null
        }
    }

    /**
     * Compresses this instance and returns it as a [ByteArray].
     *
     * @param compressFormat The compression format.
     * @param imageQuality The final image quality compared to the original. It has the default value of
     * `75`.
     */
    private fun Bitmap.compressImage(
        compressFormat: CompressFormat,
        imageQuality: Int = 75
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(compressFormat, imageQuality, outputStream)
        return outputStream.toByteArray()
    }
}
