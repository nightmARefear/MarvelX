package com.alexgreece45.marvelx.core.data.helper.multi_part_builder

import android.content.Context
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.extensions.LOGGER_TAG
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.common.util.withContextCatchable
import com.alexgreece45.marvelx.core.data.extensions.getFileNameExtension
import com.alexgreece45.marvelx.core.data.extensions.getMediaTypeFromFileName
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBodyBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartItem
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartPartBuilderResult
import com.alexgreece45.marvelx.core.data.utils.FileUtil
import com.alexgreece45.marvelx.model.model.generic.ReadFileContentData
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.annotation.Factory
import kotlin.coroutines.CoroutineContext

interface MultipartBuilder {

    /**
     * Construct a multipart body based on the provided items.
     */
    suspend fun constructMultipartBody(items: List<MultipartItem>): MultipartBodyBuilderResult

    /**
     * Constructs a multipart part based on the provided item.
     */
    suspend fun constructMultipartPart(item: MultipartItem): MultipartPartBuilderResult
}

@Factory
internal class MultipartBuilderImpl(
    private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val appLogger: AppLogger,
) : MultipartBuilder {

    override suspend fun constructMultipartBody(items: List<MultipartItem>): MultipartBodyBuilderResult =
        withContextCatchable(
            context = ioDispatcher,
            onError = { errorMessage -> MultipartBuilderResult.GenericError(errorMessage) },
            logError = { errorMessage -> appLogger.e(LOGGER_TAG) { errorMessage } }
        ) {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            items.forEach { item ->
                when (item) {
                    is MultipartItem.Text -> {
                        builder.addFormDataPart(name = item.partName, value = item.value)
                    }

                    is MultipartItem.Image -> {
                        val readFileContentData =
                            FileUtil.getAdvancedFileContentData(item.imageFileData, context)
                                ?: return@withContextCatchable MultipartBuilderResult.ImageProcessingError

                        builder.addPart(
                            createImageBodyPart(
                                readFileContentData = readFileContentData,
                                partName = item.partName,
                                fileName = item.fileName
                            )
                        )
                    }
                }
            }

            MultipartBuilderResult.Success(builder.build())
        }

    override suspend fun constructMultipartPart(item: MultipartItem): MultipartPartBuilderResult {
        return when (item) {
            is MultipartItem.Text -> {
                MultipartBuilderResult.Success(
                    MultipartBody.Part.createFormData(
                        name = item.partName,
                        value = item.value
                    )
                )
            }

            is MultipartItem.Image -> {
                val readFileContentData =
                    FileUtil.getAdvancedFileContentData(item.imageFileData, context)
                        ?: return MultipartBuilderResult.ImageProcessingError

                MultipartBuilderResult.Success(
                    createImageBodyPart(
                        readFileContentData = readFileContentData,
                        partName = item.partName,
                        fileName = item.fileName
                    )
                )
            }
        }
    }

    private fun createImageBodyPart(
        readFileContentData: ReadFileContentData,
        partName: String,
        fileName: String
    ): MultipartBody.Part {
        val imageRequestBody = readFileContentData.fileContent.toRequestBody(
            readFileContentData.fileName.getMediaTypeFromFileName()
        )

        return MultipartBody.Part.createFormData(
            name = partName,
            filename = fileName + "." + readFileContentData.fileName.getFileNameExtension(),
            body = imageRequestBody
        )
    }
}