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
import it.unibo.domain.usecase.wallet.UpdateWalletAmountUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel che gestisce le operazioni sul portafoglio valute:
 * - caricamento voci e tassi
 * - aggiunta, modifica, eliminazione voci
 */
class WalletViewModel(
    private val addEntryUseCase: AddEntryUseCase,
    private val removeEntryUseCase: RemoveEntryUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val refreshCacheUseCase: RefreshCacheUseCase,
    private val getCachedRatesUseCase: GetCachedRatesUseCase,
    private val updateWalletAmountUseCase: UpdateWalletAmountUseCase,
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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

    /**
     * Carica i tassi di cambio dalla cache e aggiunge l'EUR con valore 1.0.
     */
    fun loadRatesCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                refreshCacheUseCase.invoke()
            } catch (_: Exception) {
            }
            val rates = getCachedRatesUseCase.invoke()
            _ratesCache.value = rates + ("EUR" to 1.0)
        }
    }

    /**
     * Carica l'elenco delle valute disponibili (codice e nome)
     */
    fun loadCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAvailableCurrenciesUseCase.invoke()
            _currencies.value = list.map { it.code to it.name }
        }
    }

    /**
     * Calcola il totale delle voci del portafoglio convertito in Euro.
     * Utilizza i tassi di cambio per la conversione.
     */
    private fun calculateTotalInEuro(entries: List<WalletEntry>, rates: Map<String, Double>) {
        var sum = 0.0
        for (entry in entries) {
            sum += convertToEuro(entry, rates)
        }
        _total.value = sum
    }

    /**
     * Converte una voce in euro, usando il tasso di cambio fornito
     */
    fun convertToEuro(entry: WalletEntry, rates: Map<String, Double>): Double {
        val rate = rates[entry.currencyCode] ?: return 0.0
        val euroValue = entry.amount / rate
        return euroValue
    }

    /**
     * Aggiunge una nuova voce al portafoglio
     */
    fun addWallet(currency: String, amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val name = _currencies.value.find { it.first == currency }?.second ?: currency
            val newEntry =
                WalletEntry(id = 0, currencyCode = currency, currencyName = name, amount = amount)
            addEntryUseCase.invoke(newEntry)
        }
    }

    /**
     * Modifica l'importo di una voce esistente
     */
    fun modifyWallet(entry: WalletEntry, delta: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _errorMessage.value = null
            try {
                updateWalletAmountUseCase.invoke(entry, delta)
                _errorMessage.value = null
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * Elimina una voce dal portafoglio
     */
    fun deleteWallet(entry: WalletEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            removeEntryUseCase.invoke(entry)
        }
    }

    /**
     * Converte una stringa in Double accettando anche virgola come separatore decimale
     */
    fun parseAmount(input: String): Double? {
        return input.replace(',', '.').toDoubleOrNull()
    }

    /**
     * Converte una stringa in Double per il delta, accettando anche virgola come separatore decimale
     */
    fun parseDelta(input: String): Double? {
        return input.replace(',', '.').toDoubleOrNull()
    }

    /**
     * Resetta il messaggio di errore
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
