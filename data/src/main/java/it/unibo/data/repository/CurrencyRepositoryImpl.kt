package it.unibo.data.repository

import it.unibo.data.local.dao.CurrencyDao
import it.unibo.data.local.entity.CurrencyEntity
import it.unibo.data.remote.CurrencyApi
import it.unibo.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val dao: CurrencyDao,
    private val api: CurrencyApi
) : CurrencyRepository {

    companion object {
        private const val CACHE_VALIDITY = 24 * 60 * 60 * 1000L // 24 ore
    }

    override suspend fun getAvailableCurrencies(): Map<String, String> {
        val cached = dao.getAll()
        return if (cacheIsRecent(cached)) {
            cached.associate { it.code to it.name }
        } else {
            try {
                val response = api.getCurrencies()
                dao.clearAll()
                dao.insertAll(response.toEntities())
                response
            } catch (e: Exception) {
                if (cached.isNotEmpty()) {
                    cached.associate { it.code to it.name }
                } else {
                    emptyMap()
                }
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
}