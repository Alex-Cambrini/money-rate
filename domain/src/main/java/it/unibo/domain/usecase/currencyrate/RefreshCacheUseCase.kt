package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.repository.CurrencyRateRepository

/**
 * Use case per aggiornare la cache dei tassi di cambio.
 */
interface RefreshCacheUseCase {
    /**
     * Aggiorna la cache e restituisce true se ha avuto successo.
     */
    suspend fun invoke(): Boolean
}

/**
 * Implementazione del use case RefreshCacheUseCase.
 * Invoca il repository per aggiornare la cache.
 */
class RefreshCacheUseCaseImpl(
    private val currencyRateRepository: CurrencyRateRepository
) : RefreshCacheUseCase {
    override suspend fun invoke(): Boolean {
        return currencyRateRepository.refreshCache()
    }
}
