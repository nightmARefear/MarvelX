package com.alexgreece45.marvelx.data.repository.order

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemData
import com.alexgreece45.marvelx.core.data.model.order.item.toData
import com.alexgreece45.marvelx.core.network.clients.OrderClient
import com.alexgreece45.marvelx.core.network.model.api.order.RequestPlaceOrder
import com.alexgreece45.marvelx.data.model.order.OrderData
import com.alexgreece45.marvelx.data.model.order.OrderPreviewData
import com.alexgreece45.marvelx.data.model.order.PlaceOrderCheckoutDetails
import com.alexgreece45.marvelx.data.model.order.item.OrderItemTrackingHistoryData
import com.alexgreece45.marvelx.data.model.order.item.toData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.data.model.order.toRequest
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface OrderRepository {
    suspend fun getOrders(): RepositoryResult<List<OrderPreviewData>>
    suspend fun getOrderDetails(orderId: OrderId): RepositoryResult<OrderData>
    suspend fun getOrderItemDetails(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemData>

    suspend fun markOrderItemAsShipped(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemData>

    suspend fun getOrderItemTrackingHistory(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemTrackingHistoryData>

    suspend fun placeOrder(
        listingIds: List<String>,
        placeOrderCheckoutDetails: PlaceOrderCheckoutDetails
    ): RepositoryResult<OrderId>
}

@Single
internal class OrderRepositoryImpl(
    private val orderClient: OrderClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : OrderRepository {

    override suspend fun getOrders(): RepositoryResult<List<OrderPreviewData>> {
        return handleApiResultNoFlow(
            apiCall = { orderClient.getOrders() },
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderDetails(orderId: OrderId): RepositoryResult<OrderData> {
        return handleApiResultNoFlow(
            apiCall = { orderClient.getOrderDetails(orderId.value) },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderItemDetails(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemData> {
        return handleApiResultNoFlow(
            apiCall = { orderClient.getOrderItemDetails(orderId.value, orderItemId.value) },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun markOrderItemAsShipped(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemData> {
        return handleApiResultNoFlow(
            apiCall = { orderClient.markOrderItemAsShipped(orderId.value, orderItemId.value) },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: OrderId,
        orderItemId: OrderId
    ): RepositoryResult<OrderItemTrackingHistoryData> {
        return handleApiResultNoFlow(
            apiCall = { orderClient.getOrderItemTrackingHistory(orderId.value, orderItemId.value) },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun placeOrder(
        listingIds: List<String>,
        placeOrderCheckoutDetails: PlaceOrderCheckoutDetails
    ): RepositoryResult<OrderId> {
        val request = RequestPlaceOrder(
            listingIds = listingIds,
            checkoutInfo = placeOrderCheckoutDetails.toRequest()
        )
        return handleApiResultNoFlow(
            apiCall = { orderClient.placeOrder(request) },
            transformation = { OrderId(this.orderId) },
            ioDispatcher = ioDispatcher
        )
    }
}
