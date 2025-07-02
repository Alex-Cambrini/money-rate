package it.unibo.domain.usecase.home

class CalculateTopRatesUseCase {
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
