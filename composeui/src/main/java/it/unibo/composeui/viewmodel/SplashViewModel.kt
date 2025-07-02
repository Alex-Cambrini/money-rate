package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase

class SplashViewModel(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModel() {
    private val homeViewModel = HomeViewModel(getRateUseCase, getAvailableCurrenciesUseCase)
    val isDataReady = homeViewModel.isDataReady
    val isError = homeViewModel.isError

    fun initialize() {
        homeViewModel.initializeIfNeeded()
    }
}