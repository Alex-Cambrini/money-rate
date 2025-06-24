package it.unibo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currencyCode: String,
    val currencyName: String,
    val amount: Double
)
