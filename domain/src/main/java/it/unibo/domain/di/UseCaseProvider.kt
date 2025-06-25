package it.unibo.domain.di

import it.unibo.domain.usecase.currencyrate.ConvertCurrencyUseCase
import it.unibo.domain.usecase.currencyrate.ConvertCurrencyUseCaseImpl
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCaseImpl

object UseCaseProvider {
    lateinit var convertCurrencyUseCase: ConvertCurrencyUseCase
    lateinit var getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase

    fun setup(repositoryProvider: RepositoryProvider) {
        convertCurrencyUseCase = ConvertCurrencyUseCaseImpl(
            currencyRepository = repositoryProvider.currencyRateRepository
        )
        getAvailableCurrenciesUseCase = GetAvailableCurrenciesUseCaseImpl(
            currencyRepository = repositoryProvider.currencyRepository
        )
    }
}
