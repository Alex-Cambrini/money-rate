package it.unibo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta una valuta nel database locale.
 *
 * @property code codice valuta unico (es. "EUR")
 * @property name nome descrittivo della valuta (es. "Euro")
 * @property timestamp momento in cui è stata aggiornata questa voce (millisecondi)
 */
@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val timestamp: Long
)
