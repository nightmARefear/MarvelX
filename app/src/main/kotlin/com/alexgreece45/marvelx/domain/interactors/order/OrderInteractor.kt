package com.alexgreece45.marvelx.domain.interactors.order

import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.data.model.order.PlaceOrderCheckoutDetails
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
import com.alexgreece45.marvelx.domain.model_api.order.transaction.TransactionDomain
import com.alexgreece45.marvelx.domain.model_api.order.transaction.toDomain
import org.koin.core.annotation.Factory

interface OrderInteractor {

    suspend fun getOrders(): InteractorPartialState<List<OrderPreviewDomain>>
    suspend fun getOrderDetails(orderId: OrderId): InteractorPartialState<OrderDomain>
    suspend fun getOrderItemDetails(
        orderId: OrderId,
        orderItemId: OrderId
    ): InteractorPartialState<OrderItemDomain>

    suspend fun markOrderItemAsShipped(
        orderId: OrderId,
        orderItemId: OrderId
    ): InteractorPartialState<OrderItemDomain>

    suspend fun getOrderItemTrackingHistory(
        orderId: OrderIdDomain,
        orderItemId: OrderIdDomain
    ): InteractorPartialState<OrderItemTrackingHistoryDomain>

    suspend fun placeOrder(
        listingIds: List<String>,
        placeOrderCheckoutDetails: PlaceOrderCheckoutDetails
    ): InteractorPartialState<OrderIdDomain>

    suspend fun getPurchases(page: Int): InteractorPartialState<List<TransactionDomain>>
    suspend fun getSales(page: Int): InteractorPartialState<List<TransactionDomain>>
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

    override suspend fun markOrderItemAsShipped(
        orderId: OrderId,
        orderItemId: OrderId
    ): InteractorPartialState<OrderItemDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.markOrderItemAsShipped(orderId, orderItemId),
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

    override suspend fun placeOrder(
        listingIds: List<String>,
        placeOrderCheckoutDetails: PlaceOrderCheckoutDetails
    ): InteractorPartialState<OrderIdDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.placeOrder(
                listingIds = listingIds,
                placeOrderCheckoutDetails = placeOrderCheckoutDetails
            ),
            transformation = { this.toDomain() }
        )
    }

    override suspend fun getPurchases(page: Int): InteractorPartialState<List<TransactionDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getPurchases(page),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getSales(page: Int): InteractorPartialState<List<TransactionDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = orderRepository.getSales(page),
            transformation = { this.map { it.toDomain() } }
        )
    }
}