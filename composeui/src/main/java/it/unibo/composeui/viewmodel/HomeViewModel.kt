package it.unibo.composeui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CurrencyRepository) : ViewModel() {
    private val _rates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val rates: StateFlow<List<CurrencyRate>> = _rates

    fun loadRates(base: String = "EUR") {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching rates for base: $base")
            val ratesMap = repository.getAllRates(base)
            Log.d("HomeViewModel", "RatesMap received: $ratesMap")
            val list = ratesMap.map { (to, rate) ->
                Log.d("HomeViewModel", "Rate item: $base -> $to = $rate")
                CurrencyRate(from = base, to = to, rate = rate)
            }
            _rates.value = list
        }
    }

}

class WalletViewModel(private val repository: CurrencyRepository) : ViewModel() {
    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> = _currencies

    fun loadCurrencies() {
        viewModelScope.launch {
            val list = repository.getAvailableCurrencies()
            _currencies.value = list
        }
    }
}
