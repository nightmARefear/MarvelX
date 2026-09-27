package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LottieComposable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model.CheckoutSuccessContract
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun CheckoutSuccessScreen(
    onEvent: (CheckoutSuccessContract.Event) -> Unit,
    state: CheckoutSuccessContract.State,
    effect: SingleObserverFlow<CheckoutSuccessContract.Effect>,
    onNavigationEffect: (CheckoutSuccessContract.Effect.Navigation) -> Unit
) {

    ObserveUiEffect(flow = effect, tag = "CheckoutSuccessScreen") { uiEffect ->
        when (uiEffect) {
            is CheckoutSuccessContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Closeable(
            testTag = CheckoutSuccessTestTags.CLOSE_BUTTON,
            onClose = {
                onEvent(
                    CheckoutSuccessContract.Event.OnBack
                )
            }
        ),
        onBack = { onEvent(CheckoutSuccessContract.Event.OnBack) },
        showToolbar = true,
        stickyBottom = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                WrapOutlinedButton(
                    onClick = { onEvent(CheckoutSuccessContract.Event.OnViewOrderDetailsButtonClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CheckoutSuccessTestTags.VIEW_ORDER_DETAILS_BUTTON)
                ) {
                    Text(text = stringResource(R.string.screen_checkout_success_view_order_details_button))
                }

                WrapButton(
                    onClick = { onEvent(CheckoutSuccessContract.Event.OnContinueShoppingButtonClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CheckoutSuccessTestTags.CONTINUE_SHOPPING_BUTTON)
                ) {
                    Text(text = stringResource(R.string.screen_checkout_success_continue_shopping_button))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieComposable(
                resourceId = R.raw.lottie_success,
                size = ConstantsUi.QrCodeScreen.LOTTIE_ANIMATION_SIZE,
                iterations = 1
            ) {
                val subtitle = buildAnnotatedString {
                    append(stringResource(R.string.screen_checkout_success_subtitle_part_1))

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(
                            " " +
                                    stringResource(
                                        R.string.screen_checkout_success_subtitle_part_2,
                                        state.orderId
                                    )
                        )
                    }

                    append(stringResource(R.string.screen_checkout_success_subtitle_part_3))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    Text(
                        text = stringResource(R.string.screen_checkout_success_title),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h5
                    )

                    Text(
                        text = subtitle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true, showBackground = true
)
@Composable
private fun CheckoutSuccessScreenPreview() {
    val orderId = "10"
    MarvelXTheme {
        CheckoutSuccessScreen(
            onEvent = {},
            state = CheckoutSuccessContract.State(
                isLoading = false,
                orderId = orderId
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {}
        )
    }
}