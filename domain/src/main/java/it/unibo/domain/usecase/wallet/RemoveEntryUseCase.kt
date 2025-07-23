package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

/**
 * Use case per rimuovere una voce dal portafoglio.
 */
interface RemoveEntryUseCase {
    /**
     * Rimuove una voce specifica.
     * @param entry Voce da rimuovere.
     */
    suspend fun invoke(entry: WalletEntry)
}

/**
 * Implementazione di RemoveEntryUseCase che usa WalletRepository.
 * @property walletRepository Repository per gestire le voci.
 */
class RemoveEntryUseCaseImpl(
    private val walletRepository: WalletRepository
) : RemoveEntryUseCase {
    override suspend fun invoke(entry: WalletEntry) {
        walletRepository.removeEntry(entry)
    }
}
