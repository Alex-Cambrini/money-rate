package it.unibo.domain.usecase.currency

import it.unibo.domain.model.Currency
import it.unibo.domain.repository.CurrencyRepository

/**
 * Use case per ottenere la lista delle valute disponibili.
 */
interface GetAvailableCurrenciesUseCase {
    /**
     * Recupera la lista delle valute disponibili.
     * @return lista di oggetti Currency.
     */
    suspend fun invoke(): List<Currency>
}

/**
 * Implementazione del use case GetAvailableCurrenciesUseCase.
 * Fa da proxy verso il CurrencyRepository.
 */
class GetAvailableCurrenciesUseCaseImpl(
    private val currencyRepository: CurrencyRepository
) : GetAvailableCurrenciesUseCase {
    override suspend fun invoke(): List<Currency> {
        return currencyRepository.getAvailableCurrencies()
    }
}
