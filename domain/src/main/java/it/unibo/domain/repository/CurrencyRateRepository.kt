package it.unibo.domain.repository

interface CurrencyRateRepository {
    suspend fun getRate(from: String, to: String): Double?
    suspend fun getCachedRates(): Map<String, Double>
    suspend fun refreshCache(): Boolean
}