package it.unibo.moneyrate.network

import it.unibo.data.remote.CurrencyApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    val api: CurrencyApi = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.app/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(CurrencyApi::class.java)
}