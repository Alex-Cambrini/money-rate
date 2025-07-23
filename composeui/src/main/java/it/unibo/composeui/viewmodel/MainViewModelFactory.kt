package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.NetworkChecker

/**
 * Factory per creare un'istanza di MainViewModel.
 * Inietta la dipendenza NetworkChecker necessaria per monitorare la connessione di rete.
 */
class MainViewModelFactory(
    private val networkChecker: NetworkChecker
) : ViewModelProvider.Factory {
    /**
     * Crea un'istanza di MainViewModel se il tipo richiesto è compatibile.
     * Lancia un'eccezione se il tipo richiesto non è supportato.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(networkChecker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}