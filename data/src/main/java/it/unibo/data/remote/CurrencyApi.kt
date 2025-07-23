package it.unibo.data.remote

import retrofit2.http.GET

/**
 * API per ottenere la lista delle valute disponibili.
 */
interface CurrencyApi {

    /**
     * Restituisce una mappa con codice e nome di tutte le valute supportate.
     */
    @GET("currencies")
    suspend fun getCurrencies(): Map<String, String>
}
