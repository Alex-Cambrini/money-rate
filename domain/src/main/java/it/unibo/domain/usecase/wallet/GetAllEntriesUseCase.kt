package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case per ottenere tutte le voci del portafoglio.
 */
interface GetAllEntriesUseCase {
    /**
     * Restituisce un Flow con la lista aggiornata delle voci.
     * @return Flow che emette la lista delle WalletEntry.
     */
    fun invoke(): Flow<List<WalletEntry>>
}

/**
 * Implementazione di GetAllEntriesUseCase che usa WalletRepository.
 * @property walletRepository Repository per accedere alle voci.
 */
class GetAllEntriesUseCaseImpl(
    private val walletRepository: WalletRepository
) : GetAllEntriesUseCase {
    override fun invoke(): Flow<List<WalletEntry>> {
        return walletRepository.getAllEntries()
    }
}
