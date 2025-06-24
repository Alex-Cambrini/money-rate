package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletViewModel(
    private val currencyRepository: CurrencyRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _entries = walletRepository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val entries: StateFlow<List<WalletEntry>> = _entries

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    init {
        viewModelScope.launch {
            _entries.collect { calculateTotalInEuro(it) }
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = currencyRepository.getAvailableCurrencies()
            println("Currencies loaded: $map")
            _currencies.value = map.toList()
        }
    }

    private suspend fun calculateTotalInEuro(entries: List<WalletEntry>) {
        var sum = 0.0
        for (entry in entries) {
            val rate = currencyRepository.getRate(entry.currencyCode, "EUR") ?: continue
            sum += entry.amount * rate
        }
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

    suspend fun getRateToEuro(currency: String): Double? {
        return currencyRepository.getRate(currency, "EUR")
    }
}
