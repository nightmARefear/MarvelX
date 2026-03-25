package com.alexgreece45.marvelx.data.repository.order

import com.alexgreece45.marvelx.data.model.order.OrderData
import com.alexgreece45.marvelx.data.model.order.OrderPreviewData
import com.alexgreece45.marvelx.data.model.order.item.OrderItemTrackingHistoryData
import com.alexgreece45.marvelx.data.model.order.item.toData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.OrderClient
import kotlinx.coroutines.flow.first
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

class OrderRepositoryImpl(
    private val orderClient: OrderClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : OrderRepository {

    override suspend fun getOrders(): RepositoryResult<List<OrderPreviewData>> {
        return handleApiResult(
            apiResult = orderClient.getOrders(),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getOrderDetails(orderId: String): RepositoryResult<OrderData> {
        return handleApiResult(
            apiResult = orderClient.getOrderDetails(orderId),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): RepositoryResult<OrderItemTrackingHistoryData> {
        return handleApiResult(
            apiResult = orderClient.getOrderItemTrackingHistory(orderId, orderItemId),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun placeOrder(): RepositoryResult<String> {
        return handleApiResult(
            apiResult = orderClient.placeOrder(),
            transformation = { this.orderId },
            ioDispatcher = ioDispatcher
        ).first()
    }
}
