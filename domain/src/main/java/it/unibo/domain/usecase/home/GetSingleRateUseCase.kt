package it.unibo.domain.usecase.home

import it.unibo.domain.usecase.currencyrate.GetRateUseCase

class GetSingleRateUseCase(
    private val getRateUseCase: GetRateUseCase
) {
    /**
     * Ottiene il tasso di cambio da base a to.
     * @param base valuta di partenza
     * @param to valuta di destinazione
     * @return tasso di cambio (Double)
     */
    suspend operator fun invoke(base: String, to: String): Double {
        return getRateUseCase.invoke(base, to).rate
    }
}
