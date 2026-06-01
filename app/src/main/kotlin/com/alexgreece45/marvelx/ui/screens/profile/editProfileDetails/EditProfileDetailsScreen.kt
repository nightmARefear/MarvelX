package com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.EditProfileDetailsItemUi
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.flow.Flow

@Composable
fun EditProfileDetailsScreen(
    onEventSent: (event: EditProfileDetailsContract.Event) -> Unit,
    state: EditProfileDetailsContract.State,
    effect: Flow<EditProfileDetailsContract.Effect>,
    navigateUp: () -> Unit
) {
    LaunchedEffect(Unit) {
        onEventSent(EditProfileDetailsContract.Event.Init)
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            EditProfileDetailsContract.Effect.Navigation.NavigateUp -> {
                navigateUp()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = {
                onEventSent(
                    EditProfileDetailsContract.Event.OnBackClicked
                )
            }
        ),
        onBack = {
            onEventSent(
                EditProfileDetailsContract.Event.OnBackClicked
            )
        },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.edit_profile_details_screen_header_title),
        snackbarStateData = state.snackbarStateData,
        stickyBottom = {
            WrapButton(
                onClick = { onEventSent(EditProfileDetailsContract.Event.OnSaveChangesButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveChangesButtonEnabled
            ) {
                Text(
                    text = stringResource(R.string.edit_profile_details_screen_button_label_save_changes)
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
                        errorMessage = { Text(text = alertDialogView.errorMessage) },
                        dismissButton = alertDialogView.dismissButton,
                        confirmButton = alertDialogView.confirmButton
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SPACING_SMALL.dp),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    state.profileDetailsUi?.let { profileDetailsUi ->
                        ProfileDetailsItem(
                            editProfileDetailsItemUi = profileDetailsUi.firstName,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )

                        ProfileDetailsItem(
                            editProfileDetailsItemUi = profileDetailsUi.lastName,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )

                        ExposedDropdownMenu(
                            label = stringResource(R.string.generic_label_gender),
                            expanded = state.isGenderDropDownExpanded,
                            modifier = Modifier.fillMaxWidth(),
                            value = stringResource(profileDetailsUi.gender.currentValue.label),
                            onDismissRequest = { onEventSent(EditProfileDetailsContract.Event.GenderField.OnDismiss) },
                            onExpandedChange = { onEventSent(EditProfileDetailsContract.Event.GenderField.OnClick) },
                        ) {
                            Gender.asList().forEach { gender ->
                                DropdownMenuItem(
                                    onClick = {
                                        onEventSent(
                                            EditProfileDetailsContract.Event.GenderField.OnGenderSelected(
                                                gender
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(gender.label)
                                    )
                                }
                            }
                        }
                        VSpacer.Small()

                        ProfileDetailsItem(
                            editProfileDetailsItemUi = profileDetailsUi.email,
                            onEventSent = onEventSent,
                            shimmer = state.isLoading
                        )
                    }
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
    }
}

/**
 * Draws the given [editProfileDetailsItemUi] to the screen.
 *
 * @param editProfileDetailsItemUi  The item to draw.
 * @param onEventSent           Executes the given event.
 * @param shimmer               Whether the shimmering effect is enabled.
 */
@Suppress("UNCHECKED_CAST")
@Composable
private fun <T : Any> ProfileDetailsItem(
    editProfileDetailsItemUi: EditProfileDetailsItemUi<T>,
    onEventSent: (event: EditProfileDetailsContract.Event) -> Unit,
    shimmer: Boolean
) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    OutlinedTextFieldValidation(
        value = editProfileDetailsItemUi.currentValue.toString(),
        onValueChange = {
            onEventSent(
                editProfileDetailsItemUi.onValueChanged(it as T)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
//            .shimmer(customShimmer = shimmerInstance)
        ,
        label = stringResource(editProfileDetailsItemUi.label),
        trailingIcon = {
            ThrottledIconButton(onClick = { onEventSent(editProfileDetailsItemUi.onResetIconClicked) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_reset),
                    contentDescription = stringResource(R.string.content_description_ic_reset)
                )
            }
        },
        errorMessage = editProfileDetailsItemUi.validationResult.message,
        isError = !editProfileDetailsItemUi.validationResult.isValid && editProfileDetailsItemUi.validationResult.message.isNotBlank(),
        keyboardOptions = KeyboardOptions(keyboardType = editProfileDetailsItemUi.keyboardType)
    )
}
