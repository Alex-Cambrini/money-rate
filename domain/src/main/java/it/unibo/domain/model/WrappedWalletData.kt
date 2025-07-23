package it.unibo.domain.model

/**
 * Contiene i dati del portafoglio e la cache dei tassi di cambio.
 *
 * @param entries Lista delle voci del portafoglio.
 * @param ratesCache Mappa dei tassi di cambio con chiave valuta e valore tasso.
 */
data class WrappedWalletData(
    val entries: List<WalletEntry>,
    val ratesCache: Map<String, Double>
)
