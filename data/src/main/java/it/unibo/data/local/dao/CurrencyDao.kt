package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.unibo.data.local.entity.CurrencyEntity

/**
 * DAO per accedere e modificare la tabella delle valute nel database.
 */
@Dao
interface CurrencyDao {

    /**
     * Inserisce o aggiorna la lista di valute.
     * In caso di conflitto sovrascrive la voce esistente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyEntity>)

    /**
     * Recupera tutte le valute presenti nel database.
     */
    @Query("SELECT * FROM currencies")
    suspend fun getAll(): List<CurrencyEntity>

    /**
     * Cancella tutte le valute memorizzate nel database.
     */
    @Query("DELETE FROM currencies")
    suspend fun clearAll()
}
