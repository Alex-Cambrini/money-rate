package it.unibo.domain.repository

import it.unibo.domain.model.WalletEntry
import kotlinx.coroutines.flow.Flow

/**
 * Gestisce le operazioni CRUD per le voci del portafoglio.
 */
interface WalletRepository {
    suspend fun addEntry(entry: WalletEntry)
    suspend fun updateEntry(entry: WalletEntry)
    suspend fun removeEntry(entry: WalletEntry)
    fun getAllEntries(): Flow<List<WalletEntry>>
}
