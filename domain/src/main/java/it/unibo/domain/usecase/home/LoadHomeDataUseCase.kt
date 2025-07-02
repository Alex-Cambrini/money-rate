package it.unibo.domain.usecase.home

import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase

class LoadHomeDataUseCase(
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val getRateUseCase: GetRateUseCase
) {

    data class Data(
        val currencies: List<Pair<String, String>>,
        val rates: Map<String, Double>
    )

    suspend operator fun invoke(): Result<Data> {
        return try {
            val currenciesDomain = getAvailableCurrenciesUseCase.invoke()
            val currencies = currenciesDomain.map { it.code to it.name }

            val rates = currencies
                .mapNotNull { (code, _) ->
                    if (code != "EUR") {
                        val rate = getRateUseCase.invoke("EUR", code).rate
                        if (rate != 0.0) code to rate else null
                    } else null
                }.toMap()

            if (currencies.isEmpty() || rates.isEmpty()) {
                Result.failure(Exception("No currencies or rates available"))
            } else {
                Result.success(Data(currencies, rates))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
