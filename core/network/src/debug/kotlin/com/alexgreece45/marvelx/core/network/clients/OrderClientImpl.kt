package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.order.RequestPlaceOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnId
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.OrderService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import okhttp3.MultipartBody
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

    override suspend fun getOrderItemDetails(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItem> {
        return executeApiServiceCall { orderService.getOrderItemDetails(orderId, orderItemId) }
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

    override suspend fun placeOrder(requestPlaceOrder: RequestPlaceOrder): ApiResult<ResponseOrderId> {
        return executeApiServiceCall {
            orderService.placeOrder(requestPlaceOrder)
        }
    }

    override suspend fun getOrderItemReturn(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemReturn> {
        return executeApiServiceCall {
            orderService.getOrderItemReturn(
                orderId = orderId,
                orderItemId = orderItemId
            )
        }
    }

    override suspend fun submitOrderItemReturn(
        orderId: String,
        orderItemId: String,
        requestOrderItemReturn: MultipartBody
    ): ApiResult<ResponseOrderItemReturnId> {
        return executeApiServiceCall {
            orderService.submitOrderItemReturn(
                orderId = orderId,
                orderItemId = orderItemId,
                body = requestOrderItemReturn
            )
        }
    }

    override suspend fun getOrderItemReturnById(orderItemReturnId: String): ApiResult<ResponseOrderItemReturn> {
        return executeApiServiceCall {
            orderService.getOrderItemReturnById(orderItemReturnId = orderItemReturnId)
        }
    }
}