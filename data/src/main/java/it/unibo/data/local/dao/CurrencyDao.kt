package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.unibo.data.local.entity.CurrencyEntity

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currencies")
    suspend fun getAll(): List<CurrencyEntity>

    @Query("DELETE FROM currencies")
    suspend fun clearAll()
}
