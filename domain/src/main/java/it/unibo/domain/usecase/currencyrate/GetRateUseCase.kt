package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRateRepository

/**
 * Use case per ottenere il tasso di cambio tra due valute.
 */
interface GetRateUseCase {
    /**
     * Recupera il tasso di cambio da 'from' a 'to'.
     * @param from valuta di partenza
     * @param to valuta di destinazione
     * @return oggetto CurrencyRate con il tasso; 0.0 se non disponibile
     */
    suspend fun invoke(from: String, to: String): CurrencyRate
}

/**
 * Implementazione del use case GetRateUseCase.
 * Richiama il repository per ottenere il tasso.
 */
class GetRateUseCaseImpl(
    private val currencyRateRepository: CurrencyRateRepository
) : GetRateUseCase {
    override suspend fun invoke(from: String, to: String): CurrencyRate {
        val rate = currencyRateRepository.getRate(from, to) ?: 0.0
        return CurrencyRate(from, to, rate)
    }
}
