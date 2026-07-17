package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.core.common.extensions.fromBytesToMb

/**
 * The configuration data for the uploading of listing images.
 *
 * @param maxImageCount The maximum allowed number of images.
 * @param maxImageSize The maximum allowed size, in bytes, per image.
 * @param supportedImageFileExtensions The supported file extensions.
 */
@Immutable
data class CreateListingBasicDetailsAttachedImagesConfigurationData(
    val maxImageCount: Int,
    val maxImageSize: Long,
    val supportedImageFileExtensions: List<String>
) {

    val maxImageSizeMegaBytes: Long
        get() = maxImageSize.fromBytesToMb()
}
