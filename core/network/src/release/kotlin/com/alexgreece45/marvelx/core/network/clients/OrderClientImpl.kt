package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.OrderService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import org.koin.core.annotation.Single

@Single
internal class OrderClientImpl(
    private val orderService: OrderService
) : OrderClient {

    override suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>> {
        return executeApiServiceCall { orderService.getOrders() }
    }

    override suspend fun getOrderDetails(orderId: String): ApiResult<ResponseOrder> {
        return executeApiServiceCall { orderService.getOrderDetails(orderId) }
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemTrackingHistory> {
        return executeApiServiceCall {
            orderService.getOrderItemTrackingHistory(
                orderId = orderId,
                orderItemId = orderItemId
            )
        }
    }

    override suspend fun placeOrder(): ApiResult<ResponseOrderId> {
        return executeApiServiceCall {
            orderService.placeOrder()
        }
    }
}