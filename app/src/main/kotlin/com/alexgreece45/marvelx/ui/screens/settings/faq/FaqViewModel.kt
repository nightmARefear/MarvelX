package com.alexgreece45.marvelx.ui.screens.settings.faq

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.repository.FaqsRepository
import com.alexgreece45.marvelx.data.repository.helper.onFailed
import com.alexgreece45.marvelx.data.repository.helper.onNetworkError
import com.alexgreece45.marvelx.data.repository.helper.onSuccess
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.settings.faq.model.FaqUi
import com.alexgreece45.marvelx.ui.screens.settings.faq.model.toFaqUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface FaqContract {
    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data class OnSectionClicked(val id: String) : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val sections: List<FaqUi> = emptyList(),
        val error: FaqScreenErrorView? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
        }
    }
}

@KoinViewModel
class FaqViewModel(
    private val resourceProvider: ResourceProvider,
    private val faqsRepository: FaqsRepository
) : BaseViewModel<FaqContract.Event, FaqContract.State, FaqContract.Effect>() {
    override fun setInitialState(): FaqContract.State =
        FaqContract.State()

    override fun handleEvents(event: FaqContract.Event) {
        when (event) {
            FaqContract.Event.Init -> {
                init(event)
            }

            FaqContract.Event.OnBackClicked -> {
                setEffect {
                    FaqContract.Effect.Navigation.GoBack
                }
            }

            is FaqContract.Event.OnSectionClicked -> {
                setState {
                    copy(
                        sections = sections.map { section ->
                            if (event.id == section.id) {
                                section.copy(
                                    isExpanded = !section.isExpanded
                                )
                            } else {
                                section
                            }
                        }
                    )
                }
            }
        }
    }

    private fun init(event: FaqContract.Event) {
        viewModelScope.launch {
            faqsRepository.getFaqs()
                .onSuccess { data ->
                    setState {
                        copy(
                            isLoading = false,
                            sections = data.map { it.toFaqUi() }
                        )
                    }
                }
                .onNetworkError {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    showError(
                        subTitle = resourceProvider.getNoInternetConnectionMessage(),
                        event = event
                    )
                }
                .onFailed {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    showError(
                        subTitle = resourceProvider.getString(R.string.generic_unknown_error),
                        event = event
                    )
                }
        }
    }

    /**
     * Shows the error screen with the given [subTitle] and the [event] which represents the
     * [FaqContract.Event] that will be invoked when the primary button of the error screen is
     * clicked.
     */
    private fun showError(
        subTitle: String,
        event: FaqContract.Event
    ) {
        setState {
            copy(
                error = FaqScreenErrorView(
                    errorSubtitle = subTitle,
                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                    event = event
                )
            )
        }
    }
}
