package it.unibo.domain.usecase

import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.model.CurrencyRate

interface ConvertCurrencyUseCase {
    suspend fun invoke(from: String, to: String): CurrencyRate
}
class ConvertCurrencyUseCaseImpl (
    private val currencyRepository: CurrencyRepository
): ConvertCurrencyUseCase {
    override suspend fun invoke(from: String, to: String): CurrencyRate {
        val rate = currencyRepository.getRate(from, to)
        return CurrencyRate(from, to, rate)
    }
}
