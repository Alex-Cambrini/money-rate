package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CurrencyRepository) : ViewModel() {
    private val _rate = MutableStateFlow<Double?>(null)
    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val rate: StateFlow<Double?> = _rate
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    fun loadRate(base: String = "EUR", to: String) {
        viewModelScope.launch {
            val r = repository.getRate(base, to)
            _rate.value = r
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = repository.getAvailableCurrencies()
            _currencies.value = map.toList()
        }
    }
    fun loadAllRatesAgainstEuro(targets: List<String>) {
        viewModelScope.launch {
            val result = mutableMapOf<String, Double>()
            for (target in targets) {
                val rate = repository.getRate("EUR", target)
                if (rate != null) result[target] = rate
            }
            _latestRates.value = result
        }
    }

}
