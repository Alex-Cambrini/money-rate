package it.unibo.domain.usecase

import it.unibo.domain.repository.CurrencyRepository

interface GetAvailableCurrenciesUseCase {
    suspend fun invoke(): Map<String, String>
}

class GetAvailableCurrenciesUseCaseImpl(
    private val currencyRepository: CurrencyRepository
): GetAvailableCurrenciesUseCase {
    override suspend fun invoke(): Map<String, String> {
        return currencyRepository.getAvailableCurrencies()
    }
}
