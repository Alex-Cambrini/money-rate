package it.unibo.domain.model

/**
 * Rappresenta il tasso di cambio tra due valute.
 *
 * @param from valuta di partenza
 * @param to valuta di destinazione
 * @param rate valore del tasso di cambio
 */
data class CurrencyRate(
    val from: String,
    val to: String,
    val rate: Double
)
