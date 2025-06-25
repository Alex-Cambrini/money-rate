package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

interface UpdateEntryUseCase {
    suspend fun invoke(entry: WalletEntry)
}

class UpdateEntryUseCaseImpl(
    private val walletRepository: WalletRepository
) : UpdateEntryUseCase {
    override suspend fun invoke(entry: WalletEntry) {
        walletRepository.updateEntry(entry)
    }
}
