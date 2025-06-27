package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModel() {
    private val _rate = MutableStateFlow<Double?>(null)
    private val _currencies = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currencies: StateFlow<List<Pair<String, String>>> = _currencies

    private val _latestRates = MutableStateFlow<Map<String, Double>>(emptyMap())

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    val result: StateFlow<Double?> = combine(_rate, _amount) { currentRate, currentAmount ->
        val amountDouble = currentAmount.toDoubleOrNull()
        if (amountDouble != null && currentRate != null) amountDouble * currentRate else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val top10Rates: StateFlow<Map<String, Double>> = _latestRates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
        .combine(_currencies) { rates, availableCurrencies ->
            if (rates.isNotEmpty() && availableCurrencies.isNotEmpty()) {
                rates.entries
                    .sortedBy { it.value }
                    .take(10)
                    .associate { it.key to it.value }
            } else {
                emptyMap()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )


    fun loadRate(base: String = "EUR", to: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currencyRate = getRateUseCase.invoke(base, to)
            _rate.value = currencyRate.rate
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAvailableCurrenciesUseCase.invoke()
            _currencies.value = list.map { it.code to it.name }
        }
    }

    fun loadAllRatesAgainstEuro(targets: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mutableMapOf<String, Double>()
            for (target in targets) {
                val rate = getRateUseCase.invoke("EUR", target).rate
                if (rate != 0.0) result[target] = rate
            }
            _latestRates.value = result
        }
    }

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
    }
}