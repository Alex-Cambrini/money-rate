package it.unibo.domain.repository

import it.unibo.domain.model.Currency

interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): List<Currency>
}