package com.alexgreece45.marvelx.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : BaseEvent, State : BaseState, Effect : BaseEffect> :
    ViewModel() {

    private val initialState: State by lazy { setInitialState() }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)

    /**
     * Used by the views to acquire the current state (read-only mode).
     */
    val uiState =
        _uiState.asStateFlow()

    /**
     * Used by the viewModel to acquire the current state.
     */
    protected val currentState: State
        get() = uiState.value

    private val _effect: Channel<Effect> = Channel()

    /**
     * Used by the views to acquire the current effect.
     */
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    private val _viewStateHistory: Channel<State> = Channel()
    val viewStateHistory: Flow<State>
        get() = _viewStateHistory.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /**
     * Sets an effect.
     */
    fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }


    /**
     * Sets the initial state's value.
     */
    abstract fun setInitialState(): State

    protected fun setState(reducer: State.() -> State) {
        val newState = uiState.value.reducer()
        _uiState.value = newState
        notifyViewStateHistoryChanged(newState)
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

    private fun notifyViewStateHistoryChanged(state: State) {
        viewModelScope.launch { _viewStateHistory.send(state) }
    }

    abstract fun handleEvents(event: Event)
}