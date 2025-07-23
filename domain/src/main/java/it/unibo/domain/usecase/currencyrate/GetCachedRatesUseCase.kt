package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.repository.CurrencyRateRepository

/**
 * Use case per ottenere le tariffe di cambio memorizzate nella cache.
 */
interface GetCachedRatesUseCase {
    /**
     * Recupera la mappa delle tariffe di cambio cached.
     * @return mappa con codice valuta e relativo tasso.
     */
    suspend fun invoke(): Map<String, Double>
}

/**
 * Implementazione del use case GetCachedRatesUseCase.
 * Recupera i dati dal CurrencyRateRepository.
 */
class GetCachedRatesUseCaseImpl(
    private val currencyRateRepository: CurrencyRateRepository
) : GetCachedRatesUseCase {
    override suspend fun invoke(): Map<String, Double> {
        return currencyRateRepository.getCachedRates()
    }
}
