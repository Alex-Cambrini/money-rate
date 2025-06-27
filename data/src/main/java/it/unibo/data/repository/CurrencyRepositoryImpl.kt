package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyDao
import it.unibo.data.local.entity.CurrencyEntity
import it.unibo.data.remote.CurrencyApi
import it.unibo.domain.model.Currency
import it.unibo.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val dao: CurrencyDao,
    private val api: CurrencyApi
) : CurrencyRepository {

    companion object {
        private const val CACHE_VALIDITY = 24 * 60 * 60 * 1000L // 24 ore
    }

    override suspend fun getAvailableCurrencies(): List<Currency> {
        val cached = dao.getAll()
        return if (cacheIsRecent(cached)) {
            cached.map { it.toCurrency() }
        } else {
            try {
                val response = api.getCurrencies()
                dao.clearAll()
                dao.insertAll(response.toEntities())
                response.map { Currency(code = it.key, name = it.value) }
            } catch (e: Exception) {
                cached.map { it.toCurrency() }
            }
        }
    }

    private fun cacheIsRecent(currencies: List<CurrencyEntity>): Boolean {
        if (currencies.isEmpty()) return false
        val expiryTime = System.currentTimeMillis() - CACHE_VALIDITY
        return currencies.all { it.timestamp >= expiryTime }
    }

    private fun Map<String, String>.toEntities() = map { (code, name) ->
        CurrencyEntity(
            code = code,
            name = name,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun CurrencyEntity.toCurrency(): Currency {
        return Currency(
            code = code,
            name = name
        )
    }
}