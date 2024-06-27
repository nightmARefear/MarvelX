package com.alexgreece45.marvelx.util

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.alexgreece45.marvelx.util.extensions.toByteArray
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

/**
 * Analyzes an image to detect QR codes and invokes the appropriate callback based on the result.
 *
 * This class implements the `ImageAnalysis.Analyzer` interface to process images from the CameraX
 * ImageAnalysis use case. It supports YUV image formats and uses a `MultiFormatReader` to decode
 * QR codes from the image data.
 *
 * @param onQrCodeScanned         Callback function invoked when a QR code is successfully scanned.
 * The scanned text is passed as an argument.
 * @param onQrCodeScannedFailed   Callback function invoked when QR code scanning fails. An error
 * message (or an empty string if no message is available) is passed as an argument.
 */
internal class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit,
    private val onQrCodeScannedFailed: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
    )

    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBitmap)
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                onQrCodeScannedFailed(e.localizedMessage.orEmpty())
            } finally {
                image.close()
            }
        }
    }
}
