package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.usecase.home.CalculateConversionUseCase
import it.unibo.domain.usecase.home.CalculateTopRatesUseCase
import it.unibo.domain.usecase.home.GetSingleRateUseCase
import it.unibo.domain.usecase.home.LoadHomeDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel principale della schermata Home.
 * Gestisce il caricamento dei dati iniziali, conversioni valutarie,
 * aggiornamento delle valute disponibili e calcolo dei tassi principali.
 */
class HomeViewModel(
    private val loadHomeDataUseCase: LoadHomeDataUseCase,
    private val calculateTopRatesUseCase: CalculateTopRatesUseCase,
    private val calculateConversionUseCase: CalculateConversionUseCase,
    private val getSingleRateUseCase: GetSingleRateUseCase,
) : ViewModel() {

    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    private val _isLoadingRate = MutableStateFlow(false)
    val isLoadingRate: StateFlow<Boolean> = _isLoadingRate

    private val _latestRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val top10Rates: StateFlow<Map<String, Double>> = _latestRates
        .map { calculateTopRatesUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _rate = MutableStateFlow<Double?>(null)

    val result: StateFlow<Double?> = combine(_amount, _rate) { amount, rate ->
        val normalizedAmount = amount.replace(',', '.')
        calculateConversionUseCase(normalizedAmount, rate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Carica le valute disponibili e i tassi attuali al primo avvio della schermata.
     */
    fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            loadHomeDataUseCase().onSuccess {
                _currencies.value = it.currencies
                _latestRates.value = it.rates
            }.onFailure {
            }
        }
    }

    /**
     * Resetta il tasso di conversione.
     */
    fun resetResult() {
        _rate.value = null
    }

    /**
     * Resetta sia l'importo che il tasso di conversione.
     */
    fun resetConversion() {
        _amount.value = ""
        resetResult()
    }

    /**
     * Aggiorna l’importo digitato dall’utente.
     */
    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
    }

    /**
     * Carica il tasso di conversione tra due valute specificate.
     */
    fun loadRate(base: String = "EUR", to: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingRate.value = true
            val rate = getSingleRateUseCase.invoke(base, to)
            _rate.value = rate
            _isLoadingRate.value = false
        }
    }
}
