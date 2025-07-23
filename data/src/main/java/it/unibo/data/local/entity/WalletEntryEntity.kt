package it.unibo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta una voce nel portafoglio.
 *
 * @property id identificatore univoco autogenerato
 * @property currencyCode codice valuta (es. "USD")
 * @property currencyName nome della valuta (es. "Dollaro USA")
 * @property amount quantità posseduta di questa valuta
 */
@Entity(tableName = "wallet")
data class WalletEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currencyCode: String,
    val currencyName: String,
    val amount: Double
)
