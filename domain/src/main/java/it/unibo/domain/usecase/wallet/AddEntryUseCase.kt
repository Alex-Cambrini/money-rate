package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

interface AddEntryUseCase {
    suspend fun invoke(entry: WalletEntry)
}

class AddEntryUseCaseImpl(
    private val walletRepository: WalletRepository
) : AddEntryUseCase {
    override suspend fun invoke(entry: WalletEntry) {
        walletRepository.addEntry(entry)
    }
}
