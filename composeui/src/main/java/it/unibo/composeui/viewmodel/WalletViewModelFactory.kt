package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository

class WalletViewModelFactory(
    private val currencyRepository: CurrencyRepository,
    private val walletRepository: WalletRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            return WalletViewModel(currencyRepository, walletRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
