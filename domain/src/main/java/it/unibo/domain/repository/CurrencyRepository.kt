package it.unibo.domain.repository

interface CurrencyRepository {
    suspend fun getRate(from: String, to: String): Double
    suspend fun getAllRates(base: String): Map<String, Double>
}