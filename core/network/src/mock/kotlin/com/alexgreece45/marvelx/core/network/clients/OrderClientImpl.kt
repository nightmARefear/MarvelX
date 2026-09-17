package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.common.extensions.minusDays
import com.alexgreece45.marvelx.core.common.extensions.plusDays
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.model.CardBrand
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderCheckoutInfo
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderEstimatedDeliveryDateRange
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderId
import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderPreview
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.RequestOrderItemReturnResponse
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnEligibility
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnId
import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingHistoryItem
import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingInfo
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.model.order.OrderCourier
import com.alexgreece45.marvelx.core.network.model.order.OrderStatus
import com.alexgreece45.marvelx.core.network.model.order.item.OrderItemReturnCondition
import com.alexgreece45.marvelx.core.network.model.order.item.OrderItemReturnMethod
import com.alexgreece45.marvelx.core.network.model.order.item.OrderItemReturnReason
import com.alexgreece45.marvelx.core.network.model.order.item.OrderItemReturnStatus
import com.alexgreece45.marvelx.core.network.model.order.item.OrderItemStatus
import com.alexgreece45.marvelx.core.network.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.core.network.utils.MultipartBodyUtils
import okhttp3.MultipartBody
import org.koin.core.annotation.Single
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

@Single
internal class OrderClientImpl : OrderClient {

    override suspend fun getOrders(): ApiResult<List<ResponseOrderPreview>> {
        return ApiResult.Success(
            (1..10).map { index ->
                val currentDate = DateUtil.getUTCInstant()
                val orderDate = currentDate.minusDays(Random.nextLong(2L..4))
                val estimatedDeliveryDateMin =
                    orderDate.plusDays(Random.nextLong(2L..4)).toLocalDate()
                val estimatedDeliveryDateMax =
                    orderDate.plusDays(Random.nextLong(5L..7)).toLocalDate()
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
                orderItemId = "B${index + 1}",
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
        val estimatedDeliveryDateMin =
            orderDate.plusDays(estimatedDeliveryDateMinPlusDays).toLocalDate()
        val estimatedDeliveryDateMax =
            orderDate.plusDays(estimatedDeliveryDateMaxPlusDays).toLocalDate()

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

    override suspend fun getOrderItemDetails(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItem> {
        return OrderClientConstants.getOrderItemDetails(orderItemId = orderItemId)
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

        val returnUntilDate =
            delivered?.toLocalDate()?.plusDays(30L)?.toString() ?: estimatedDeliveryTo.toLocalDate()
                .plusDays(30L).toString()

        return ApiResult.Success(
            ResponseOrderItemTrackingHistory(
                trackingId = (orderId + orderItemId).toList().shuffled().joinToString(""),
                courier = OrderCourier.entries.random().code,
                estimatedDelivery = ResponseOrderEstimatedDeliveryDateRange(
                    from = estimatedDeliveryFrom.toLocalDate().toString(),
                    to = estimatedDeliveryTo.toLocalDate().toString()
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
                returnEligibility = ResponseOrderItemReturnEligibility(
                    returnStatus = returnStatus.key,
                    returnUntilDate = returnUntilDate,
                    refundStatus = if (returnStatus.isProcessingStatus()) OrderItemReturnStatus
                        .entries
                        .filterNot { it.isProcessingStatus() }
                        .random()
                        .key else OrderItemReturnStatus.entries.random().key,
                    refundUntilDate = returnUntilDate
                ),
            )
        )
    }

    override suspend fun placeOrder(): ApiResult<ResponseOrderId> = ApiResult.Success(
        ResponseOrderId("A10001")
    )

    override suspend fun getOrderItemReturn(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseOrderItemReturn> {
        return if (orderId == "1") {
            submittedOrderItemReturn?.let {
                submittedOrderItemReturn = null
                return ApiResult.Success(it)
            }

            if (orderItemId == "B2") {
                ApiResult.Success(OrderClientConstants.responseOrderItemReturns["RET-1000"]!!)
            } else {
                ApiResult.Success(OrderClientConstants.responseOrderItemReturns["RET-1001"]!!)
            }
        } else {
            ApiResult.Success(OrderClientConstants.responseOrderItemReturns["RET-1002"]!!)
        }
    }

    override suspend fun submitOrderItemReturn(
        orderId: String,
        orderItemId: String,
        requestOrderItemReturn: MultipartBody
    ): ApiResult<ResponseOrderItemReturnId> {
        val orderItem = OrderClientConstants.getOrderItemDetails("1").body
        val orderItemReturnId = "RET-1003"
        val requestRefund = MultipartBodyUtils.getMultipartValueByName(
            multipartBody = requestOrderItemReturn,
            targetPartName = "requestRefund"
        )!! == "true"

        submittedOrderItemReturn = ResponseOrderItemReturn(
            orderItemReturnId = orderItemReturnId,
            orderItem = orderItem,
            returnStatus = OrderItemReturnStatus.PENDING.key,
            returnUntilDate = DateUtil.getCurrentLocalDate().plusDays(15).toString(),
            refundStatus = if (requestRefund) OrderItemRefundStatus.PENDING.key else OrderItemRefundStatus.AVAILABLE.key,
            returnReason = MultipartBodyUtils.getMultipartValueByName(
                multipartBody = requestOrderItemReturn,
                targetPartName = "returnReason"
            )!!,
            description = MultipartBodyUtils.getMultipartValueByName(
                multipartBody = requestOrderItemReturn,
                targetPartName = "description"
            ),
            attachedImageUrls = listOf(), // not supported
            condition = MultipartBodyUtils.getMultipartValueByName(
                multipartBody = requestOrderItemReturn,
                targetPartName = "itemCondition"
            )!!,
            sellerMessage = null, // not applicable
            returnMethod = MultipartBodyUtils.getMultipartValueByName(
                multipartBody = requestOrderItemReturn,
                targetPartName = "returnMethod"
            )!!,
            refundAmount = if (MultipartBodyUtils.getMultipartValueByName(
                    multipartBody = requestOrderItemReturn,
                    targetPartName = "requestRefund"
                )!! == "true"
            ) {
                orderItem.price
            } else null,
            submittedAt = DateUtil.getCurrentLocalDate().minusDays(1).toString()
        )

        return ApiResult.Success(ResponseOrderItemReturnId(orderItemReturnId))
    }

    override suspend fun getOrderItemReturnById(orderItemReturnId: String): ApiResult<ResponseOrderItemReturn> {
        return ApiResult.Success(if (submittedOrderItemReturn?.orderItemReturnId == orderItemReturnId) submittedOrderItemReturn!! else OrderClientConstants.responseOrderItemReturns[orderItemReturnId]!!)
    }

    override suspend fun respondToOrderItemReturn(
        orderItemReturnId: String,
        request: RequestOrderItemReturnResponse
    ): ApiResult<ResponseOrderItemReturn> {
        val isSubmitted = submittedOrderItemReturn?.orderItemReturnId == orderItemReturnId
        val current = if (isSubmitted) {
            submittedOrderItemReturn!!
        } else {
            OrderClientConstants.responseOrderItemReturns[orderItemReturnId]!!
        }

        val (newReturnStatus, newRefundStatus) = when (request.action) {
            "reject" -> OrderItemReturnStatus.REJECTED.key to OrderItemRefundStatus.REJECTED.key
            "accept_replace" -> OrderItemReturnStatus.ITEM_REPLACED.key to OrderItemRefundStatus.REJECTED.key
            "accept_refund" -> OrderItemReturnStatus.REFUNDED.key to OrderItemRefundStatus.ACCEPTED.key
            else -> current.returnStatus to current.refundStatus
        }

        val updated = current.copy(
            returnStatus = newReturnStatus,
            refundStatus = newRefundStatus,
            sellerMessage = request.sellerMessage ?: current.sellerMessage
        )

        if (isSubmitted) {
            submittedOrderItemReturn = updated
        } else {
            OrderClientConstants.responseOrderItemReturns[orderItemReturnId] = updated
        }

        return ApiResult.Success(updated)
    }
}

var submittedOrderItemReturn: ResponseOrderItemReturn? = null

private object OrderClientConstants {
    private val responseOrderItem: ResponseOrderItem = getOrderItemDetails("1").body
    val responseOrderItemReturns: HashMap<String, ResponseOrderItemReturn> =
        hashMapOf<String, ResponseOrderItemReturn>().apply {
            putAll(
                listOf(
                    "RET-1000" to ResponseOrderItemReturn(
                        orderItemReturnId = "RET-1000",
                        orderItem = responseOrderItem,
                        returnStatus = OrderItemReturnStatus.PENDING.key,
                        returnUntilDate = DateUtil.getCurrentLocalDate().plusDays(10).toString(),
                        refundStatus = OrderItemRefundStatus.AVAILABLE.key,
                        returnReason = OrderItemReturnReason.NOT_AS_DESCRIBED.key,
                        description = "The item looks nothing like the photos!",
                        attachedImageUrls = listOf(),
                        condition = OrderItemReturnCondition.ORIGINAL_PACKAGING.key
                    ),
                    "RET-1001" to ResponseOrderItemReturn(
                        orderItemReturnId = "RET-1001",
                        orderItem = responseOrderItem,
                        returnStatus = OrderItemReturnStatus.AVAILABLE.key,
                        returnUntilDate = DateUtil.getCurrentLocalDate().plusDays(12).toString(),
                        refundStatus = OrderItemRefundStatus.AVAILABLE.key,
                        returnReason = null,
                        description = null,
                        attachedImageUrls = listOf(),
                        condition = null
                    ),
                    "RET-1002" to ResponseOrderItemReturn(
                        orderItemReturnId = "RET-1002",
                        orderItem = responseOrderItem,
                        returnStatus = OrderItemReturnStatus.REFUNDED.key,
                        returnUntilDate = DateUtil.getCurrentLocalDate().plusDays(8).toString(),
                        refundStatus = OrderItemRefundStatus.ACCEPTED.key,
                        returnReason = OrderItemReturnReason.DEFECTIVE.key,
                        description = "The item that I received was in really bad shape. I want my money back!",
                        attachedImageUrls = listOf(),
                        condition = OrderItemReturnCondition.PACKAGE_OPENED.key,
                        sellerMessage = "The courier must have messed up the package."
                    ),
                    "RET-1003" to ResponseOrderItemReturn(
                        orderItemReturnId = "RET-1003",
                        orderItem = responseOrderItem,
                        returnStatus = OrderItemReturnStatus.PENDING.key,
                        returnUntilDate = DateUtil.getCurrentLocalDate().plusDays(5).toString(),
                        refundStatus = OrderItemRefundStatus.AVAILABLE.key,
                        returnReason = OrderItemReturnReason.WRONG_ITEM.key,// requestOrderItemReturn.reason,
                        description = "RETURN_DESCRIPTION",//requestOrderItemReturn.description,
                        attachedImageUrls = listOf(""), // TODO: Add dummy image in the future.
                        condition = OrderItemReturnCondition.PACKAGE_OPENED.key, //requestOrderItemReturn.conditionCode
                        returnMethod = OrderItemReturnMethod.PREPAID_RETURN_LABEL.key,
                        refundAmount = Random.nextDouble(5.0, 100.0).toString(),
                        submittedAt = DateUtil.getCurrentLocalDate().minusDays(1).toString()
                    )
                )
            )
        }


    fun getOrderItemDetails(orderItemId: String): ApiResult.Success<ResponseOrderItem> {
        val sellerUserId = if (orderItemId.toInt() % 2 == 0) "1" else "2"
        return ApiResult.Success(
            ResponseOrderItem(
                orderItemId = orderItemId,
                sellerUserId = sellerUserId,
                title = "wow cup!!!",
                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/$sellerUserId/profile-image",
                status = OrderItemStatus.Processing.code,
                price = Random.nextDouble(5.0, 100.0).toString()
            )
        )
    }
}