package it.unibo.data.remote

import it.unibo.data.remote.models.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API per ottenere i tassi di cambio aggiornati.
 */
interface CurrencyRateApi {

    /**
     * Restituisce i tassi di cambio più recenti a partire da una valuta base.
     *
     * @param from codice valuta di partenza (es. "EUR")
     * @param to codice valuta di destinazione (opzionale; se null, restituisce tutte)
     * @return risposta con i tassi di cambio
     */
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String? = null
    ): ExchangeRatesResponse
}
