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
    val uiState =
        _uiState.asStateFlow() // Used by the views to acquire the current state in a read-only mode.
    val currentState: State
        get() = uiState.value // Used by the viewModel to acquire the current state.

    private val _effect: Channel<Effect> = Channel()
    var effect: Flow<Effect> = _effect.receiveAsFlow()

    private val _viewStateHistory: Channel<State> = Channel()
    val viewStateHistory get() = _viewStateHistory.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

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

    abstract suspend fun handleEvents(event: Event)
}