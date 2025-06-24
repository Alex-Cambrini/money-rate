package it.unibo.data.repository

import it.unibo.data.local.dao.WalletDao
import it.unibo.data.local.entity.WalletEntryEntity
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepositoryImpl(
    private val dao: WalletDao
) : WalletRepository {

    override suspend fun addEntry(entry: WalletEntry) {
        dao.insert(entry.toEntity())
    }

    override suspend fun updateEntry(entry: WalletEntry) {
        dao.update(entry.toEntity())
    }

    override suspend fun removeEntry(entry: WalletEntry) {
        dao.delete(entry.toEntity())
    }

    override fun getAllEntries(): Flow<List<WalletEntry>> {
        return dao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun WalletEntry.toEntity() = WalletEntryEntity(
        id = this.id,
        currency = this.currency,
        amount = this.amount
    )

    private fun WalletEntryEntity.toDomain() = WalletEntry(
        id = this.id,
        currency = this.currency,
        amount = this.amount
    )
}
