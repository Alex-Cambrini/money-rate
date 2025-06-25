package it.unibo.data.repository

import it.unibo.data.remote.CurrencyApi
import it.unibo.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val api: CurrencyApi
) : CurrencyRepository {


    override suspend fun getAvailableCurrencies(): Map<String, String> {
        return try {
            api.getCurrencies()
        } catch (e: Exception) {
            emptyMap()
        }
    }

}
