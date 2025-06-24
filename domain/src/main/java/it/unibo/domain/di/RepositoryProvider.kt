package it.unibo.domain.di

import it.unibo.domain.NetworkChecker
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository

interface RepositoryProvider {
    val currencyRepository: CurrencyRepository
    val walletRepository: WalletRepository
    val networkChecker: NetworkChecker
}