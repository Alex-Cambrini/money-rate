package it.unibo.domain.repository

interface CurrencyRepository {
    suspend fun getRate(from: String, to: String): Double?
    suspend fun getAllRatesToEuro(): Map<String, Double>
    suspend fun refreshCache(): Boolean
    suspend fun getAvailableCurrencies(): List<String>
}