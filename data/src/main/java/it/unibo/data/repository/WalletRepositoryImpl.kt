package it.unibo.data.repository

import it.unibo.data.local.dao.WalletDao
import it.unibo.data.local.entity.WalletEntryEntity
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementazione del repository per la gestione del portafoglio.
 * Fornisce operazioni CRUD su valute possedute, con mappatura tra entità e dominio.
 */
class WalletRepositoryImpl(
    private val dao: WalletDao
) : WalletRepository {

    /**
     * Aggiunge una nuova voce al portafoglio.
     */
    override suspend fun addEntry(entry: WalletEntry) {
        dao.insert(entry.toEntity())
    }

    /**
     * Aggiorna una voce esistente nel portafoglio.
     */
    override suspend fun updateEntry(entry: WalletEntry) {
        dao.update(entry.toEntity())
    }

    /**
     * Rimuove una voce dal portafoglio.
     */
    override suspend fun removeEntry(entry: WalletEntry) {
        dao.delete(entry.toEntity())
    }

    /**
     * Restituisce tutte le voci del portafoglio come Flow osservabile.
     */
    override fun getAllEntries(): Flow<List<WalletEntry>> {
        return dao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    /**
     * Converte un modello di dominio in entità per il database.
     */
    private fun WalletEntry.toEntity() = WalletEntryEntity(
        id = this.id,
        currencyCode = this.currencyCode,
        currencyName = this.currencyName,
        amount = this.amount
    )

    /**
     * Converte un'entità del database in modello di dominio.
     */
    private fun WalletEntryEntity.toDomain() = WalletEntry(
        id = this.id,
        currencyCode = this.currencyCode,
        currencyName = this.currencyName,
        amount = this.amount
    )
}
