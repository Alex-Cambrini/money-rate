package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModel() {

    private val _isDataReady = MutableStateFlow(false)
    val isDataReady: StateFlow<Boolean> = _isDataReady

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    private val _latestRates = MutableStateFlow<Map<String, Double>>(emptyMap())

    val currencies: StateFlow<List<Pair<String, String>>> = _currencies
    val latestRates: StateFlow<Map<String, Double>> = _latestRates

    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_currencies.value.isNotEmpty() && _latestRates.value.isNotEmpty()) {
                    _isDataReady.value = true
                    _isError.value = false
                    return@launch
                }

                val list = getAvailableCurrenciesUseCase.invoke()
                _currencies.value = list.map { it.code to it.name }

                val result = mutableMapOf<String, Double>()
                for ((code, _) in _currencies.value) {
                    if (code != "EUR") {
                        val rate = getRateUseCase.invoke("EUR", code).rate
                        if (rate != 0.0) result[code] = rate
                    }
                }
                _latestRates.value = result

                if (_latestRates.value.isEmpty()) {
                    _isError.value = true
                    _isDataReady.value = false
                    return@launch
                }

                _isDataReady.value = true
                _isError.value = false
            } catch (e: Exception) {
                _isError.value = true
                _isDataReady.value = false
            }
        }
    }
}
