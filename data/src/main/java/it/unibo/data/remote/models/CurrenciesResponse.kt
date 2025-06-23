package it.unibo.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrenciesResponse(
    val currencies: Map<String, String>
)