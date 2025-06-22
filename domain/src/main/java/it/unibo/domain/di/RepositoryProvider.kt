package it.unibo.domain.di

import it.unibo.domain.repository.CurrencyRepository

interface RepositoryProvider {
    val currencyRepository: CurrencyRepository
}