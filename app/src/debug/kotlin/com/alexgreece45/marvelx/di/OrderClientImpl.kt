package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.network_logic.clients.OrderClient
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrder
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderId
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.network_logic.services.OrderService
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCall

class OrderClientImpl(
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
