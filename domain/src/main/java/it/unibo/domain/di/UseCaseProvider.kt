package it.unibo.domain.di

import it.unibo.domain.usecase.ConvertCurrencyUseCase
import it.unibo.domain.usecase.ConvertCurrencyUseCaseImpl

object UseCaseProvider {
    lateinit var convertCurrencyUseCase: ConvertCurrencyUseCase

    fun setup(repositoryProvider: RepositoryProvider) {
        convertCurrencyUseCase = ConvertCurrencyUseCaseImpl(
            currencyRepository = repositoryProvider.currencyRepository
        )
    }
}