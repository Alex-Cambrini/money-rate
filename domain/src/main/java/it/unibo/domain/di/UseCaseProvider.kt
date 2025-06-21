package it.unibo.domain.di

import it.unibo.domain.repository.CurrencyRepository

object UseCaseProvider {
    private lateinit var currencyRepository: CurrencyRepository

    fun setup(repositoryProvider: RepositoryProvider) {
        this.currencyRepository = repositoryProvider.currencyRepository
    }
}