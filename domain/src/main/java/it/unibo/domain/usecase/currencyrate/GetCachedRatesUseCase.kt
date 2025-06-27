package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.repository.CurrencyRateRepository

interface GetCachedRatesUseCase {
    suspend fun invoke(): Map<String, Double>
}

class GetCachedRatesUseCaseImpl(
    private val currencyRateRepository: CurrencyRateRepository
) : GetCachedRatesUseCase {
    override suspend fun invoke(): Map<String, Double> {
        return currencyRateRepository.getCachedRates()
    }
}
