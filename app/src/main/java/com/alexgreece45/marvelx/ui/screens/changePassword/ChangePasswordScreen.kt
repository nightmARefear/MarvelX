package com.alexgreece45.marvelx.ui.screens.changePassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.viewModels.ChangePasswordContract
import com.alexgreece45.marvelx.viewModels.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    popBackStack: () -> Unit,
    otpFlowType: String?,
    requestIdEmail: String,
    requestIdPhoneNumber: String?,
    viewModel: ChangePasswordViewModel = hiltViewModel()
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
}