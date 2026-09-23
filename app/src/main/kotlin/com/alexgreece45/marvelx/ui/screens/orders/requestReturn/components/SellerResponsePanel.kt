package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.inputFields.TextInputField
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderTitle
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.RequestItemReturnTestTags
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnSellerActionUi
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi

/**
 * Every decision the seller can take on a pending return, each with the consequence spelled out.
 * Selecting one opens a confirmation dialog rather than applying immediately, because the
 * backend only allows a pending request to be answered once.
 */
@Composable
internal fun SellerResponsePanel(
    sellerResponseMessageField: InputFieldUi.Text,
    isSubmitting: Boolean,
    onMessageChange: (String) -> Unit,
    onActionClick: (OrderItemReturnSellerActionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        SectionHeaderTitle(title = stringResource(R.string.screen_request_item_return_seller_response_section_title))

        SellerDecisionWarning(modifier = Modifier.fillMaxWidth())

        TextInputField(
            inputField = sellerResponseMessageField,
            label = stringResource(R.string.screen_request_item_return_seller_response_message_label),
            placeholder = stringResource(R.string.screen_request_item_return_seller_response_message_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(RequestItemReturnTestTags.SELLER_RESPONSE_MESSAGE_TEXT_FIELD),
            enabled = !isSubmitting,
            onValueChanged = onMessageChange,
            minLines = 2,
            maxLines = 4
        )

        Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
            OrderItemReturnSellerActionUi.entries.forEach { action ->
                SellerActionCard(
                    action = action,
                    isEnabled = !isSubmitting,
                    onClick = { onActionClick(action) }
                )
            }
        }

        if (isSubmitting) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE))

                Text(
                    text = stringResource(R.string.screen_request_item_return_seller_response_submitting),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

@Composable
private fun SellerActionCard(
    action: OrderItemReturnSellerActionUi,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val accentColor = if (action.isDestructive) {
        MaterialTheme.customColors.genericRed
    } else {
        MaterialTheme.customColors.successStatusContainerColors.contentColor
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACING_MEDIUM.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(action.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                    tint = accentColor
                )

                Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
                    Text(
                        text = stringResource(action.labelResId),
                        color = accentColor,
                        style = MaterialTheme.typography.h6
                    )

                    Text(
                        text = stringResource(action.descriptionResId),
                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            WrapButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(RequestItemReturnTestTags.sellerResponseActionButton(action.name)),
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = accentColor,
                    contentColor = MaterialTheme.customColors.buttonContentWhite
                )
            ) {
                Text(text = stringResource(action.labelResId))
            }
        }
    }
}
