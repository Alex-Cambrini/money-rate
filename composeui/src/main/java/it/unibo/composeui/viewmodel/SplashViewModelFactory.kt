package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.InitializeAppDataUseCase


class SplashViewModelFactory(
    private val initializeAppDataUseCase: InitializeAppDataUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(initializeAppDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


