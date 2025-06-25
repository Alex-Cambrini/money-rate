package it.unibo.domain.usecase.currencyrate

import it.unibo.domain.model.CurrencyRate
import it.unibo.domain.repository.CurrencyRateRepository

interface ConvertCurrencyUseCase {
    suspend fun invoke(from: String, to: String): CurrencyRate
}

class ConvertCurrencyUseCaseImpl (
    private val currencyRateRepository: CurrencyRateRepository
): ConvertCurrencyUseCase {
    override suspend fun invoke(from: String, to: String): CurrencyRate {
        val rate = currencyRateRepository.getRate(from, to) ?: 0.0
        return CurrencyRate(from, to, rate)
    }
}
