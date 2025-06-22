package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.unibo.data.local.entity.CurrencyRateEntity

@Dao
interface CurrencyRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT * FROM currency_rates WHERE base = :base")
    suspend fun getRatesByBase(base: String): List<CurrencyRateEntity>

    @Query("SELECT rate FROM currency_rates WHERE base = :base AND currencyCode = :toCurrency LIMIT 1")
    suspend fun getRate(base: String, toCurrency: String): Double?

    @Query("DELETE FROM currency_rates WHERE timestamp < :expiryTime")
    suspend fun deleteOldRates(expiryTime: Long)
}