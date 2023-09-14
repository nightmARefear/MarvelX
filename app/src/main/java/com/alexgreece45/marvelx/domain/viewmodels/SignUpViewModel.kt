package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class SignUpEvent : BaseEvent {
    object InitView : SignUpEvent()
}

/*
first_name,varchar(100)
last_name,varchar(100)
gender,"enum('female','male','other')"
email,varchar(200)
password,varchar(100)

*/

data class SignUpState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val firstName: String = "",
    val lastName: String = "",
//    val gender: Gender? = null,

) : BaseState {
    companion object {
        fun getDefaultValues(): SignUpState = SignUpState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null
        )
    }
}

sealed class SignUpEffect : BaseEffect {
    object NavigateToAllCharactersScreen : SignUpEffect()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(

) : BaseViewModel<SignUpEvent, SignUpState, SignUpEffect>() {
    override fun setInitialState(): SignUpState = SignUpState.getDefaultValues()

    init {
        setEvent(SignUpEvent.InitView)
    }

    override suspend fun handleEvents(event: SignUpEvent) {
        when (event) {
            SignUpEvent.InitView -> {
                initView()
            }
        }
    }

    private fun initView() {

    }
}