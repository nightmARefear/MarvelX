package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsItemUi
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.flow.Flow

@Composable
fun ProfileDetailsScreen(
    state: ProfileDetailsContract.State,
    onEventSent: (ProfileDetailsContract.Event) -> Unit,
    effect: Flow<ProfileDetailsContract.Effect>,
    popBackStack: () -> Unit
) {
    LaunchedEffect(Unit) {
        onEventSent(ProfileDetailsContract.Event.Init)

        effect.collect {
            when (it) {
                ProfileDetailsContract.Effect.Navigation.PopBackStack -> {
                    popBackStack()
                }
            }
        }
    }

    state.errorView?.let { errorView ->
        ContentErrorScreen(
            errorTitle = errorView.errorTitle,
            errorSubtitle = errorView.errorSubtitle,
            onClose = { onEventSent(errorView.onCloseClicked) },
            errorButtonLayout = errorView.errorButtonLayout
        )
        return
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(onBack = {
            onEventSent(
                ProfileDetailsContract.Event.OnBackClicked
            )
        }),
        onBack = {
            onEventSent(
                ProfileDetailsContract.Event.OnBackClicked
            )
        },
        showToolbar = true,
        snackbarState = state.snackbarState,
        stickyBottom = {
            WrapButton(
                onClick = { onEventSent(ProfileDetailsContract.Event.OnSaveChangesButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveChangesButtonEnabled
            ) {
                Text(
                    text = stringResource(R.string.profile_details_screen_button_label_save_changes)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            item {
                state.alertDialogView?.let { alertDialogView ->
                    AlertDialogError(
                        isVisible = true,
                        title = alertDialogView.title,
                        errorMessage = alertDialogView.errorMessage,
                        dismissButton = alertDialogView.dismissButton,
                        confirmButton = alertDialogView.confirmButton
                    )
                }
            }

            item { ContentScreenHeader(title = stringResource(R.string.profile_details_screen_header_title)) }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SPACING_SMALL.dp),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    state.profileDetailsUi?.let { profileDetailsUi ->
                        ProfileDetailsItem(
                            profileDetailsItemUi = profileDetailsUi.firstName,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )

                        ProfileDetailsItem(
                            profileDetailsItemUi = profileDetailsUi.lastName,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )

                        ExposedDropdownMenu(
                            label = stringResource(R.string.generic_label_gender),
                            expanded = state.isGenderDropDownExpanded,
//                                modifier = Modifier.fillMaxWidth(),
                            value = stringResource(profileDetailsUi.gender.currentValue.label),
                            onDismissRequest = { onEventSent(ProfileDetailsContract.Event.GenderField.OnDismiss) },
                            onExpandedChange = { onEventSent(ProfileDetailsContract.Event.GenderField.OnClick) },
                        ) {
                            Gender.asList().forEach { gender ->
                                DropdownMenuItem(
                                    onClick = {
                                        onEventSent(
                                            ProfileDetailsContract.Event.GenderField.OnValueChanged(
                                                gender
                                            )
                                        )
                                    }
                                ) {
                                    Text(
                                        text = stringResource(gender.label)
                                    )
                                }
                            }
                        }
                        VSpacer.Small()

                        ProfileDetailsItem(
                            profileDetailsItemUi = profileDetailsUi.email,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws the given [profileDetailsItemUi] to the screen.
 *
 * @param profileDetailsItemUi  The item to draw.
 * @param onEventSent           Executes the given event.
 * @param shimmer               Whether the shimmering effect is enabled.
 */
@Suppress("UNCHECKED_CAST")
@Composable
private fun <T : Any> ProfileDetailsItem(
    profileDetailsItemUi: ProfileDetailsItemUi<T>,
    onEventSent: (event: ProfileDetailsContract.Event) -> Unit,
    shimmer: Boolean
) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    OutlinedTextFieldValidation(
        value = profileDetailsItemUi.currentValue.toString(),
        onValueChange = {
            onEventSent(
                profileDetailsItemUi.onValueChanged(it as T)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
//            .shimmer(customShimmer = shimmerInstance)
        ,
        label = stringResource(profileDetailsItemUi.label),
        trailingIcon = {
            ThrottledIconButton(onClick = { onEventSent(profileDetailsItemUi.onResetIconClicked) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_reset),
                    contentDescription = stringResource(R.string.content_description_ic_reset)
                )
            }
        },
        errorMessage = profileDetailsItemUi.validationResult.message,
        isError = !profileDetailsItemUi.validationResult.isValid && profileDetailsItemUi.validationResult.message.isNotBlank()
    )
}