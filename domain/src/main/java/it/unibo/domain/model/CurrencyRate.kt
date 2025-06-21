package it.unibo.domain.model

data class CurrencyRate(
    val from: String,
    val to: String,
    val rate: Double
)