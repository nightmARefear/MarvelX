package com.alexgreece45.marvelx.network_logic.clients

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrder
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderId
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult

interface OrderClient {

    suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>>
    suspend fun getOrderDetails(orderId: String): ApiResult<ResponseOrder>
    suspend fun getOrderItemTrackingHistory(orderId: String, orderItemId: String): ApiResult<ResponseOrderItemTrackingHistory>

    suspend fun placeOrder(): ApiResult<ResponseOrderId>
}
