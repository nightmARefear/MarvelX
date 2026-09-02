package com.alexgreece45.marvelx.domain.interactors.order

import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.data.repository.order.OrderRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.model_api.order.OrderDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemTrackingHistoryDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.toData
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import org.koin.core.annotation.Factory

interface OrderInteractor {

    suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>>
    suspend fun getOrderDetails(orderId: OrderId): InteractorPartialState<OrderDomain>
    suspend fun getOrderItemDetails(
        orderId: OrderId,
        orderItemId: OrderId
    ): InteractorPartialState<OrderItemDomain>

    suspend fun getOrderItemTrackingHistory(
        orderId: OrderIdDomain,
        orderItemId: OrderIdDomain
    ): InteractorPartialState<OrderItemTrackingHistoryDomain>

    suspend fun placeOrder(): InteractorPartialState<OrderIdDomain>
}

@Factory
internal class OrderInteractorImpl(
    private val orderRepository: OrderRepository
) : OrderInteractor {

    override suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrders(),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getOrderDetails(orderId: OrderId): InteractorPartialState<OrderDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderDetails(orderId),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun getOrderItemDetails(
        orderId: OrderId,
        orderItemId: OrderId
    ): InteractorPartialState<OrderItemDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderItemDetails(orderId, orderItemId),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun getOrderItemTrackingHistory(
        orderId: OrderIdDomain,
        orderItemId: OrderIdDomain
    ): InteractorPartialState<OrderItemTrackingHistoryDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderItemTrackingHistory(
                orderId = orderId.toData(),
                orderItemId = orderItemId.toData()
            ),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun placeOrder(): InteractorPartialState<OrderIdDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.placeOrder(),
            transformation = { this.toDomain() }
        )
    }
}