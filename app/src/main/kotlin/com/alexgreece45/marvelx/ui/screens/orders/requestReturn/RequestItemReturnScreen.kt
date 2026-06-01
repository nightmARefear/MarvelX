package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ListingCard
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

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
        toolbarTitle = stringResource(R.string.screen_request_return_toolbar_title),
        stickyBottom = {
            WrapButton(
                onClick = { onEvent(RequestItemReturnContract.Event.OnSubmitButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSubmitButtonEnabled
            ) {
                Text(text = stringResource(R.string.screen_request_return_submit_button))
            }
        }
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

//            DescriptionSection(
//                description = state.description,
//                maxDescriptionLength = ,
//                onDescriptionChanged = {
//                    onEvent(
//                        RequestItemReturnContract.Event.OnDescriptionChanged(
//                            it
//                        )
//                    )
//                }
//            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                MandatoryCharacter(fontStyle = FontStyle.Italic)

                Text(
                    text = stringResource(R.string.screen_request_return_mandatory_field_label),
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.subtitle2
                )
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeaderTitle(title = stringResource(R.string.screen_request_return_reason_title))

            MandatoryCharacter()
        }

        OrderItemReturnReasonUi.entries.forEach { reason ->
            ReturnReasonItem(
                reason = reason,
                isSelected = reason == selectedReason,
                onSelected = { onReasonSelected(reason) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReturnReasonItem(
    reason: OrderItemReturnReasonUi,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelected)

            SectionHeaderTitle(title = stringResource(reason.labelResId))
        }
    }
}

@Composable
private fun MandatoryCharacter(
    fontStyle: FontStyle? = null,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = "*",
        color = MaterialTheme.customColors.genericRed,
        fontStyle = fontStyle,
        style = style
    )
}

@Composable
private fun DescriptionSection(
    description: String,
    maxDescriptionLength: Int,
    onDescriptionChanged: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_return_description_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            OutlinedTextFieldValidation(
                value = description,
                onValueChange = onDescriptionChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.screen_request_return_description_placeholder),
                minLines = 4,
                maxLines = 8
            )
            
            Text(
                text = stringResource(
                    R.string.screen_request_return_description_remaining_characters_label,
                    description.length,
                    maxDescriptionLength
                ),
                modifier = Modifier.padding(start = SPACING_SMALL.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ScreenPreview() {
    MarvelXTheme {
        RequestItemReturnScreen(
            onEvent = {},
            state = RequestItemReturnContract.State(),
            effect = emptyFlow(),
            onNavigationEffect = {},
            orderId = "1",
            orderItemId = "100",
            orderItemTitle = "Wow mug!!!!",
            orderItemCoverImageUrl = "....",
            orderItemPriceLabel = "150.20€"
        )
    }
}
