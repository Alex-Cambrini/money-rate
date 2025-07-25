package it.unibo.domain.di

import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCaseImpl
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCase
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCaseImpl
import it.unibo.domain.usecase.currencyrate.GetRateUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCaseImpl
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCase
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCaseImpl
import it.unibo.domain.usecase.wallet.AddEntryUseCase
import it.unibo.domain.usecase.wallet.AddEntryUseCaseImpl
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCase
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCaseImpl
import it.unibo.domain.usecase.wallet.RemoveEntryUseCase
import it.unibo.domain.usecase.wallet.RemoveEntryUseCaseImpl
import it.unibo.domain.usecase.wallet.UpdateWalletAmountUseCase
import it.unibo.domain.usecase.wallet.UpdateWalletAmountUseCaseImpl

/**
 * Oggetto singleton che fornisce istanze dei Use Case del dominio.
 * Gestisce le dipendenze tramite setup con un RepositoryProvider.
 */
object UseCaseProvider {
    // currency
    lateinit var getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase

    // currency rate
    lateinit var getCachedRatesUseCase: GetCachedRatesUseCase
    lateinit var getRateUseCase: GetRateUseCase
    lateinit var refreshCacheUseCase: RefreshCacheUseCase

    // wallet
    lateinit var addEntryUseCase: AddEntryUseCase
    lateinit var getAllEntriesUseCase: GetAllEntriesUseCase
    lateinit var removeEntryUseCase: RemoveEntryUseCase
    lateinit var updateWalletAmountUseCase: UpdateWalletAmountUseCase

    /**
     * Inizializza i use case con le implementazioni concrete,
     * utilizzando le repository fornite dal RepositoryProvider.
     *
     * @param repositoryProvider fornitore delle repository necessarie ai use case
     */
    fun setup(repositoryProvider: RepositoryProvider) {
        getAvailableCurrenciesUseCase = GetAvailableCurrenciesUseCaseImpl(
            currencyRepository = repositoryProvider.currencyRepository
        )
        getCachedRatesUseCase = GetCachedRatesUseCaseImpl(
            currencyRateRepository = repositoryProvider.currencyRateRepository
        )
        getRateUseCase = GetRateUseCaseImpl(
            currencyRateRepository = repositoryProvider.currencyRateRepository
        )
        refreshCacheUseCase = RefreshCacheUseCaseImpl(
            currencyRateRepository = repositoryProvider.currencyRateRepository
        )
        addEntryUseCase = AddEntryUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
        getAllEntriesUseCase = GetAllEntriesUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
        removeEntryUseCase = RemoveEntryUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
        updateWalletAmountUseCase = UpdateWalletAmountUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
    }
}
