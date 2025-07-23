package it.unibo.composeui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCase
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCase
import it.unibo.domain.usecase.wallet.AddEntryUseCase
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCase
import it.unibo.domain.usecase.wallet.RemoveEntryUseCase
import it.unibo.domain.usecase.wallet.UpdateWalletAmountUseCase

/**
 * Factory per la creazione del ViewModel WalletViewModel.
 * Inietta tutte le dipendenze necessarie tramite i relativi UseCase.
 */
class WalletViewModelFactory(
    private val addEntryUseCase: AddEntryUseCase,
    private val removeEntryUseCase: RemoveEntryUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val refreshCacheUseCase: RefreshCacheUseCase,
    private val getCachedRatesUseCase: GetCachedRatesUseCase,
    private val updateWalletAmountUseCase: UpdateWalletAmountUseCase
) : ViewModelProvider.Factory {

    /**
     * Crea un'istanza di WalletViewModel se il tipo richiesto è compatibile.
     * Lancia un'eccezione se il tipo richiesto non è supportato.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            return WalletViewModel(
                addEntryUseCase = addEntryUseCase,
                removeEntryUseCase = removeEntryUseCase,
                getAllEntriesUseCase = getAllEntriesUseCase,
                getAvailableCurrenciesUseCase = getAvailableCurrenciesUseCase,
                refreshCacheUseCase = refreshCacheUseCase,
                getCachedRatesUseCase = getCachedRatesUseCase,
                updateWalletAmountUseCase = updateWalletAmountUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
