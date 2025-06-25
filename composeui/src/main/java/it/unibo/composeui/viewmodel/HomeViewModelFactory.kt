package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.currencyrate.ConvertCurrencyUseCase
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase

class HomeViewModelFactory(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(convertCurrencyUseCase, getAvailableCurrenciesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
