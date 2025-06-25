package it.unibo.domain.di

import it.unibo.domain.usecase.ConvertCurrencyUseCase
import it.unibo.domain.usecase.ConvertCurrencyUseCaseImpl
import it.unibo.domain.usecase.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.GetAvailableCurrenciesUseCaseImpl

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
