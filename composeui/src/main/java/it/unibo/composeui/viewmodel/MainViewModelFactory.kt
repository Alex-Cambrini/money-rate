package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.NetworkChecker

class MainViewModelFactory(
    private val networkChecker: NetworkChecker
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(networkChecker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}