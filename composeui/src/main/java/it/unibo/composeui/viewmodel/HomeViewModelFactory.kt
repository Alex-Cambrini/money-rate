package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.home.CalculateConversionUseCase
import it.unibo.domain.usecase.home.CalculateTopRatesUseCase
import it.unibo.domain.usecase.home.GetSingleRateUseCase
import it.unibo.domain.usecase.home.LoadHomeDataUseCase

/**
 * Factory per la creazione di istanze di HomeViewModel.
 * Permette di fornire manualmente i UseCase necessari al ViewModel
 */
class HomeViewModelFactory(
    private val loadHomeDataUseCase: LoadHomeDataUseCase,
    private val calculateTopRatesUseCase: CalculateTopRatesUseCase,
    private val calculateConversionUseCase: CalculateConversionUseCase,
    private val getSingleRateUseCase: GetSingleRateUseCase
) : ViewModelProvider.Factory {
    /**
     * Crea un'istanza di HomeViewModel con i UseCase forniti.
     * Lancia un'eccezione se il tipo richiesto non è supportato.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
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


