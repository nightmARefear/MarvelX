package com.alexgreece45.marvelx.data.repository.order

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.network.clients.OrderClient
import com.alexgreece45.marvelx.data.model.order.OrderData
import com.alexgreece45.marvelx.data.model.order.OrderPreviewData
import com.alexgreece45.marvelx.data.model.order.item.OrderItemTrackingHistoryData
import com.alexgreece45.marvelx.data.model.order.item.toData
import com.alexgreece45.marvelx.data.model.order.toData
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface OrderRepository {

    suspend fun getOrders(): RepositoryResult<List<OrderPreviewData>>
    suspend fun getOrderDetails(orderId: String): RepositoryResult<OrderData>
    suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): RepositoryResult<OrderItemTrackingHistoryData>

    suspend fun placeOrder(): RepositoryResult<String>
}

@Single
internal class OrderRepositoryImpl(
    private val orderClient: OrderClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : OrderRepository {

    override suspend fun getOrders(): RepositoryResult<List<OrderPreviewData>> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrders(),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderDetails(orderId: String): RepositoryResult<OrderData> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrderDetails(orderId),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): RepositoryResult<OrderItemTrackingHistoryData> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrderItemTrackingHistory(orderId, orderItemId),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun placeOrder(): RepositoryResult<String> {
        return handleApiResultNoFlow(
            apiResult = orderClient.placeOrder(),
            transformation = { this.orderId },
            ioDispatcher = ioDispatcher
        )
    }
}