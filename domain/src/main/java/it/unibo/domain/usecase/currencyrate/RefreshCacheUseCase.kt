package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRateRepository

interface RefreshCacheUseCase {
    suspend fun invoke(): Boolean
}

class RefreshCacheUseCaseImpl (
    private val currencyRateRepository: CurrencyRateRepository
): RefreshCacheUseCase {
    override suspend fun invoke(): Boolean {
        return currencyRateRepository.refreshCache()
    }
}
