package it.unibo.domain.repository

/**
 * Gestisce l’accesso ai tassi di cambio valute,
 * con metodi per recuperare, cache e aggiornare i dati.
 */
interface CurrencyRateRepository {
    suspend fun getRate(from: String, to: String): Double?
    suspend fun getCachedRates(): Map<String, Double>
    suspend fun refreshCache(): Boolean
}