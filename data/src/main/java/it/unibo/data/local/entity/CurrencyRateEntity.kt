package it.unibo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val currencyCode: String,
    val rate: Double,
    val base: String,
    val timestamp: Long
)