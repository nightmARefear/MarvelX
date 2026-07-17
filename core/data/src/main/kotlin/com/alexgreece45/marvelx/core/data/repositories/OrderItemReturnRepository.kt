package com.alexgreece45.marvelx.core.data.repositories

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.MultipartBuilder
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartItem
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.toMultipartResult
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnCondition
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnReason
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnResult
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState
import com.alexgreece45.marvelx.core.data.model.order.item.toDataState
import com.alexgreece45.marvelx.core.network.clients.OrderClient
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface OrderItemReturnRepository {

    suspend fun getOrderItemReturn(
        orderId: OrderId,
        orderItemId: OrderId,
    ): RepositoryResult<OrderItemReturnState>

    suspend fun submitOrderItemReturn(
        orderId: OrderId,
        orderItemId: OrderId,
        returnReason: OrderItemReturnReason,
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

    private val loggerTag = "OrderItemReturnRepository"

    override suspend fun getOrderItemReturn(
        orderId: OrderId,
        orderItemId: OrderId,
    ): RepositoryResult<OrderItemReturnState> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrderItemReturn(
                orderId = orderId.value,
                orderItemId = orderItemId.value
            ),
            transformation = { this.toDataState() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun submitOrderItemReturn(
        orderId: OrderId,
        orderItemId: OrderId,
        returnReason: OrderItemReturnReason,
        requestRefund: Boolean,
        attachedImages: List<AttachedFileContentData>,
        description: String,
        itemCondition: OrderItemReturnCondition
    ): OrderItemReturnResult {
        val multipartBuilderResult = multipartBuilder.constructMultipartBody(
            buildList {
                add(
                    MultipartItem.Text(
                        partName = "returnReason",
                        value = returnReason.key
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
                    apiResult = orderClient.submitOrderItemReturn(
                        orderId = orderId.value,
                        orderItemId = orderItemId.value,
                        requestOrderItemReturn = multipartBuilderResult.data
                    ),
                    transformation = { this.toDataState() },
                    ioDispatcher = ioDispatcher,
                    logError = { errorMessage -> appLogger.e(loggerTag) { errorMessage } }
                ).toMultipartResult()
            }
        }
    }
}