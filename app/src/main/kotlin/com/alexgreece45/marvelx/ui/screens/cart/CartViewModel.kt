package com.alexgreece45.marvelx.ui.screens.cart

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.order.OrderRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.cart.CartInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.mappers.CartItemUiMapper
import com.alexgreece45.marvelx.ui.screens.cart.model.CartContract
import com.alexgreece45.marvelx.ui.screens.cart.model.CartInfoUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CartViewModel(
    private val cartInteractor: CartInteractor,
    private val cartItemUiMapper: CartItemUiMapper,
    private val listingInteractor: ListingInteractor,
    private val resourceProvider: ResourceProvider,
    private val orderRepository: OrderRepository,
) : BaseViewModel<CartContract.Event, CartContract.State, CartContract.Effect>() {

    override fun setInitialState(): CartContract.State = CartContract.State()

    override fun handleEvents(event: CartContract.Event) {
        when (event) {
            is CartContract.Event.Init -> init(event.orderCheckoutInfo)
            CartContract.Event.OnEmptyCartViewButtonClicked -> onEmptyCartViewButtonClicked()
            is CartContract.Event.CartItemEvents.OnClicked -> onCartItemClicked(event.listingId)
            is CartContract.Event.CartItemEvents.OnFavoriteButtonClicked -> onCartItemFavoriteButtonClicked(
                event.listingId
            )

            is CartContract.Event.CartItemEvents.OnDeleteButtonClicked -> onCartItemDeleteButtonClicked(
                event.listingId
            )

            CartContract.Event.CartCheckoutEvents.OnEditShippingDetailsButtonClicked -> onEditShippingDetailsButtonClicked()
            CartContract.Event.CartCheckoutEvents.OnEditPaymentDetailsButtonClicked -> onEditPaymentDetailsButtonClicked()

            CartContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
            CartContract.Event.OnCartSummaryErrorButtonClicked -> onCartSummaryErrorButtonClicked()
            CartContract.Event.OnCartSummaryButtonClicked -> onCartSummaryButtonClicked()
        }
    }

    /**
     * =================================
     * Event functions.
     * =================================
     */

    /**
     * The init function.
     */
    private fun init(orderCheckoutInfoUi: OrderCheckoutInfoDomain?) {
        setState {
            setInitialState().copy(
                orderCheckoutInfo = orderCheckoutInfoUi?.toUi()
            )
        }
        getCartInfo()
    }

    private fun onEmptyCartViewButtonClicked() {
        setEffect {
            CartContract.Effect.Navigation.GoToMarketplaceScreen
        }
    }

    private fun onCartItemClicked(listingId: String) {
        setEffect {
            CartContract.Effect.Navigation.GoToListingDetailsScreen(listingId)
        }
    }

    private fun onCartItemFavoriteButtonClicked(listingId: String) {
        var title = ""
        var favorite = false
        setState {
            copy(
                cartInfo = cartInfo?.copy(
                    items = cartInfo.items.map { cartItem ->
                        if (cartItem.listingId == listingId) {
                            title = cartItem.title
                            favorite = cartItem.favorite
                            cartItem.copy(isFavoriteProcessing = true)
                        } else cartItem
                    }
                )
            )
        }
        viewModelScope.launch {
            val wasAddedOrRemovedFromFavorites =
                if (favorite) listingInteractor.removeFromFavorites(listingId) else listingInteractor.addToFavorites(
                    listingId
                )
            setState {
                copy(
                    cartInfo = cartInfo?.copy(
                        items = cartInfo.items.map { cartItem ->
                            if (cartItem.listingId == listingId) {
                                cartItem.copy(
                                    favorite = if (wasAddedOrRemovedFromFavorites) !favorite else favorite,
                                    isFavoriteProcessing = false
                                )
                            } else cartItem
                        }
                    )
                )
            }

            if (!wasAddedOrRemovedFromFavorites) {
                setEffect {
                    CartContract.Effect.ShowToastMessage(
                        resourceProvider.getString(
                            if (favorite) R.string.screen_listing_details_error_removing_from_favorites else R.string.screen_cart_error_adding_to_favorites,
                            title
                        )
                    )
                }
            }
        }
    }

    private fun onCartItemDeleteButtonClicked(listingId: String) {
        var title = ""
        setState {
            copy(
                cartInfo = cartInfo?.copy(
                    items = cartInfo.items.map { cartItem ->
                        if (cartItem.listingId == listingId) {
                            title = cartItem.title
                            cartItem.copy(isDeleteProcessing = true)
                        } else cartItem
                    }
                )
            )
        }
        viewModelScope.launch {
            val wasCartItemDeleted = cartInteractor.deleteCartItem(listingId)
            setState {
                copy(
                    cartInfo = cartInfo?.copy(
                        items = if (wasCartItemDeleted) cartInfo.items.filterNot { cartItem -> cartItem.listingId == listingId } else cartInfo.items.map { cartItem ->
                            if (cartItem.listingId == listingId) {
                                cartItem.copy(
                                    isDeleteProcessing = false
                                )
                            } else cartItem
                        }
                    )
                )
            }

            if (wasCartItemDeleted) {
                updateCartSummary()
            } else {
                setEffect {
                    CartContract.Effect.ShowToastMessage(
                        resourceProvider.getString(
                            R.string.screen_cart_error_removing_from_cart,
                            title
                        )
                    )
                }
            }
        }
    }

    private fun onEditShippingDetailsButtonClicked() {
        setEffect {
            CartContract.Effect.Navigation.GoToEditCheckoutShippingDetailsScreen
        }
    }

    private fun onEditPaymentDetailsButtonClicked() {
        setEffect {
            CartContract.Effect.Navigation.GoToEditCheckoutPaymentDetailsScreen
        }
    }

    private fun onErrorButtonClicked() {
        getCartInfo()
    }

    private fun onCartSummaryErrorButtonClicked() {
        updateCartSummary()
    }

    private fun onCartSummaryButtonClicked() {
        if (currentState.orderCheckoutInfo == null) {
            setEffect {
                CartContract.Effect.Navigation.GoToCheckoutScreen
            }
        } else {
            placeOrder()
        }
    }

    /**
     * Helper functions.
     */

    /**
     * Retrieves the cart info.
     */
    private fun getCartInfo() {
        setState {
            copy(
                isLoading = true,
                error = false
            )
        }
        viewModelScope.launch {
            when (val partialState = cartInteractor.getCartInfo()) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            isLoading = false,
                            error = true
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val cartItemsUi = cartItemUiMapper.toUiList(partialState.data.items)
                    if (cartItemsUi == null) {
                        setState {
                            copy(
                                isLoading = false,
                                error = true
                            )
                        }
                    } else {
                        setState {
                            copy(
                                isLoading = false,
                                cartInfo = CartInfoUi(
                                    items = cartItemsUi,
                                    summary = partialState.data.summary
                                ),
                                error = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateCartSummary() {
        setState {
            copy(
                isSummaryLoading = true,
                cartSummaryError = false
            )
        }
        viewModelScope.launch {
            when (val partialState = cartInteractor.getCartSummary()) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            isSummaryLoading = false,
                            cartSummaryError = true
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    setState {
                        copy(
                            isSummaryLoading = false,
                            cartInfo = cartInfo?.copy(
                                summary = partialState.data
                            )
                        )
                    }
                }
            }
        }
    }

    private fun placeOrder() {
        setEffect {
            CartContract.Effect.Navigation.GoToCheckoutSuccessScreen("123213321!@#K#J#!@J")
        }
        return // TODO: Remove return statement.

        setState {
            copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            when (val result = orderRepository.placeOrder()) {
                is RepositoryResult.Failed,
                RepositoryResult.NetworkError -> {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    setEffect {
                        CartContract.Effect.ShowToastMessage(resourceProvider.getString(R.string.screen_cart_error_placing_order))
                    }
                }

                is RepositoryResult.Success -> {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    setEffect {
                        CartContract.Effect.Navigation.GoToCheckoutSuccessScreen(result.data)
                    }
                }
            }
        }
    }
}
