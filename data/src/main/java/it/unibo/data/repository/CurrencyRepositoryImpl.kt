package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.remote.CurrencyApi
import it.unibo.data.remote.models.ExchangeRatesResponse
import it.unibo.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val dao: CurrencyRateDao,
    private val api: CurrencyApi
) : CurrencyRepository {

    companion object {
        private const val CACHE_VALIDITY = 10 * 60 * 1000L // 10 minutes
        private const val CACHE_BASE = "EUR"
    }



    override suspend fun getRate(from: String, to: String): Double? {
        if (from == to) return 1.0

        if (from == CACHE_BASE || to == CACHE_BASE) {
            if (!refreshCache()) return null
            val rates = dao.getRatesByBase(CACHE_BASE).associate { it.currencyCode to it.rate }

            return when {
                from == CACHE_BASE -> rates[to]
                to == CACHE_BASE -> rates[from]?.let { 1.0 / it }
                else -> null
            }
        }

        // Nessuno è EUR: chiamata API diretta senza cache
        val response = try {
            api.getLatestRates(from = from, to = to)
        } catch (e: Exception) {
            return null
        }

        return response.rates[to]
    }

    override suspend fun refreshCache(): Boolean {
        if (cacheIsRecent()) return true

        return try {
            val response = api.getLatestRates(from = CACHE_BASE, to = null)
            dao.insertRates(response.toEntities())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAvailableCurrencies(): List<String> {
        return try {
            api.getCurrencies().keys.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun cacheIsRecent(): Boolean {
        val rates = dao.getRatesByBase(CACHE_BASE)
        if (rates.isEmpty()) return false
        val expiryTime = System.currentTimeMillis() - CACHE_VALIDITY
        return rates.all { it.timestamp >= expiryTime }
    }

    private fun ExchangeRatesResponse.toEntities() = rates.map { (currency, rate) ->
        CurrencyRateEntity(currencyCode = currency, rate = rate, base = base, timestamp = System.currentTimeMillis())
    }
}
