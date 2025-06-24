package it.unibo.domain.model

data class WalletEntry(
    val id: Int = 0,
    val currencyCode: String,
    val currencyName: String,
    val amount: Double
)