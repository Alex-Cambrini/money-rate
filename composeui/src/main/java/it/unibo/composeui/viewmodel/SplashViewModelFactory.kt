package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.InitializeAppDataUseCase

/**
 * Factory per creare un'istanza di SplashViewModel
 * fornendo il use case necessario per inizializzare i dati dell'applicazione.
 */
class SplashViewModelFactory(
    private val initializeAppDataUseCase: InitializeAppDataUseCase
) : ViewModelProvider.Factory {
    /**
     * Crea un'istanza di SplashViewModel se il tipo richiesto è compatibile.
     * Lancia un'eccezione se il tipo richiesto non è supportato.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(initializeAppDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


