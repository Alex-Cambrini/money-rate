package it.unibo.domain.usecase.home

import it.unibo.domain.usecase.currencyrate.GetRateUseCase

class GetSingleRateUseCase(
    private val getRateUseCase: GetRateUseCase
) {
    suspend operator fun invoke(base: String, to: String): Double {
        return getRateUseCase.invoke(base, to).rate
    }
}