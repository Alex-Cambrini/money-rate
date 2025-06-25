package it.unibo.domain.usecase.wallet

import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

interface GetAllEntriesUseCase {
    fun invoke(): Flow<List<WalletEntry>>
}

class GetAllEntriesUseCaseImpl(
    private val walletRepository: WalletRepository
) : GetAllEntriesUseCase {
    override fun invoke(): Flow<List<WalletEntry>> {
        return walletRepository.getAllEntries()
    }
}
