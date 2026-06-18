package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import okhttp3.MultipartBody

interface OrderClient {

    suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>>
    suspend fun getOrderDetails(orderId: String): ApiResult<ResponseOrder>
    suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemTrackingHistory>

    suspend fun placeOrder(): ApiResult<ResponseOrderId>

    suspend fun getOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
    ): ApiResult<ResponseOrderItemReturn>


    suspend fun placeOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
        requestOrderItemReturn: MultipartBody
    ): ApiResult<ResponseOrderItemReturn>
}