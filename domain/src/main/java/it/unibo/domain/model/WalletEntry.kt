package it.unibo.domain.model

/**
 * Rappresenta una voce nel portafoglio.
 *
 * @param id Identificatore univoco autogenerato (0 se non ancora salvato).
 * @param currencyCode Codice valuta (es. "EUR").
 * @param currencyName Nome della valuta (es. "Euro").
 * @param amount Quantità posseduta.
 */
data class WalletEntry(
    val id: Int = 0,
    val currencyCode: String,
    val currencyName: String,
    val amount: Double
)
