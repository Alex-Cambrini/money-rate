package it.unibo.domain.usecase

import it.unibo.domain.repository.CurrencyRepository

class ConvertCurrencyUseCase(
    private val repo: CurrencyRepository
) {
    suspend operator fun invoke(
        from: String,
        to: String,
        amount: Double
    ): Double {
        val rate = repo.getRate(from, to)
        return amount * rate
    }
}
