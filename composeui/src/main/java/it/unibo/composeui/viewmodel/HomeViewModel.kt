package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModel() {
    private val _rate = MutableStateFlow<Double?>(null)
    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    private val _latestRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val latestRates: StateFlow<Map<String, Double>> = _latestRates
    val rate: StateFlow<Double?> = _rate
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    fun loadRate(base: String = "EUR", to: String) {
        viewModelScope.launch {
            val currencyRate = getRateUseCase.invoke(base, to)
            _rate.value = currencyRate.rate
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = getAvailableCurrenciesUseCase.invoke()
            _currencies.value = map.toList()
        }
    }

    fun loadAllRatesAgainstEuro(targets: List<String>) {
        viewModelScope.launch {
            val result = mutableMapOf<String, Double>()
            for (target in targets) {
                val rate = getRateUseCase.invoke("EUR", target).rate
                if (rate != 0.0) result[target] = rate
            }
            _latestRates.value = result
        }
    }
}
