package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.unibo.data.local.entity.WalletEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per gestire le voci del portafoglio nel database locale.
 */
@Dao
interface WalletDao {

    /**
     * Inserisce o aggiorna una voce del portafoglio.
     * Sovrascrive in caso di conflitto sull’ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WalletEntryEntity)

    /**
     * Aggiorna una voce esistente del portafoglio.
     */
    @Update
    suspend fun update(entry: WalletEntryEntity)

    /**
     * Elimina una voce del portafoglio.
     */
    @Delete
    suspend fun delete(entry: WalletEntryEntity)

    /**
     * Restituisce un Flow con la lista aggiornata delle voci del portafoglio.
     */
    @Query("SELECT * FROM wallet")
    fun getAll(): Flow<List<WalletEntryEntity>>
}
