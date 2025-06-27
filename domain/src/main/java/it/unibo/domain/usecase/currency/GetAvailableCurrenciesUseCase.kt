package it.unibo.domain.usecase.currency

import it.unibo.domain.model.Currency
import it.unibo.domain.repository.CurrencyRepository

interface GetAvailableCurrenciesUseCase {
    suspend fun invoke(): List<Currency>
}

class GetAvailableCurrenciesUseCaseImpl(
    private val currencyRepository: CurrencyRepository
): GetAvailableCurrenciesUseCase {
    override suspend fun invoke(): List<Currency> {
        return currencyRepository.getAvailableCurrencies()
    }
}
