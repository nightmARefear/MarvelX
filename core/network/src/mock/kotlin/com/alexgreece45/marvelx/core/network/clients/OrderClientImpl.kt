package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.common.extensions.minusDays
import com.alexgreece45.marvelx.core.common.extensions.plusDays
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.clients.model.CardBrand
import com.alexgreece45.marvelx.core.network.clients.model.OrderCourier
import com.alexgreece45.marvelx.core.network.clients.model.OrderItemCondition
import com.alexgreece45.marvelx.core.network.clients.model.OrderItemReturnReason
import com.alexgreece45.marvelx.core.network.clients.model.OrderItemReturnStatus
import com.alexgreece45.marvelx.core.network.clients.model.OrderItemStatus
import com.alexgreece45.marvelx.core.network.clients.model.OrderStatus
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderCheckoutInfo
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderEstimatedDeliveryDateRange
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.RequestOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnRequest
import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingHistoryItem
import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingInfo
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.OrderService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import org.koin.core.annotation.Single
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

@Single
internal class OrderClientImpl(
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
                    status = OrderStatus.entries[Random.nextInt(
                        0,
                        OrderStatus.entries.lastIndex + 1
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

            val orderItemStatus = OrderItemStatus.entries.random()
            when (orderItemStatus) {
                OrderItemStatus.Processing -> processingCount += 1
                OrderItemStatus.Shipped,
                OrderItemStatus.DepartedSortingFacility,
                OrderItemStatus.OutForDelivery -> shippedCount += 1

                OrderItemStatus.Delivered -> deliveredCount += 1
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
            deliveredCount == orderItemsCount -> OrderStatus.Delivered
            deliveredCount >= 1 -> OrderStatus.PartiallyDelivered
            shippedCount == orderItemsCount -> OrderStatus.Shipped
            shippedCount >= 1 -> OrderStatus.PartiallyShipped
            else -> OrderStatus.Processing
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
                    cardBrand = CardBrand.entries
                        .filterNot { it == CardBrand.UNKNOWN }
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

        val returnStatus = OrderItemReturnStatus.entries.random()

        return ApiResult.Success(
            ResponseOrderItemTrackingHistory(
                trackingId = (orderId + orderItemId).toList().shuffled().joinToString(""),
                courier = OrderCourier.entries.random().code,
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
                    delivered != null -> OrderItemStatus.Delivered.code
                    outForDelivery != null -> OrderItemStatus.OutForDelivery.code
                    else -> OrderItemStatus.DepartedSortingFacility.code
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
                refundStatus = if (returnStatus.isProcessingStatus()) OrderItemReturnStatus
                    .entries
                    .filterNot { it.isProcessingStatus() }
                    .random()
                    .key else OrderItemReturnStatus.entries.random().key,
                refundUntilDate = delivered?.toString()
            )
        )
    }

    override suspend fun placeOrder(): ApiResult<ResponseOrderId> {
        return executeApiServiceCall {
            orderService.placeOrder()
        }
    }

    override suspend fun getOrderItemReturnRequest(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemReturnRequest> {
        return if (orderId == "1") {
            if (orderItemId == "B2") {
                ApiResult.Success(
                    ResponseOrderItemReturnRequest(
                        status = OrderItemReturnStatus.PENDING.key,
                        reason = OrderItemReturnReason.CHANGED_MIND.key,
                        description = "I have changed my mind regarding this item. I want my money back!",
                        attachedImageUrls = listOf(),
                        condition = OrderItemCondition.FAIR.key
                    )
                )
            } else {
                ApiResult.Success(
                    ResponseOrderItemReturnRequest(
                        status = OrderItemReturnStatus.AVAILABLE.key,
                        reason = OrderItemReturnReason.WRONG_ITEM.key,
                        description = "I have received something completely different to what I ordered. I want the correct item!",
                        attachedImageUrls = listOf(),
                        condition = OrderItemCondition.VERY_GOOD.key
                    )
                )
            }
        } else {
            ApiResult.Success(
                ResponseOrderItemReturnRequest(
                    status = OrderItemReturnStatus.REFUNDED.key,
                    reason = OrderItemReturnReason.DEFECTIVE.key,
                    description = "The item that I received was in really bad shape. I want my money back!",
                    attachedImageUrls = listOf(),
                    condition = OrderItemCondition.BAD.key,
                    sellerMessage = "The courier must have messed up the package."
                )
            )
        }
    }

    override suspend fun placeOrderItemReturnRequest(
        orderId: String,
        orderItemId: String,
        requestOrderItemReturn: RequestOrderItemReturn
    ): ApiResult<ResponseOrderItemReturnRequest> {
        return ApiResult.Success(
            ResponseOrderItemReturnRequest(
                status = OrderItemReturnStatus.PENDING.key,
                reason = requestOrderItemReturn.reason,
                description = requestOrderItemReturn.description,
                attachedImageUrls = listOf(""), // TODO: Add dummy image in the future.
                condition = requestOrderItemReturn.conditionCode
            )
        )
    }
}