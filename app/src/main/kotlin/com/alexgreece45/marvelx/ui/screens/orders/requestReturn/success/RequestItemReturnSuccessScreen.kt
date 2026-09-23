package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.components.statusContainer.StatusContainer
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnMethod
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapImage
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.components.sections.SectionHeaderRow
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.model.RequestItemReturnSuccessContract
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer

@Composable
internal fun RequestItemReturnSuccessScreen(
    onEvent: (RequestItemReturnSuccessContract.Event) -> Unit,
    state: RequestItemReturnSuccessContract.State,
    effect: SingleObserverFlow<RequestItemReturnSuccessContract.Effect>,
    onNavigationEffect: (RequestItemReturnSuccessContract.Effect.Navigation) -> Unit
) {
    ObserveUiEffect(flow = effect, tag = "RequestItemReturnSuccessScreen") { uiEffect ->
        when (uiEffect) {
            is RequestItemReturnSuccessContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.uiState.isLoading,
        navigableAction = ScreenNavigableAction.Closeable(
            testTag = RequestItemReturnSuccessTestTags.CLOSE_BUTTON,
            onClose = {
                onEvent(
                    RequestItemReturnSuccessContract.Event.OnBack
                )
            }
        ),
        onBack = {
            onEvent(
                RequestItemReturnSuccessContract.Event.OnBack
            )
        },
        showToolbar = true,
        stickyBottom = {
            if (state.uiState is UiState.Success) {
                WrapButton(
                    onClick = { onEvent(RequestItemReturnSuccessContract.Event.OnStickyBottomButtonClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(RequestItemReturnSuccessTestTags.VIEW_STATUS_BUTTON)
                ) {
                    Text(text = stringResource(if (state.uiState.data.isRefundRequested) R.string.screen_request_item_return_success_sticky_bottom_button_view_refund_status else R.string.screen_request_item_return_success_sticky_bottom_button_view_return_status))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                UiState.Loading -> {}
                is UiState.Error -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        GenericErrorScreen(
                            errorMessage = stringResource(R.string.screen_request_item_return_success_generic_error_message),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(maxHeight),
                            buttonTestTag = RequestItemReturnSuccessTestTags.ERROR_RETRY_BUTTON,
                            onErrorButtonClick = {
                                onEvent(RequestItemReturnSuccessContract.Event.OnErrorButtonClicked)
                            }
                        )
                    }
                }

                is UiState.Success -> {
                    SuccessContent(orderItemReturn = uiState.data)
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(orderItemReturn: OrderItemReturnUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        SuccessMessage(orderItemReturn.isRefundRequested)

        // Return request details.
        SectionHeader(
            title = stringResource(if (orderItemReturn.isRefundRequested) R.string.screen_request_item_return_success_details_refund_title else R.string.screen_request_item_return_success_details_return_title),
            additionalTitleContent = {
                VSpacer.Medium()

                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeaderRow(
                        startLabel = stringResource(R.string.screen_request_item_return_success_details_request_id),
                        endLabel = orderItemReturn.orderItemReturnId.formatted
                    )

                    SectionHeaderRow(
                        startLabel = stringResource(R.string.screen_request_item_return_success_details_status),
                        endLabel = stringResource(orderItemReturn.returnStatus.labelResId)
                    )

                    if (orderItemReturn.isRefundRequested && orderItemReturn.refundAmount != null) {
                        SectionHeaderRow(
                            startLabel = stringResource(R.string.screen_request_item_return_success_details_expected_refund),
                            endLabel = orderItemReturn.refundAmount.label
                        )
                    }

                    SectionHeaderRow(
                        startLabel = stringResource(R.string.screen_request_item_return_success_details_submitted),
                        endLabel = orderItemReturn.submittedAt.label
                    )
                }
            },
            enabled = false,
            onClick = {}
        )

        NextSteps(orderItemReturn.returnMethod)
    }
}

@Composable
private fun SuccessMessage(isRefundRequested: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.customColors.successStatusContainerColors.outlineColor
                    ),
                    shape = CircleShape
                )
                .background(
                    color = MaterialTheme.customColors.successStatusContainerColors.backgroundColor,
                    shape = CircleShape
                )
                .padding(SPACING_MEDIUM.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.customColors.white
            )
        }

        Text(
            text = stringResource(if (isRefundRequested) R.string.screen_request_item_return_success_main_content_refund_title else R.string.screen_request_item_return_success_main_content_return_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Text(
            text = stringResource(if (isRefundRequested) R.string.screen_request_item_return_success_main_content_refund_description else R.string.screen_request_item_return_success_main_content_return_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NextSteps(returnMethod: OrderItemReturnMethod) {
    StatusContainer(
        title = stringResource(R.string.screen_request_item_return_success_next_steps_title),
        titleColor = MaterialTheme.customColors.infoStatusContainerColors.contentColor,
        backgroundColor = MaterialTheme.customColors.infoStatusContainerColors.backgroundColor,
        modifier = Modifier.fillMaxWidth(),
        outlineColor = MaterialTheme.customColors.infoStatusContainerColors.outlineColor,
        icon = {
            WrapImage(
                painter = painterResource(UiThemeDrawable.ic_clipboard),
                contentDescription = null
            )
        },
        extraContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
            ) {
                when (returnMethod) {
                    OrderItemReturnMethod.PREPAID_RETURN_LABEL -> {
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_prepaid_shipping_label_step_1))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_prepaid_shipping_label_step_2))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_prepaid_shipping_label_step_3))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_prepaid_shipping_label_step_4))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_prepaid_shipping_label_step_5))
                    }

                    OrderItemReturnMethod.REQUEST_COURIER_PICKUP -> {
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_courier_pickup_step_1))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_courier_pickup_step_2))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_courier_pickup_step_3))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_courier_pickup_step_4))
                        Text(text = stringResource(R.string.screen_request_item_return_success_next_steps_courier_pickup_step_5))
                    }
                }
            }
        }
    )
}