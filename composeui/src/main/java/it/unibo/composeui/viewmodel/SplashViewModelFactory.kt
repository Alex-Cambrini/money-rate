package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase


class SplashViewModelFactory(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(getRateUseCase, getAvailableCurrenciesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

