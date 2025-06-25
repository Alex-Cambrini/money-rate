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
import it.unibo.domain.usecase.wallet.UpdateEntryUseCase
import it.unibo.domain.usecase.wallet.UpdateEntryUseCaseImpl

object UseCaseProvider {
    // currency
    lateinit var getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase
    // currency rate
    lateinit var getCachedRatesUseCase: GetCachedRatesUseCase
    lateinit var getRateUseCase: GetRateUseCase
    lateinit var refreshCacheUseCase: RefreshCacheUseCase
    // wallet
    lateinit var addEntryUseCase: AddEntryUseCase
    lateinit var getAllEntryUseCase: GetAllEntriesUseCase
    lateinit var removeEntryUseCase: RemoveEntryUseCase
    lateinit var updateEntryUseCase: UpdateEntryUseCase

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
        getAllEntryUseCase = GetAllEntriesUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
        removeEntryUseCase = RemoveEntryUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
        updateEntryUseCase = UpdateEntryUseCaseImpl(
            walletRepository = repositoryProvider.walletRepository
        )
    }
}
