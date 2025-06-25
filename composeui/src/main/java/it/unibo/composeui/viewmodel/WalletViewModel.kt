package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.di.UseCaseProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {

    private val addEntryUseCase = UseCaseProvider.addEntryUseCase
    private val updateEntryUseCase = UseCaseProvider.updateEntryUseCase
    private val removeEntryUseCase = UseCaseProvider.removeEntryUseCase
    private val getAllEntriesUseCase = UseCaseProvider.getAllEntryUseCase

    private val getAvailableCurrenciesUseCase = UseCaseProvider.getAvailableCurrenciesUseCase
    private val refreshCacheUseCase = UseCaseProvider.refreshCacheUseCase
    private val getCachedRatesUseCase = UseCaseProvider.getCachedRatesUseCase

    private val _entries = getAllEntriesUseCase
        .invoke()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val entries: StateFlow<List<WalletEntry>> = _entries

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    private val _ratesCache = MutableStateFlow<Map<String, Double>>(emptyMap())

    init {
        viewModelScope.launch {
            loadRatesCache()
            _entries.collect { calculateTotalInEuro(it) }
        }
    }

    private fun loadRatesCache() {
        viewModelScope.launch {
            val refreshed = refreshCacheUseCase.invoke()
            if (refreshed) {
                val rates = getCachedRatesUseCase.invoke()
                _ratesCache.value = rates + ("EUR" to 1.0)
                calculateTotalInEuro(_entries.value)
            }
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = getAvailableCurrenciesUseCase.invoke()
            _currencies.value = map.toList()
        }
    }

    private fun calculateTotalInEuro(entries: List<WalletEntry>) {
        var sum = 0.0
        for (entry in entries) {
            sum += convertToEuro(entry)
        }
        _total.value = sum
    }

    fun convertToEuro(entry: WalletEntry): Double {
        val rate = _ratesCache.value[entry.currencyCode] ?: return 0.0
        return entry.amount / rate
    }

    fun addWallet(currency: String, amount: Double) {
        viewModelScope.launch {
            val name = _currencies.value.find { it.first == currency }?.second ?: currency
            val newEntry = WalletEntry(id = 0, currencyCode = currency, currencyName = name, amount = amount)
            addEntryUseCase.invoke(newEntry)
        }
    }

    fun modifyWallet(entry: WalletEntry, delta: Double) {
        viewModelScope.launch {
            val updated = entry.copy(amount = entry.amount + delta)
            updateEntryUseCase.invoke(updated)
        }
    }

    fun deleteWallet(entry: WalletEntry) {
        viewModelScope.launch {
            removeEntryUseCase.invoke(entry)
        }
    }
}
