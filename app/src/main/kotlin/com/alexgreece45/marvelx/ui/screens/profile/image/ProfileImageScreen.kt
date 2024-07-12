package com.alexgreece45.marvelx.ui.screens.profile.image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.Flow

@Composable
fun ProfileImageScreen(
    onEventSent: (ProfileImageContract.Event) -> Unit,
    state: ProfileImageContract.State,
    effect: Flow<ProfileImageContract.Effect>,
    fullName: String,
    navigateUp: () -> Unit,
) {
    OneTimeLaunchedEffect {
        onEventSent(ProfileImageContract.Event.Init(fullName = fullName))
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
        ) {
            ContentScreenHeader(
                title = stringResource(
                    R.string.profile_image_screen_toolbar_title,
                    state.fullName
                )
            )

            VSpacer.Medium()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WrapAsyncImage(
                    imageUrl = state.imageUri,
                    contentDescription = stringResource(R.string.profile_image_screen_content_description_profile_image),
                    modifier = Modifier
                        .size(ConstantsUi.PROFILE_IMAGE_FULL_SIZE)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.ic_person),
                    error = painterResource(R.drawable.ic_person),
                    onSuccess = { onEventSent(ProfileImageContract.Event.ProfileImage.OnLoadedSuccess) },
                    onError = { onEventSent(ProfileImageContract.Event.ProfileImage.OnLoadedError) },
                    contentScale = ContentScale.Fit,
                )
            }
        }

    }

    state.errorView?.let { errorView ->

    }
}
