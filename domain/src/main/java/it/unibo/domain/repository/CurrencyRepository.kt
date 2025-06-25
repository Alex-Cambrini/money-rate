package it.unibo.domain.repository

interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): Map<String, String>
}