package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.model.WrappedWalletData
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCase
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCase
import it.unibo.domain.usecase.wallet.AddEntryUseCase
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCase
import it.unibo.domain.usecase.wallet.RemoveEntryUseCase
import it.unibo.domain.usecase.wallet.UpdateEntryUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletViewModel(
    private val addEntryUseCase: AddEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val removeEntryUseCase: RemoveEntryUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val refreshCacheUseCase: RefreshCacheUseCase,
    private val getCachedRatesUseCase: GetCachedRatesUseCase
) : ViewModel() {

    private val _entries = getAllEntriesUseCase
        .invoke()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _ratesCache = MutableStateFlow<Map<String, Double>>(emptyMap())

    val combinedData: StateFlow<WrappedWalletData> = combine(
        _entries,
        _ratesCache
    ) { entries, ratesCache ->
        WrappedWalletData(entries, ratesCache)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, WrappedWalletData(emptyList(), emptyMap()))


    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    init {
        viewModelScope.launch {
            loadRatesCache()
        }
        viewModelScope.launch {
            combinedData.collect { data ->
                if (data.ratesCache.isNotEmpty()) {
                    calculateTotalInEuro(data.entries, data.ratesCache)
                } else {
                    _total.value = 0.0
                }
            }
        }
    }

    fun loadRatesCache() {
        viewModelScope.launch {
            try {
                refreshCacheUseCase.invoke()
            } catch (_: Exception) { }
            val rates = getCachedRatesUseCase.invoke()
            _ratesCache.value = rates + ("EUR" to 1.0)
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val map = getAvailableCurrenciesUseCase.invoke()
            _currencies.value = map.toList()
        }
    }

    private fun calculateTotalInEuro(entries: List<WalletEntry>, rates: Map<String, Double>) {
        var sum = 0.0
        for (entry in entries) {
            sum += convertToEuro(entry, rates)
        }
        _total.value = sum
    }

    fun convertToEuro(entry: WalletEntry, rates: Map<String, Double>): Double {
        val rate = rates[entry.currencyCode]
        if (rate == null) {
            return 0.0
        }
        val euroValue = entry.amount / rate
        return euroValue
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
