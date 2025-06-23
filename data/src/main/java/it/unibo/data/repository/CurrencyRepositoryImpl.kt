package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.remote.CurrencyApi
import it.unibo.data.remote.models.ExchangeRatesResponse
import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val dao: CurrencyRateDao,
    private val api: CurrencyApi
)  : CurrencyRepository {

    companion object {
        private const val CACHE_VALIDITY = 10 * 60 * 1000L // 10 minutes
    }

    override suspend fun getAllRates(base: String): Map<String, Double> {
        println("getAllRates called with base=$base")

        return if (base == "EUR" && cacheIsRecent()) {
            println("Cache is recent, loading rates from DB")
            val ratesFromDb = dao.getRatesByBase(base)
            println("Rates from DB: $ratesFromDb")
            ratesFromDb.associate { it.currencyCode to it.rate }
        } else {
            println("Cache not recent or base != EUR, fetching from API")
            val response = try {
                api.getLatestRates(from = base, to = null)
            } catch (e: Exception) {
                println("API call failed: ${e.message}")
                return emptyMap()
            }
            println("API response: $response")
            val entities = response.toEntities()
            println("Converted to entities: $entities")
            if (base == "EUR") {
                println("Inserting rates into DB")
                dao.insertRates(entities)
            }
            entities.associate { it.currencyCode to it.rate }
        }
    }

    override suspend fun getRate(from: String, to: String): Double {
        println("getRate called from=$from to=$to")

        if (from == "EUR" && cacheIsRecent()) {
            println("Cache is recent, checking DB")
            dao.getRate(from, to)?.let {
                println("Found rate in DB: $it")
                return it
            }
        }

        println("Fetching rate from API")
        val response = try {
            api.getLatestRates(from = from, to = to)
        } catch (e: Exception) {
            println("API call failed: ${e.message}")
            throw e
        }
        val entities = response.toEntities()
        println("Converted to entities: $entities")
        if (from == "EUR") {
            println("Inserting rates into DB")
            dao.insertRates(entities)
        }
        val rate = entities.firstOrNull { it.currencyCode == to }?.rate
        println("Rate found: $rate")
        return rate ?: throw IllegalArgumentException("Rate not available for $from -> $to")
    }

    override suspend fun getAvailableCurrencies(): List<String> {
        println("getAvailableCurrencies called")
        val response = try {
            api.getCurrencies()
        } catch (e: Exception) {
            println("API call failed: ${e.message}")
            return emptyList()
        }
        println("Available currencies: ${response.keys}")
        return response.keys.toList()
    }

    private suspend fun cacheIsRecent(): Boolean {
        val rates = dao.getRatesByBase("EUR")
        if (rates.isEmpty()) return false
        val expiryTime = System.currentTimeMillis() - CACHE_VALIDITY
        val oldRates = rates.filter { it.timestamp < expiryTime }
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
