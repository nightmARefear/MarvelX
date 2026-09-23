package com.alexgreece45.marvelx.ui.screens.changePassword.passwordChanged

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL

private val SUCCESS_BADGE_SIZE = 96.dp

/**
 * Terminal screen of the forgot-password flow. Back is redirected to login so the user cannot
 * return to the now-consumed reset form.
 */
@Composable
fun PasswordChangedScreen(onGoToLoginClick: () -> Unit) {
    BackHandler(onBack = onGoToLoginClick)

    ContentScreen(
        isLoading = false,
        stickyBottom = {
            WrapButton(
                onClick = onGoToLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(PasswordChangedTestTags.GO_TO_LOGIN_BUTTON)
            ) {
                Text(text = stringResource(R.string.screen_password_changed_button_login))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SPACING_MEDIUM.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(SUCCESS_BADGE_SIZE)
                    .background(
                        color = MaterialTheme.customColors.greenCheckmark.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(SUCCESS_BADGE_SIZE / 2),
                    tint = MaterialTheme.customColors.greenCheckmark
                )
            }

            Text(
                text = stringResource(R.string.screen_password_changed_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5
            )

            Text(
                text = stringResource(R.string.screen_password_changed_subtitle),
                modifier = Modifier.padding(horizontal = SPACING_SMALL.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
