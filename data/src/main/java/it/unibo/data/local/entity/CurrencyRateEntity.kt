package it.unibo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta un tasso di cambio nel database locale.
 *
 * @property currencyCode codice della valuta target (es. "USD")
 * @property rate valore del tasso di cambio rispetto alla valuta base
 * @property base valuta di riferimento
 * @property timestamp momento dell’ultimo aggiornamento (millisecondi)
 */
@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val currencyCode: String,
    val rate: Double,
    val base: String,
    val timestamp: Long
)
