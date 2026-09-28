package com.alexgreece45.marvelx.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapTextButton
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

private val HERO_HEIGHT = 320.dp
private val HERO_CORNER_RADIUS = 32.dp
private val BRAND_MARK_SIZE = 112.dp

@Composable
internal fun HomeScreen(
    onEventSent: (event: HomeEvent) -> Unit,
    state: HomeState,
    effect: SingleObserverFlow<HomeEffect>,
    onNavigationEffect: (HomeEffect.Navigation) -> Unit,
    logoutReason: String?,
) {
    OneTimeLaunchedEffect {
        onEventSent(HomeEvent.Init(logoutReason))
    }

    ObserveUiEffect(flow = effect, tag = "HomeScreen") { uiEffect ->
        when (uiEffect) {
            is HomeEffect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    state.alertDialogError?.let { dialog ->
        AlertDialogError(
            title = dialog.title,
            errorMessage = { Text(text = dialog.description) },
            confirmButton = AlertDialogButton(
                label = dialog.buttonLabel,
                onClick = dialog.alertDialogOnClick
            )
        )
    }

    ContentScreen(
        isLoading = state.isLoading,
        onBack = { onEventSent(HomeEvent.OnBack) },
        paddingValues = PaddingValues(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Hero()

            // The buttons sit centred in the space below the hero.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DEFAULT_SCREEN_PADDING),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    WrapButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(HomeTestTags.SIGNUP_BUTTON),
                        onClick = { onEventSent(HomeEvent.OnSignupButtonClicked) }
                    ) {
                        Text(text = stringResource(R.string.screen_home_signup_button))
                    }

                    WrapTextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(HomeTestTags.LOGIN_BUTTON),
                        onClick = { onEventSent(HomeEvent.OnLoginButtonClicked) }
                    ) {
                        Text(text = stringResource(R.string.screen_home_login_button))
                    }
                }
            }
        }
    }
}

/**
 * Brand mark on a primary-to-accent gradient with a rounded bottom edge, so the landing page reads
 * as a product rather than a form.
 */
@Composable
private fun Hero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HERO_HEIGHT)
            .clip(RoundedCornerShape(bottomStart = HERO_CORNER_RADIUS, bottomEnd = HERO_CORNER_RADIUS))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary,
                        MaterialTheme.customColors.genericRed
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = SPACING_MEDIUM.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(BRAND_MARK_SIZE)
                    .background(
                        color = MaterialTheme.customColors.white.copy(alpha = 0.16f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.screen_home_hero_title),
                    modifier = Modifier.size(BRAND_MARK_SIZE)
                )
            }

            Text(
                text = stringResource(R.string.screen_home_hero_title),
                color = MaterialTheme.customColors.white,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h3
            )

            Text(
                text = stringResource(R.string.screen_home_hero_tagline),
                color = MaterialTheme.customColors.white.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
