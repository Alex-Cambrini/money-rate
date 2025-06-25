package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.unibo.data.local.entity.WalletEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WalletEntryEntity)

    @Update
    suspend fun update(entry: WalletEntryEntity)

    @Delete
    suspend fun delete(entry: WalletEntryEntity)

    @Query("SELECT * FROM wallet")
    fun getAll(): Flow<List<WalletEntryEntity>>
}
