package it.unibo.data.remote

import retrofit2.http.GET

interface CurrencyApi {
    @GET("currencies")
    suspend fun getCurrencies(): Map<String, String>
}