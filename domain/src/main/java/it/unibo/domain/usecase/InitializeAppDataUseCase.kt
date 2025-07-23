package it.unibo.domain.usecase

import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase

/**
 * Use case per inizializzare i dati dell'app.
 * Recupera le valute disponibili e i relativi tassi di cambio rispetto all'EUR.
 */
class InitializeAppDataUseCase(
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val getRateUseCase: GetRateUseCase
) {

    /**
     * Dati dell'app contenenti valute e tassi di cambio.
     */
    data class AppData(
        val currencies: List<Pair<String, String>>, // codice e nome valuta
        val rates: Map<String, Double> // tassi di cambio rispetto a EUR
    )

    /**
     * Risultato dell'inizializzazione: successo con dati o fallimento.
     */
    sealed class Result {
        data class Success(val data: AppData) : Result()
        data object Failure : Result()
    }

    /**
     * Esegue l'inizializzazione.
     * @return Result con i dati o Failure in caso di errore o assenza di tassi.
     */
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

