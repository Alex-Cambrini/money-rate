package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository

/**
 * Use case per aggiornare l'importo di una voce del portafoglio.
 */
interface UpdateWalletAmountUseCase {
    /**
     * Aggiorna l'importo di una voce sommando delta.
     * @param entry Voce da aggiornare.
     * @param delta Valore da sommare all'importo corrente.
     * @throws IllegalArgumentException se il nuovo importo è inferiore a 0.01.
     */
    suspend fun invoke(entry: WalletEntry, delta: Double)
}

/**
 * Implementazione di UpdateWalletAmountUseCase che verifica il limite minimo e aggiorna la voce.
 * @property walletRepository Repository per modificare i dati.
 */
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
