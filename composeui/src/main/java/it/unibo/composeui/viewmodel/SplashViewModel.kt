package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.usecase.InitializeAppDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val initializeAppDataUseCase: InitializeAppDataUseCase
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data class Success(
            val currencies: List<Pair<String, String>>,
            val rates: Map<String, Double>
        ) : State()

        data object Error : State()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    fun initialize() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val result = initializeAppDataUseCase()) {
                is InitializeAppDataUseCase.Result.Success -> {
                    _state.value = State.Success(
                        result.data.currencies,
                        result.data.rates
                    )
                }

                is InitializeAppDataUseCase.Result.Failure -> {
                    _state.value = State.Error
                }
            }
        }
    }
}
