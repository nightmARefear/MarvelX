package com.alexgreece45.marvelx.domain.interactors.order

import com.alexgreece45.marvelx.data.repository.order.OrderRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.model_api.order.OrderDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemTrackingHistoryDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain

interface OrderInteractor {

    suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>>
    suspend fun getOrderDetails(orderId: String): InteractorPartialState<OrderDomain>
    suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): InteractorPartialState<OrderItemTrackingHistoryDomain>

    suspend fun placeOrder(): InteractorPartialState<String>
}

class OrderInteractorImpl(
    private val orderRepository: OrderRepository
) : OrderInteractor {

    override suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrders(),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getOrderDetails(orderId: String): InteractorPartialState<OrderDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderDetails(orderId),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: String,
        orderItemId: String
    ): InteractorPartialState<OrderItemTrackingHistoryDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderItemTrackingHistory(
                orderId = orderId,
                orderItemId = orderItemId
            ),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun placeOrder(): InteractorPartialState<String> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.placeOrder(),
            transformation = { this }
        )
    }
}
