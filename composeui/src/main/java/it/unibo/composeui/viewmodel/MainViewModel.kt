package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.domain.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel principale che monitora lo stato della connessione di rete.
 * Utilizza un NetworkChecker per controllare periodicamente la connettività.
 */
class MainViewModel(
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    /**
     * Avvia un ciclo infinito che controlla la connessione ogni 5 secondi
     * e aggiorna il valore di isConnected.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                _isConnected.value = networkChecker.isNetworkAvailable()
                delay(5000)
            }
        }
    }
}
