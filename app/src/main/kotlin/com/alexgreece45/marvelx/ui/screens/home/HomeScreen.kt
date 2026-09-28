package com.alexgreece45.marvelx.ui.screens.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL

private val BRAND_MARK_SIZE = 88.dp
private val TAGLINE_MAX_WIDTH = 320.dp
private val CTA_HEIGHT = 56.dp
private val CTA_CORNER_RADIUS = 16.dp
private val CTA_FONT_SIZE = 16.sp
private val CTA_OUTLINE_WIDTH = 1.5.dp
private const val CTA_OUTLINE_ALPHA = 0.4f
private const val CTA_GLOW_ALPHA = 0.22f
private const val TAGLINE_ALPHA = 0.72f

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
        // The brand stays quiet in the top half so the CTAs, centred in the bottom half (the thumb
        // zone), are what the eye lands on.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = DEFAULT_SCREEN_PADDING)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Brand()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .ctaGlow(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    WrapButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CTA_HEIGHT)
                            .testTag(HomeTestTags.SIGNUP_BUTTON),
                        shape = RoundedCornerShape(CTA_CORNER_RADIUS),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary,
                            contentColor = MaterialTheme.colors.onSecondary
                        ),
                        onClick = { onEventSent(HomeEvent.OnSignupButtonClicked) }
                    ) {
                        CtaLabel(text = stringResource(R.string.screen_home_signup_button))
                    }

                    WrapOutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CTA_HEIGHT)
                            .testTag(HomeTestTags.LOGIN_BUTTON),
                        shape = RoundedCornerShape(CTA_CORNER_RADIUS),
                        border = BorderStroke(
                            width = CTA_OUTLINE_WIDTH,
                            color = MaterialTheme.colors.onBackground.copy(alpha = CTA_OUTLINE_ALPHA)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.customColors.transparent,
                            contentColor = MaterialTheme.colors.onBackground
                        ),
                        onClick = { onEventSent(HomeEvent.OnLoginButtonClicked) }
                    ) {
                        CtaLabel(text = stringResource(R.string.screen_home_login_button))
                    }
                }
            }
        }
    }
}

/**
 * A soft red radial glow behind the CTAs, fading to transparent at the edges, so the buttons read as
 * lit while the rest of the screen stays flat.
 */
@Composable
private fun Modifier.ctaGlow(): Modifier {
    val glowColor = MaterialTheme.colors.secondary.copy(alpha = CTA_GLOW_ALPHA)
    val transparent = MaterialTheme.customColors.transparent
    return drawBehind {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(glowColor, transparent),
                center = center,
                radius = size.minDimension / 2
            )
        )
    }
}

@Composable
private fun CtaLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.button.copy(fontSize = CTA_FONT_SIZE),
        fontWeight = FontWeight.Bold
    )
}


@Composable
private fun Brand() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = SPACING_SMALL.dp)
                .size(BRAND_MARK_SIZE)
                .background(color = MaterialTheme.customColors.black, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Decorative: the visible title right below already names the brand.
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(BRAND_MARK_SIZE)
            )
        }

        Text(
            text = stringResource(R.string.screen_home_hero_title),
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h4
        )

        Text(
            modifier = Modifier
                .widthIn(max = TAGLINE_MAX_WIDTH)
                .padding(horizontal = SPACING_MEDIUM.dp),
            text = stringResource(R.string.screen_home_hero_tagline),
            color = MaterialTheme.colors.onBackground.copy(alpha = TAGLINE_ALPHA),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
}
