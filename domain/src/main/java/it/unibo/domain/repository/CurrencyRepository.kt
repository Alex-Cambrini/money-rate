package it.unibo.domain.repository

interface CurrencyRepository {
    suspend fun getRate(from: String, to: String): Double?
    suspend fun getCachedRates(): Map<String, Double>
    suspend fun refreshCache(): Boolean
    suspend fun getAvailableCurrencies(): Map<String, String>
}