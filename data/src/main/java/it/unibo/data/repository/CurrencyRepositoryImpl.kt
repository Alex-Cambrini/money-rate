package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.remote.CurrencyApi
import it.unibo.data.remote.models.ExchangeRatesResponse
import it.unibo.domain.model.CurrencyRate

class CurrencyRepositoryImpl(
    private val dao: CurrencyRateDao,
    private val api: CurrencyApi
) {

    companion object {
        private const val CACHE_VALIDITY = 10 * 60 * 1000L // 10 minute
    }

    suspend fun getRates(from: String): List<CurrencyRate> {
        return if (from == "EUR" && cacheIsRecent()) {
            val cachedEntities = dao.getRatesByBase(from)
            cachedEntities.map { it.toDomain() }
        } else {
            val to = "EUR"
            val response = api.getLatestRates(from = from, to = to)
            val entities = response.toEntities()
            dao.insertRates(entities)
            entities.map { it.toDomain() }
        }
    }

    suspend fun getAvailableCurrencies(): List<String> {
        return api.getCurrencies().keys.sorted()
    }

    private suspend fun cacheIsRecent(): Boolean {
        val expiryTime = System.currentTimeMillis() - CACHE_VALIDITY
        val oldRates = dao.getRatesByBase("EUR").filter { it.timestamp < expiryTime }
        return oldRates.isEmpty()
    }

    private fun ExchangeRatesResponse.toEntities(): List<CurrencyRateEntity> =
        rates.map { (toCurrency, rate) ->
            CurrencyRateEntity(
                currencyCode = toCurrency,
                rate = rate,
                base = base,
                timestamp = System.currentTimeMillis()
            )
        }

    private fun CurrencyRateEntity.toDomain(): CurrencyRate =
        CurrencyRate(
            from = base,
            to = currencyCode,
            rate = rate
        )
}