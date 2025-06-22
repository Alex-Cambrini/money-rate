package it.unibo.domain.usecase

import it.unibo.domain.repository.CurrencyRepository

interface ConvertCurrencyUseCase {
    suspend fun invoke(from: String, to: String): CurrencyRate
}
