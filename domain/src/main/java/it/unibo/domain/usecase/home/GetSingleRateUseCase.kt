package it.unibo.domain.usecase.home

import it.unibo.domain.usecase.currencyrate.GetRateUseCase

/**
 * Use case per ottenere il tasso di cambio tra due valute.
 */
class GetSingleRateUseCase(
    private val getRateUseCase: GetRateUseCase
) {
    /**
     * Restituisce il tasso di cambio da una valuta base a una valuta di destinazione.
     *
     * @param base valuta di partenza (es. "EUR")
     * @param to valuta di destinazione (es. "USD")
     * @return tasso di cambio come Double
     */
    suspend operator fun invoke(base: String, to: String): Double {
        return getRateUseCase.invoke(base, to).rate
    }
}
