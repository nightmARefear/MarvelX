package com.alexgreece45.marvelx.ui.screens.profile.image

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.screens.profile.image.model.ProfileImageContract
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileImageScreen(
    onEventSent: (ProfileImageContract.Event) -> Unit,
    state: ProfileImageContract.State,
    effect: Flow<ProfileImageContract.Effect>,
    navigateUp: () -> Unit,
    profileImageUrl: String,
) {
    LaunchedEffect(Unit) {
        onEventSent(ProfileImageContract.Event.Init(profileImageUrl))
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            ProfileImageContract.Effect.Navigation.NavigateUp -> {
                navigateUp()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = true,
        navigableAction = ScreenNavigableAction.Closeable(
            onClose = {
                onEventSent(
                    ProfileImageContract.Event.OnBack
                )
            }
        ),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.profileImageUrl?.let { imageUri ->
                Spacer(modifier = Modifier.weight(0.15f))
                Row(modifier = Modifier.weight(0.85f)) {
                    WrapAsyncImage(
                        imageUrl = imageUri,
                        contentDescription = stringResource(R.string.profile_image_screen_content_description_profile_image),
                        modifier = Modifier
                            .size(size = ConstantsUi.PROFILE_IMAGE_FULL_SIZE)
                            .circularImageWithBorder(
                                borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                                borderColor = MaterialTheme.customColors.genericRed
                            ),
                        placeholder = painterResource(R.drawable.ic_person),
                        error = painterResource(R.drawable.ic_person),
                        onSuccess = { onEventSent(ProfileImageContract.Event.ProfileImage.OnLoadedSuccess) },
                        onError = { onEventSent(ProfileImageContract.Event.ProfileImage.OnLoadedError) },
                        contentScale = ContentScale.FillBounds,
                        isCacheDisabled = true
                    )
                }
            }
        }
    }

    state.errorView?.let { errorView ->
        ContentErrorScreen(
            errorTitle = errorView.title,
            errorSubtitle = errorView.subtitle,
            onClose = { onEventSent(ProfileImageContract.Event.OnBack) },
            errorButtonLayout = errorView.errorButtonLayout
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileImageScreenPreview() {
    MarvelXTheme {
        ProfileImageScreen(
            onEventSent = {},
            state = ProfileImageContract.State(
                isLoading = false,
                profileImageUrl = ""
            ),
            effect = emptyFlow(),
            navigateUp = {},
            profileImageUrl = "1"
        )
    }
}
