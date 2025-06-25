package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCase
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCase
import it.unibo.domain.usecase.wallet.AddEntryUseCase
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCase
import it.unibo.domain.usecase.wallet.RemoveEntryUseCase
import it.unibo.domain.usecase.wallet.UpdateEntryUseCase

class WalletViewModelFactory(
    private val addEntryUseCase: AddEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val removeEntryUseCase: RemoveEntryUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val refreshCacheUseCase: RefreshCacheUseCase,
    private val getCachedRatesUseCase: GetCachedRatesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            return WalletViewModel(
                addEntryUseCase = addEntryUseCase,
                updateEntryUseCase = updateEntryUseCase,
                removeEntryUseCase = removeEntryUseCase,
                getAllEntriesUseCase = getAllEntriesUseCase,
                getAvailableCurrenciesUseCase = getAvailableCurrenciesUseCase,
                refreshCacheUseCase = refreshCacheUseCase,
                getCachedRatesUseCase = getCachedRatesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
