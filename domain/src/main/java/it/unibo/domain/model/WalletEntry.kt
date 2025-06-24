package it.unibo.domain.model

data class WalletEntry(
    val id: Int = 0,
    val currency: String,
    val amount: Double
)