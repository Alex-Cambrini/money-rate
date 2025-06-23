package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CurrencyRepository) : ViewModel() {
    private val _rate = MutableStateFlow<Double?>(null)
    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val rate: StateFlow<Double?> = _rate
    val currencies: StateFlow<List<String>> = _currencies

    fun loadRate(base: String = "EUR", to: String) {
        viewModelScope.launch {
            val r = repository.getRate(base, to)
            _rate.value = r
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val list = repository.getAvailableCurrencies()
            _currencies.value = list
        }
    }
}
