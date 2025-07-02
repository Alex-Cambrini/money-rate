package it.unibo.domain.usecase

import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase

class InitializeAppDataUseCase(
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val getRateUseCase: GetRateUseCase
) {

    data class AppData(
        val currencies: List<Pair<String, String>>,
        val rates: Map<String, Double>
    )

    sealed class Result {
        data class Success(val data: AppData) : Result()
        data object Failure : Result()
    }

    suspend operator fun invoke(): Result {
        return try {
            val currenciesDomain = getAvailableCurrenciesUseCase.invoke()
            val currencies = currenciesDomain.map { it.code to it.name }

            val rates = mutableMapOf<String, Double>()
            for ((code, _) in currencies) {
                if (code != "EUR") {
                    val rate = getRateUseCase.invoke("EUR", code).rate
                    if (rate != 0.0) rates[code] = rate
                }
            }

            if (rates.isEmpty()) return Result.Failure

            Result.Success(AppData(currencies, rates))
        } catch (e: Exception) {
            Result.Failure
        }
    }
}
