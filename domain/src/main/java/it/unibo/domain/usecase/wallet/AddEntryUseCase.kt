package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

/**
 * Interfaccia per aggiungere una voce al portafoglio.
 */
interface AddEntryUseCase {
    /**
     * Aggiunge una voce al portafoglio.
     * @param entry Voce da aggiungere.
     */
    suspend fun invoke(entry: WalletEntry)
}

/**
 * Implementazione di AddEntryUseCase che usa WalletRepository.
 * @property walletRepository Repository per gestire i dati del portafoglio.
 */
class AddEntryUseCaseImpl(
    private val walletRepository: WalletRepository
) : AddEntryUseCase {
    override suspend fun invoke(entry: WalletEntry) {
        walletRepository.addEntry(entry)
    }
}
