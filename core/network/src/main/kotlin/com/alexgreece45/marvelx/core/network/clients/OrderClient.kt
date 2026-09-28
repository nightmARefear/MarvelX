package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.order.RequestPlaceOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.RequestOrderItemReturnResponse
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnId
import com.alexgreece45.marvelx.core.network.model.api.order.transaction.ResponseTransaction
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import okhttp3.MultipartBody

interface OrderClient {
    suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>>
    suspend fun getOrderDetails(orderId: String): ApiResult<ResponseOrder>
    suspend fun getOrderItemDetails(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItem>

    suspend fun markOrderItemAsShipped(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItem>

    suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemTrackingHistory>

    suspend fun placeOrder(requestPlaceOrder: RequestPlaceOrder): ApiResult<ResponseOrderId>

    suspend fun getPurchases(page: Int): ApiResult<List<ResponseTransaction>>
    suspend fun getSales(page: Int): ApiResult<List<ResponseTransaction>>

    suspend fun getOrderItemReturn(
        orderId: String,
        orderItemId: String,
    ): ApiResult<ResponseOrderItemReturn>

    suspend fun submitOrderItemReturn(
        orderId: String,
        orderItemId: String,
        requestOrderItemReturn: MultipartBody
    ): ApiResult<ResponseOrderItemReturnId>

    suspend fun getOrderItemReturnById(
        orderItemReturnId: String
    ): ApiResult<ResponseOrderItemReturn>

    suspend fun respondToOrderItemReturn(
        orderItemReturnId: String,
        request: RequestOrderItemReturnResponse
    ): ApiResult<ResponseOrderItemReturn>
}
