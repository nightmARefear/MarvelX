package com.alexgreece45.marvelx.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericLazyItemInfoScreen
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.LazyItemErrorScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.checkoutDataCard.CheckoutInfoCard
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.icons.ProcessingRequestIconLottie
import com.alexgreece45.marvelx.ui.components.orderSumamaryCard.OrderSummaryCard
import com.alexgreece45.marvelx.ui.screens.cart.model.CartContract
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.Flow

@Composable
fun CartScreen(
    onEvent: (CartContract.Event) -> Unit,
    state: CartContract.State,
    effect: Flow<CartContract.Effect>,
    onNavigationEffect: (CartContract.Effect.Navigation) -> Unit,
    bottomNavigationBarData: (@Composable () -> BottomNavigationBarData)?,
    orderCheckoutInfo: OrderCheckoutInfoDomain?,
) {

    val context = LocalContext.current

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CartContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is CartContract.Effect.ShowToastMessage -> context.showToast(uiEffect.message)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CartContract.Event.Init(orderCheckoutInfo))
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = orderCheckoutInfo == null,
        toolbarTitle = stringResource(R.string.screen_cart_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData?.invoke(),
        isInnerScreen = orderCheckoutInfo != null
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            if (state.error) {
                item("errorView") {
                    LazyItemContentErrorScreen(
                        errorMessage = stringResource(R.string.screen_cart_error_loading_cart),
                        onRetryClick = { onEvent(CartContract.Event.OnErrorButtonClicked) }
                    )
                }
                return@LazyColumn
            }

            if (state.cartInfo == null || state.cartInfo.items.isEmpty()) {
                item(key = "emptyCartView") {
                    GenericLazyItemInfoScreen(
                        title = stringResource(R.string.screen_cart_empty_cart_title),
                        buttonLabel = stringResource(R.string.screen_cart_empty_cart_button),
                        onButtonClicked = {
                            onEvent(CartContract.Event.OnEmptyCartViewButtonClicked)
                        }
                    )
                }
                return@LazyColumn
            }

            state.orderCheckoutInfo?.let { checkoutInfo ->
                item(key = "checkoutInfoCard") {
                    CheckoutInfoCard(
                        orderCheckoutInfo = checkoutInfo,
                        isShippingDetailsEditable = true,
                        isPaymentDetailsEditable = true,
                        onShippingDetailsEditButtonClicked = {
                            onEvent(CartContract.Event.CartCheckoutEvents.OnEditShippingDetailsButtonClicked)
                        },
                        onPaymentDetailsEditButtonClicked = {
                            onEvent(CartContract.Event.CartCheckoutEvents.OnEditPaymentDetailsButtonClicked)
                        }
                    )
                }
            }

            // Cart items.
            if (state.orderCheckoutInfo == null) {
                items(
                    items = state.cartInfo.items,
                    key = { it.listingId }
                ) { cartItem ->
                    CartItemCard(
                        title = cartItem.title,
                        price = cartItem.priceLabel,
                        coverImageUrl = cartItem.coverImageUrl,
                        favorite = cartItem.favorite,
                        isFavoriteProcessing = cartItem.isFavoriteProcessing,
                        isDeleteProcessing = cartItem.isDeleteProcessing,
                        onClicked = {
                            onEvent(CartContract.Event.CartItemEvents.OnClicked(cartItem.listingId))
                        },
                        onFavoriteButtonClicked = {
                            onEvent(
                                CartContract.Event.CartItemEvents.OnFavoriteButtonClicked(
                                    cartItem.listingId
                                )
                            )
                        },
                        onDeleteButtonClicked = {
                            onEvent(
                                CartContract.Event.CartItemEvents.OnDeleteButtonClicked(
                                    cartItem.listingId
                                )
                            )
                        }
                    )
                }
            } else {
                item(key = "checkoutCartItemsTitle") {
                    Text(
                        text = stringResource(R.string.screen_cart_checkout_items_title),
                        style = MaterialTheme.typography.h6
                    )
                }

                items(
                    items = state.cartInfo.items,
                    key = { it.listingId }
                ) { cartItem ->
                    CheckoutCartItemCard(
                        title = cartItem.title,
                        price = cartItem.priceLabel,
                        coverImageUrl = cartItem.coverImageUrl
                    )
                }
            }

            // Cart summary.
            if (state.cartSummaryError) {
                item(key = "cartSummaryErrorView") {
                    LazyItemErrorScreen(
                        errorMessage = stringResource(R.string.screen_cart_error_loading_cart_summary),
                        modifier = Modifier.fillMaxWidth(),
                        onRetryClick = {
                            onEvent(CartContract.Event.OnCartSummaryErrorButtonClicked)
                        },
                    )
                }
                return@LazyColumn
            }

            item(key = "cartSummaryCard") {
                OrderSummaryCard(
                    title = stringResource(if (state.isCheckout) R.string.screen_cart_checkout_summary_card_title_checkout else R.string.screen_cart_checkout_summary_card_title_overview),
                    subtotalPrice = state.cartInfo.summary.subtotalPriceLabel,
                    shippingPrice = state.cartInfo.summary.shippingPriceLabel,
                    totalPrice = state.cartInfo.summary.totalPriceLabel,
                    additionalContent = {
                        VSpacer.Medium()

                        WrapButton(
                            onClick = { onEvent(CartContract.Event.OnCartSummaryButtonClicked) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative,
                                contentColor = MaterialTheme.customColors.buttonContentWhite
                            )
                        ) {
                            Text(
                                text = if (state.isCheckout) stringResource(
                                    R.string.screen_cart_sticky_bottom_button_pay,
                                    state.cartInfo.summary.totalPriceLabel
                                ) else stringResource(R.string.screen_cart_sticky_bottom_button_proceed)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckoutCartItemCard(
    title: String,
    price: String,
    coverImageUrl: String,
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = { PriceText(price = price) },
        titleContentAlignment = Alignment.CenterVertically,
        trailingIcon = null,
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun CartItemCard(
    title: String,
    price: String,
    coverImageUrl: String,
    favorite: Boolean,
    isFavoriteProcessing: Boolean,
    isDeleteProcessing: Boolean,
    onClicked: () -> Unit,
    onFavoriteButtonClicked: () -> Unit,
    onDeleteButtonClicked: () -> Unit
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = { PriceText(price = price) },
        titleContentAlignment = Alignment.CenterVertically,
        trailingIcon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isFavoriteProcessing) {
                    ProcessingRequestIconLottie()
                } else {
                    ThrottledIconButton(
                        onClick = onFavoriteButtonClicked
                    ) {
                        Icon(
                            painter = painterResource(if (favorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_empty),
                            contentDescription = stringResource(if (favorite) R.string.screen_cart_content_description_remove_from_favorites else R.string.screen_cart_content_description_add_to_favorites),
                            tint = MaterialTheme.customColors.genericRed
                        )
                    }
                }

                if (isDeleteProcessing) {
                    ProcessingRequestIconLottie()
                } else {
                    ThrottledIconButton(
                        onClick = onDeleteButtonClicked
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.screen_cart_error_removing_from_cart),
                            tint = MaterialTheme.customColors.genericRed
                        )
                    }
                }
            }
        },
        onClicked = onClicked
    )
}

@Composable
private fun PriceText(price: String) {
    VSpacer.Medium()

    Text(
        text = price,
        color = MaterialTheme.customColors.genericRed,
        style = MaterialTheme.typography.subtitle1
    )
}