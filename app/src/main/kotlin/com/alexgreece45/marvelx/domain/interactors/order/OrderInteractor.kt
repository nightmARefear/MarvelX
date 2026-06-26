package com.alexgreece45.marvelx.domain.interactors.order

import com.alexgreece45.marvelx.data.repository.order.OrderRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.model_api.order.OrderDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemTrackingHistoryDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.toData
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import org.koin.core.annotation.Factory

interface OrderInteractor {

    suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>>
    suspend fun getOrderDetails(orderId: OrderIdDomain): InteractorPartialState<OrderDomain>
    suspend fun getOrderItemTrackingHistory(
        orderId: OrderIdDomain,
        orderItemId: OrderIdDomain
    ): InteractorPartialState<OrderItemTrackingHistoryDomain>

    suspend fun placeOrder(): InteractorPartialState<String>
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

    override suspend fun getOrderDetails(orderId: OrderIdDomain): InteractorPartialState<OrderDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getOrderDetails(orderId.toData()),
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

    override suspend fun placeOrder(): InteractorPartialState<String> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.placeOrder(),
            transformation = { this }
        )
    }
}