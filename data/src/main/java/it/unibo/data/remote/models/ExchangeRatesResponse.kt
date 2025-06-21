package it.unibo.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse (
    @Json(name = "amount") val amount   : Double,
    @Json(name = "base") val base       : String,
    @Json(name = "date") val date       : String,
    @Json(name = "rates") val rates     : Map<String, Double>
)