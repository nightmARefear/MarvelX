package com.alexgreece45.marvelx.network_logic.clients

import com.alexgreece45.marvelx.data.model.cardBrand.CardBrandData
import com.alexgreece45.marvelx.data.model.order.OrderCourierData
import com.alexgreece45.marvelx.data.model.order.OrderItemStatusData
import com.alexgreece45.marvelx.data.model.order.OrderStatusData
import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnStatusData
import com.alexgreece45.marvelx.data.model.order.item.refund.OrderItemRefundStatusData
import com.alexgreece45.marvelx.domain.model_api.order.OrderStatusDomain
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrder
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderCheckoutInfo
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderEstimatedDeliveryDateRange
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderId
import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.network_logic.models_api.order.tracking.ResponseOrderTrackingHistoryItem
import com.alexgreece45.marvelx.network_logic.models_api.order.tracking.ResponseOrderTrackingInfo
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.network_logic.services.OrderService
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCall
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.extensions.minusDays
import com.alexgreece45.marvelx.util.extensions.plusDays
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

class OrderClientImpl(
    private val orderService: OrderService
) : OrderClient {

    override suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>> {
        return ApiResult.Success(
            (1..10).map { index ->
                val currentDate = DateUtil.getUTCInstant()
                val orderDate = currentDate.minusDays(Random.nextLong(2L..4))
                val estimatedDeliveryDateMin = orderDate.plusDays(Random.nextLong(2L..4))
                val estimatedDeliveryDateMax = orderDate.plusDays(Random.nextLong(5L..7))
                val price = BigDecimal.valueOf(Random.nextDouble(10.50, 100.00))

                ResponseOrderPreview(
                    orderId = index.toString(),
                    coverImageUrls = listOf(""),
                    orderDate = orderDate.toString(),
                    estimatedDeliveryDate = ResponseOrderEstimatedDeliveryDateRange(
                        from = estimatedDeliveryDateMin.toString(),
                        to = estimatedDeliveryDateMax.toString()
                    ),
                    status = OrderStatusData.entries[Random.nextInt(
                        0,
                        OrderStatusDomain.entries.lastIndex + 1
                    )].code,
                    price = price.toString()
                )
            }
        )
    }

    override suspend fun getOrderDetails(orderId: String): ApiResult<ResponseOrder> {
        val orderItemsCount = Random.nextInt(2..5)
        var subtotalPrice = 0.0

        var processingCount = 0
        var shippedCount = 0
        var deliveredCount = 0

        val orderItems = (1..orderItemsCount).map { index ->
            val itemPrice = Random.nextDouble(2.5, 8.5)
            subtotalPrice += itemPrice

            val orderItemStatus = OrderItemStatusData.entries.random()
            when (orderItemStatus) {
                OrderItemStatusData.Processing -> processingCount += 1
                OrderItemStatusData.Shipped,
                OrderItemStatusData.DepartedSortingFacility,
                OrderItemStatusData.OutForDelivery -> shippedCount += 1

                OrderItemStatusData.Delivered -> deliveredCount += 1
            }

            val userId = Random.nextInt(1..3).toString()

            ResponseOrderItem(
                id = "B${index + 1}",
                sellerUserId = userId,
                title = "Wow item $index",
                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/$userId/profile-image",
                status = orderItemStatus.code,
                price = itemPrice.toString()
            )
        }
        val shippingPrice = Random.nextDouble(2.5, 8.5)
        val totalPrice = subtotalPrice + shippingPrice

        val orderStatus = when {
            deliveredCount == orderItemsCount -> OrderStatusData.Delivered
            deliveredCount >= 1 -> OrderStatusData.PartiallyDelivered
            shippedCount == orderItemsCount -> OrderStatusData.Shipped
            shippedCount >= 1 -> OrderStatusData.PartiallyShipped
            else -> OrderStatusData.Processing
        }

        val orderDateMinusDays = Random.nextLong(3L..10L)
        val estimatedDeliveryDateMinPlusDays = Random.nextLong(1L..3L)
        val estimatedDeliveryDateMaxPlusDays = Random.nextLong(1L..5L)

        val orderDate = DateUtil.getUTCInstant().minusDays(orderDateMinusDays)
        val estimatedDeliveryDateMin = orderDate.plusDays(estimatedDeliveryDateMinPlusDays)
        val estimatedDeliveryDateMax = orderDate.plusDays(estimatedDeliveryDateMaxPlusDays)

        return ApiResult.Success(
            ResponseOrder(
                orderId = orderId,
                status = orderStatus.code,
                items = orderItems,
                trackingInfo = ResponseOrderTrackingInfo(
                    orderDate = orderDate.toString(),
                    estimatedDeliveryDate = ResponseOrderEstimatedDeliveryDateRange(
                        from = estimatedDeliveryDateMin.toString(),
                        to = estimatedDeliveryDateMax.toString()
                    )
                ),
                checkoutInfo = ResponseOrderCheckoutInfo(
                    firstName = "Alexandros",
                    lastName = "Voliotis",
                    streetAddress = "Agios Minas",
                    city = "Chalkida",
                    zipCode = "34100",
                    maskedCardNumber = "1234",
                    cardBrand = CardBrandData.entries
                        .filterNot { it == CardBrandData.UNKNOWN }
                        .random()
                        .code,
                    paymentDate = orderDate.plusDays(1L).toString(),
                    transactionId = "TID2506565"
                ),
                subtotalPrice = subtotalPrice.toString(),
                shippingPrice = shippingPrice.toString(),
                totalPrice = totalPrice.toString()
            )
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemTrackingHistory> {
        val locations = listOf("Athens", "Thessaloniki", "Larissa", "Chalkida")

        val currentDate = DateUtil.getUTCInstant()
        val estimatedDeliveryFrom = currentDate.plusDays(Random.nextLong(2L..4))
        val estimatedDeliveryTo = estimatedDeliveryFrom.plusDays(Random.nextLong(7L..10))

        val departedSortingFacility = estimatedDeliveryFrom.plusDays(2L)
        val outForDelivery =
            if (Random.nextBoolean()) departedSortingFacility.plusDays(Random.nextLong(1..3L)) else null
        val delivered =
            if (outForDelivery != null && Random.nextBoolean()) outForDelivery.plusDays(1L) else null

        val returnStatus = OrderItemReturnStatusData.entries.random()

        return ApiResult.Success(
            ResponseOrderItemTrackingHistory(
                trackingId = (orderId + orderItemId).toList().shuffled().joinToString(""),
                courier = OrderCourierData.entries.random().code,
                estimatedDelivery = ResponseOrderEstimatedDeliveryDateRange(
                    from = estimatedDeliveryFrom.toString(),
                    to = estimatedDeliveryTo.toString()
                ),
                shipped = ResponseOrderTrackingHistoryItem(
                    location = locations.random(),
                    date = currentDate.minusDays(Random.nextLong(2L..5)).toString()
                ),
                departedSortingFacility = ResponseOrderTrackingHistoryItem(
                    location = locations.random(),
                    date = departedSortingFacility.toString()
                ),
                status = when {
                    delivered != null -> OrderItemStatusData.Delivered.code
                    outForDelivery != null -> OrderItemStatusData.OutForDelivery.code
                    else -> OrderItemStatusData.DepartedSortingFacility.code
                },
                outForDelivery = outForDelivery?.let {
                    ResponseOrderTrackingHistoryItem(
                        location = locations.random(),
                        date = it.toString()
                    )
                },
                delivered = delivered?.let {
                    ResponseOrderTrackingHistoryItem(
                        location = locations.random(),
                        date = it.toString()
                    )
                },
                returnStatus = returnStatus.key,
                returnUntilDate = delivered?.toString(),
                refundStatus = if (returnStatus.isProcessingStatus()) OrderItemRefundStatusData
                    .entries
                    .filterNot { it.isProcessingStatus() }
                    .random()
                    .key else OrderItemRefundStatusData.entries.random().key,
                refundUntilDate = delivered?.toString()
            )
        )
    }

    override suspend fun placeOrder(): ApiResult<ResponseOrderId> {
        return executeApiServiceCall {
            orderService.placeOrder()
        }
    }
}
