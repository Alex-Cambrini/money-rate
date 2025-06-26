package it.unibo.domain.model

data class WrappedWalletData(
    val entries: List<WalletEntry>,
    val ratesCache: Map<String, Double>
)