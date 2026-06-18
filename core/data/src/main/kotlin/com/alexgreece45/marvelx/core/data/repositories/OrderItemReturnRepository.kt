package com.alexgreece45.marvelx.core.data.repositories

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.extensions.LOGGER_TAG
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.MultipartBuilder
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartItem
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.toMultipartResult
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnCondition
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnResult
import com.alexgreece45.marvelx.core.data.model.order.item.toData
import com.alexgreece45.marvelx.core.network.clients.OrderClient
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface OrderItemReturnRepository {

    suspend fun getOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
    ): RepositoryResult<ResponseOrderItemReturn>

    suspend fun placeOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
        reason: String,
        requestRefund: Boolean,
        attachedImages: List<AttachedFileContentData>,
        description: String,
        itemCondition: OrderItemReturnCondition
    ): OrderItemReturnResult
}

@Single
internal class OrderItemReturnRepositoryImpl(
    private val orderClient: OrderClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val multipartBuilder: MultipartBuilder,
    private val appLogger: AppLogger
) : OrderItemReturnRepository {

    override suspend fun getOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
    ): RepositoryResult<ResponseOrderItemReturn> {
        return handleApiResultNoTransformationNoFlow(
            apiResult = orderClient.getOrderItemReturnRequest(
                orderId = orderId,
                orderItemId = orderItemId
            ),
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun placeOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
        reason: String,
        requestRefund: Boolean,
        attachedImages: List<AttachedFileContentData>,
        description: String,
        itemCondition: OrderItemReturnCondition
    ): OrderItemReturnResult {
        val multipartBuilderResult = multipartBuilder.constructMultipartBody(
            buildList {
                add(
                    MultipartItem.Text(
                        partName = "reason",
                        value = reason
                    )
                )
                add(
                    MultipartItem.Text(
                        partName = "requestRefund",
                        value = requestRefund.toString()
                    )
                )
                attachedImages.forEachIndexed { index, attachedFileContentData ->
                    add(
                        MultipartItem.Image(
                            partName = "images",
                            fileName = "image_${index + 1}",
                            imageFileData = attachedFileContentData
                        )
                    )
                }
                add(
                    MultipartItem.Text(
                        partName = "description",
                        value = description
                    )
                )
                add(
                    MultipartItem.Text(
                        partName = "itemCondition",
                        value = itemCondition.key
                    )
                )
            }
        )

        return when (multipartBuilderResult) {
            is MultipartBuilderResult.GenericError -> MultipartResult.GenericError(
                multipartBuilderResult.errorMessage
            )

            MultipartBuilderResult.ImageProcessingError -> MultipartResult.ImageProcessingError
            is MultipartBuilderResult.Success -> {
                handleApiResultNoFlow(
                    apiResult = orderClient.placeOrderItemReturnRequest(
                        orderId = orderId,
                        orderItemId = orderItemId,
                        requestOrderItemReturn = multipartBuilderResult.data
                    ),
                    transformation = { this.toData() },
                    ioDispatcher = ioDispatcher,
                    logError = { errorMessage -> appLogger.e(LOGGER_TAG) { errorMessage } }
                ).toMultipartResult()
            }
        }
    }
}