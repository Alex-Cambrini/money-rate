package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.remote.CurrencyRateApi
import it.unibo.data.remote.models.ExchangeRatesResponse
import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRateRepository

class CurrencyRateRepositoryImpl(
    private val dao: CurrencyRateDao,
    private val api: CurrencyRateApi
) : CurrencyRateRepository {

    companion object {
        private const val CACHE_VALIDITY = 10 * 60 * 1000L // 10 minutes
        private const val CACHE_BASE = "EUR"
    }

    override suspend fun getRate(from: String, to: String): Double? {
        if (from == to) return 1.0

        if (from == CACHE_BASE || to == CACHE_BASE) {
            if (!refreshCache()) return null
            val rates = dao.getRatesByBase(CACHE_BASE)
                .map { it.toCurrencyRate() }
                .associate { it.to to it.rate }

            return if (from == CACHE_BASE) {
                rates[to]
            } else {
                rates[from]?.let { 1.0 / it }
            }
        }
        val response = try {
            api.getLatestRates(from = from, to = to)
        } catch (e: Exception) {
            return null
        }
        return response.rates[to]
    }

    override suspend fun getCachedRates(): Map<String, Double> {
        val rates = dao.getRatesByBase(CACHE_BASE)
            .map { it.toCurrencyRate() }
        return rates.associate { it.to to it.rate }
    }

    override suspend fun refreshCache(): Boolean {
        if (cacheIsRecent()) return true

        return try {
            val response = api.getLatestRates(from = CACHE_BASE, to = null)
            dao.clearRatesByBase(CACHE_BASE)
            dao.insertRates(response.toEntities())
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun cacheIsRecent(): Boolean {
        val rates = dao.getRatesByBase(CACHE_BASE)
        if (rates.isEmpty()) return false
        val expiryTime = System.currentTimeMillis() - CACHE_VALIDITY
        return rates.all { it.timestamp >= expiryTime }
    }

    private fun ExchangeRatesResponse.toEntities() = rates.map { (currency, rate) ->
        CurrencyRateEntity(
            currencyCode = currency,
            rate = rate,
            base = base,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun CurrencyRateEntity.toCurrencyRate(): CurrencyRate {
        return CurrencyRate(
            from = base,
            to = currencyCode,
            rate = rate
        )
    }
}
