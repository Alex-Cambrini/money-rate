package it.unibo.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.frankfurter.app/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val currencyApi: CurrencyApi by lazy {
        retrofit.create(CurrencyApi::class.java)
    }

    val currencyRateApi: CurrencyRateApi by lazy {
        retrofit.create(CurrencyRateApi::class.java)
    }
}
