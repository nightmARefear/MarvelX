package com.alexgreece45.marvelx.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.receiveAsSingleObserverFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : BaseEvent, State : BaseState, Effect : BaseEffect> :
    ViewModel() {

    private val initialState: State by lazy { setInitialState() }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val _effect: Channel<Effect> = Channel()

    /**
     * Used by the views to acquire the current state as read-only.
     */
    val uiState: StateFlow<State> =
        _state.asStateFlow()

    /**
     * Used by the ViewModels to acquire the current state.
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Used by the views to acquire the current effect.
     *
     * ### Note: Only one consumer is allowed.
     */
    val uiEffect: SingleObserverFlow<Effect> = _effect.receiveAsSingleObserverFlow()

    init {
        subscribeToEvents()
    }

    /**
     * Sets an effect.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    /**
     * Sets the initial state value.
     */
    protected abstract fun setInitialState(): State

    protected fun setState(reducer: State.() -> State) {
        _state.update { oldState -> oldState.reducer() }
    }

    fun setEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvents(it)
            }
        }
    }

    protected abstract fun handleEvents(event: Event)
}