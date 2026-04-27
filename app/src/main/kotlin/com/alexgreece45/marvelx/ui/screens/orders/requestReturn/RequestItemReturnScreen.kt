package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.Flow

@Composable
fun RequestItemReturnScreen(
    onEvent: (RequestItemReturnContract.Event) -> Unit,
    state: RequestItemReturnContract.State,
    effect: Flow<RequestItemReturnContract.Effect>,
    onNavigationEffect: (RequestItemReturnContract.Effect.Navigation) -> Unit,
    orderId: String,
    orderItemId: String,
    orderItemTitle: String,
    orderItemCoverImageUrl: String,
    orderItemPriceLabel: String,
) {
    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is RequestItemReturnContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(
            RequestItemReturnContract.Event.Init(
                orderId = orderId,
                orderItemId = orderItemId,
                orderItemTitle = orderItemTitle,
                orderItemCoverImageUrl = orderItemCoverImageUrl,
                orderItemPriceLabel = orderItemPriceLabel
            )
        )
    }

    ContentScreen(
        isLoading = state.isSubmitting,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = { onEvent(RequestItemReturnContract.Event.OnBack) }
        ),
        onBack = { onEvent(RequestItemReturnContract.Event.OnBack) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_request_return_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            OrderItemPreviewCard(
                title = orderItemTitle,
                coverImageUrl = orderItemCoverImageUrl,
                priceLabel = orderItemPriceLabel
            )

            ReturnReasonSection(
                selectedReason = state.selectedReason,
                onReasonSelected = {
                    onEvent(
                        RequestItemReturnContract.Event.OnReturnReasonSelected(
                            it
                        )
                    )
                }
            )

            DescriptionSection(
                description = state.description,
                onDescriptionChanged = {
                    onEvent(
                        RequestItemReturnContract.Event.OnDescriptionChanged(
                            it
                        )
                    )
                }
            )

            WrapButton(
                onClick = { onEvent(RequestItemReturnContract.Event.OnSubmitClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selectedReason != null && !state.isSubmitting
            ) {
                Text(text = stringResource(R.string.screen_request_return_submit_button))
            }
        }
    }
}

@Composable
private fun OrderItemPreviewCard(
    title: String,
    coverImageUrl: String,
    priceLabel: String,
) {
    ListingCard(
        title = title,
        coverImageUrl = coverImageUrl,
        additionalTitleContent = {
            Text(
                text = priceLabel,
                style = MaterialTheme.typography.subtitle1
            )
        },
        trailingIcon = null,
        additionalContent = null,
        enabled = false,
        onClicked = {}
    )
}

@Composable
private fun ReturnReasonSection(
    selectedReason: OrderItemReturnReasonUi?,
    onReasonSelected: (OrderItemReturnReasonUi) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_return_reason_title))

        OrderItemReturnReasonUi.entries.forEach { reason ->
            ReturnReasonItem(
                reason = reason,
                isSelected = reason == selectedReason,
                onSelected = { onReasonSelected(reason) }
            )
        }
    }
}

@Composable
private fun ReturnReasonItem(
    reason: OrderItemReturnReasonUi,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val accentColor =
        if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.4f)

    SectionHeader(
        title = stringResource(reason.labelResId),
        trailingIcon = {
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
            )
        },
        onClick = onSelected
    )
}

@Composable
private fun DescriptionSection(
    description: String,
    onDescriptionChanged: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_return_description_title))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(R.string.screen_request_return_description_placeholder))
            },
            minLines = 4,
            maxLines = 8,
            shape = MaterialTheme.shapes.medium
        )
    }
}
