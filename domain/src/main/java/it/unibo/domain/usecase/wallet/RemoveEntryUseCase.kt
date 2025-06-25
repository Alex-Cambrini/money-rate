package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

interface RemoveEntryUseCase {
    suspend fun invoke(entry: WalletEntry)
}

class RemoveEntryUseCaseImpl(
    private val walletRepository: WalletRepository
) : RemoveEntryUseCase {
    override suspend fun invoke(entry: WalletEntry) {
        walletRepository.removeEntry(entry)
    }
}
