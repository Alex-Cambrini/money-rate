package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

interface UpdateWalletAmountUseCase {
    suspend fun invoke(entry: WalletEntry, delta: Double)
}

class UpdateWalletAmountUseCaseImpl(
    private val walletRepository: WalletRepository
) : UpdateWalletAmountUseCase {

    companion object {
        private const val MIN_AMOUNT = 0.01
    }

    override suspend fun invoke(entry: WalletEntry, delta: Double) {
        val newAmount = entry.amount + delta
        if (newAmount < MIN_AMOUNT) {
            throw IllegalArgumentException("The balance must be at least $MIN_AMOUNT")
        }

        val updatedEntry = entry.copy(amount = newAmount)
        walletRepository.updateEntry(updatedEntry)
    }
}
