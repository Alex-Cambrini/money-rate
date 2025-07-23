package it.unibo.domain.usecase.home

/**
 * Use case per ottenere i tassi più bassi fino a un limite specificato.
 */
class CalculateTopRatesUseCase {
    /**
     * Restituisce i top tassi più bassi fino al limite specificato.
     * @param rates mappa valuta -> tasso
     * @param limit numero massimo di tassi da restituire (default 10)
     * @return mappa dei top tassi ordinati per valore crescente
     */
    operator fun invoke(
        rates: Map<String, Double>,
        limit: Int = 10
    ): Map<String, Double> {
        return rates.entries
            .sortedBy { it.value }
            .take(limit)
            .associate { it.key to it.value }
    }
}
