package com.alexgreece45.marvelx.network_logic.services

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrder
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderId
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @GET("/api/private/marvelx/orders")
    suspend fun getOrders(): Response<List<ResponseOrderPreview>>

    @POST("/api/private/marvelx/orders")
    suspend fun placeOrder(): Response<ResponseOrderId>

    @GET("/api/private/marvelx/orders/{orderId}")
    suspend fun getOrderDetails(@Path(value = "orderId") orderId: String): Response<ResponseOrder>

    @GET("/api/private/marvelx/orders/{orderId}/{orderItemId}/tracking-history")
    suspend fun getOrderItemTrackingHistory(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItemTrackingHistory>
}
