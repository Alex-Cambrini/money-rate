package it.unibo.domain.repository

import it.unibo.domain.model.Currency

/**
 * Gestisce l’accesso alla lista di valute disponibili.
 */
interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): List<Currency>
}