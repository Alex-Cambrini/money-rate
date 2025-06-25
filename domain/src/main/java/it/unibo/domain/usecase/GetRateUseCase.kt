package it.unibo.domain.usecase

import it.unibo.domain.repository.CurrencyRateRepository
import it.unibo.domain.model.CurrencyRate

interface ConvertCurrencyUseCase {
    suspend fun invoke(from: String, to: String): CurrencyRate
}

class ConvertCurrencyUseCaseImpl (
    private val currencyRepository: CurrencyRateRepository
): ConvertCurrencyUseCase {
    override suspend fun invoke(from: String, to: String): CurrencyRate {
        val rate = currencyRepository.getRate(from, to) ?: 0.0
        return CurrencyRate(from, to, rate)
    }
}
