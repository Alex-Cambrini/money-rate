package it.unibo.domain.di

import it.unibo.domain.repository.CurrencyRepository

interface RepositoryProvider {
    var currencyRepository: CurrencyRepository
}