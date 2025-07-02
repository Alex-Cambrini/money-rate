package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase
import it.unibo.domain.usecase.home.CalculateConversionUseCase
import it.unibo.domain.usecase.home.CalculateTopRatesUseCase
import it.unibo.domain.usecase.home.GetSingleRateUseCase
import it.unibo.domain.usecase.home.LoadHomeDataUseCase

class HomeViewModelFactory(
    private val getRateUseCase: GetRateUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val loadHomeDataUseCase =
                LoadHomeDataUseCase(getAvailableCurrenciesUseCase, getRateUseCase)
            val calculateTopRatesUseCase = CalculateTopRatesUseCase()
            val calculateConversionUseCase = CalculateConversionUseCase()
            val getSingleRateUseCase = GetSingleRateUseCase(getRateUseCase)

            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                loadHomeDataUseCase,
                calculateTopRatesUseCase,
                calculateConversionUseCase,
                getSingleRateUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

