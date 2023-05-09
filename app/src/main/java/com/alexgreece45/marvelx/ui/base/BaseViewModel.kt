package com.alexgreece45.marvelx.ui.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : BaseEvent, State : BaseState, Effect : BaseEffect> :
    ViewModel() {
    private val initialState: State by lazy { setInitialState() }
    private val _effect: Channel<Effect> = Channel()
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    private var _state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val _viewState: MutableState<State> by lazy { mutableStateOf(initialState) }
    val viewState: androidx.compose.runtime.State<State> by lazy { _viewState }

    var state: StateFlow<State> = _state.asStateFlow()
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

    fun setState(state: State) {
        _state.value = state
        notifyViewStateHistoryChanged(state)
    }

    protected fun setStateCompose(reducer: State.() -> State) {
        val newState = viewState.value.reducer()
        _viewState.value = newState
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