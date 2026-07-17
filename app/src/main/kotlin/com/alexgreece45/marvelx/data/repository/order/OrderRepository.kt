package com.alexgreece45.marvelx.data.repository.order

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.model.order.OrderId
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
    suspend fun getOrderDetails(orderId: OrderId): RepositoryResult<OrderData>
    suspend fun getOrderItemTrackingHistory(
        orderId: OrderId,
        orderItemId: OrderId
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

    override suspend fun getOrderDetails(orderId: OrderId): RepositoryResult<OrderData> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrderDetails(orderId.value),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemTrackingHistoryData> {
        return handleApiResultNoFlow(
            apiResult = orderClient.getOrderItemTrackingHistory(orderId.value, orderItemId.value),
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