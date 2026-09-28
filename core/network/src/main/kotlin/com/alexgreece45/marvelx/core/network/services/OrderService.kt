package com.alexgreece45.marvelx.core.network.services

import com.alexgreece45.marvelx.core.network.model.api.order.RequestPlaceOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.RequestOrderItemReturnResponse
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnEligibility
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnId
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

internal interface OrderService {

    @GET("/api/private/marvelx/orders")
    suspend fun getOrders(): Response<List<ResponseOrderPreview>>

    @POST("/api/private/marvelx/orders")
    suspend fun placeOrder(@Body requestPlaceOrder: RequestPlaceOrder): Response<ResponseOrderId>

    @GET("/api/private/marvelx/orders/{orderId}")
    suspend fun getOrderDetails(@Path(value = "orderId") orderId: String): Response<ResponseOrder>

    @GET("/api/private/marvelx/orders/{orderId}/{orderItemId}/details")
    suspend fun getOrderItemDetails(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItem>

    @PATCH("/api/private/marvelx/orders/{orderId}/{orderItemId}/ship")
    suspend fun markOrderItemAsShipped(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItem>

    @GET("/api/private/marvelx/orders/{orderId}/{orderItemId}/return-eligibility")
    suspend fun getOrderItemReturnEligibility(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItemReturnEligibility>

    @GET("/api/private/marvelx/orders/{orderId}/{orderItemId}/tracking-history")
    suspend fun getOrderItemTrackingHistory(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItemTrackingHistory>

    @GET("/api/private/marvelx/orders/{orderId}/{orderItemId}/return-request")
    suspend fun getOrderItemReturn(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
    ): Response<ResponseOrderItemReturn>

    @POST("/api/private/marvelx/orders/{orderId}/{orderItemId}/return-request")
    suspend fun submitOrderItemReturn(
        @Path(value = "orderId") orderId: String,
        @Path(value = "orderItemId") orderItemId: String,
        @Body body: MultipartBody
    ): Response<ResponseOrderItemReturnId>

    @GET("/api/private/marvelx/return-requests/{orderItemReturnId}")
    suspend fun getOrderItemReturnById(
        @Path(value = "orderItemReturnId") orderItemReturnId: String
    ): Response<ResponseOrderItemReturn>

    @PATCH("/api/private/marvelx/return-requests/{orderItemReturnId}/respond")
    suspend fun respondToOrderItemReturn(
        @Path(value = "orderItemReturnId") orderItemReturnId: String,
        @Body body: RequestOrderItemReturnResponse
    ): Response<ResponseOrderItemReturn>
}