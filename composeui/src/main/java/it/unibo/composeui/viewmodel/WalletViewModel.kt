package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletViewModel(
    private val currencyUseCase: GetAvailableCurrenciesUseCase,
    private val walletRepository: WalletRepository // TODO: implement use case
) : ViewModel() {

    private val _entries = walletRepository.getAllEntries()
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

    fun convertToEuro(entry: WalletEntry): Double {
        val rate = _ratesCache.value[entry.currencyCode] ?: return 0.0
        return entry.amount / rate
    }

    private fun loadRatesCache() {
        viewModelScope.launch {
            val refreshed = currencyRepository.refreshCache()
            if (refreshed) {
                val rates = currencyRepository.getCachedRates()
                _ratesCache.value = rates + ("EUR" to 1.0)
                calculateTotalInEuro(_entries.value)
            } else {
                // gestisci errore o usa cache vecchia
            }
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = currencyRepository.getAvailableCurrencies()
            _currencies.value = map.toList()
        }
    }

    private fun calculateTotalInEuro(entries: List<WalletEntry>) {
        var sum = 0.0
        for (entry in entries) {
            sum += convertToEuro(entry)        }
        _total.value = sum
    }

    fun addWallet(currency: String, amount: Double) {
        viewModelScope.launch {
            val name = _currencies.value.find { it.first == currency }?.second ?: currency
            val newEntry = WalletEntry(id = 0, currencyCode = currency, currencyName = name, amount = amount)
            walletRepository.addEntry(newEntry)
        }
    }

    fun modifyWallet(entry: WalletEntry, delta: Double) {
        viewModelScope.launch {
            val updated = entry.copy(amount = entry.amount + delta)
            walletRepository.updateEntry(updated)
        }
    }

    fun deleteWallet(entry: WalletEntry) {
        viewModelScope.launch {
            walletRepository.removeEntry(entry)
        }
    }
}
