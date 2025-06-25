package it.unibo.data.remote

import it.unibo.data.remote.models.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRateApi {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String? = null
    ): ExchangeRatesResponse

}