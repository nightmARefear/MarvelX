package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LottieComposable
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.Flow

@Composable
fun CreateListingSuccessScreen(
    onEvent: (CreateListingSuccessContract.Event) -> Unit,
    state: CreateListingSuccessContract.State,
    effect: Flow<CreateListingSuccessContract.Effect>,
    onNavigation: (CreateListingSuccessContract.Effect.Navigation) -> Unit
) {

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CreateListingSuccessContract.Effect.Navigation -> onNavigation(uiEffect)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CreateListingSuccessContract.Event.Init)
    }

    ContentScreen(
        isLoading = state.isLoading
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(
                SPACING_SMALL.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieComposable(
                resourceId = R.raw.lottie_success,
                size = ConstantsUi.QrCodeScreen.LOTTIE_ANIMATION_SIZE,
                iterations = 1
            ) {
                Text(
                    text = state.successMessage,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                )
            }

            WrapButton(onClick = { onEvent(CreateListingSuccessContract.Event.OnViewListingClicked) }) {
                Text(text = stringResource(R.string.screen_create_listing_button_view_listing))
            }
        }
    }
}
