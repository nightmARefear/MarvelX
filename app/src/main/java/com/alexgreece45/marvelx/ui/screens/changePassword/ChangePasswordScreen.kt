package com.alexgreece45.marvelx.ui.screens.changePassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.viewModels.ChangePasswordContract
import com.alexgreece45.marvelx.viewModels.ChangePasswordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    popBackStack: () -> Unit,
    otpFlowType: String?,
    requestIdEmail: String,
    requestIdPhoneNumber: String?,
    viewModel: ChangePasswordViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ChangePasswordContract.Effect.Navigation.PopBackStack -> {
                    popBackStack()
                }
            }
        }
    }

    // TODO: To be implemented in the future.
}