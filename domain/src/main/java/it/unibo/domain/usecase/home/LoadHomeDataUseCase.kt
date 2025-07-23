package it.unibo.domain.usecase.home

import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCase
import it.unibo.domain.usecase.currencyrate.GetRateUseCase

/**
 * Carica valute disponibili e tassi di cambio rispetto a EUR.
 */
class LoadHomeDataUseCase(
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val getRateUseCase: GetRateUseCase
) {

    /**
     * Contenitore dati con valute e tassi.
     * @param currencies lista di coppie (codice, nome) delle valute
     * @param rates mappa codice valuta -> tasso di cambio rispetto a EUR
     */
    data class Data(
        val currencies: List<Pair<String, String>>,
        val rates: Map<String, Double>
    )

    /**
     * Ottiene valute e tassi, esclude EUR e tassi zero.
     * @return Result con Data o errore
     */
    suspend operator fun invoke(): Result<Data> {
        return try {
            // Ottiene le valute disponibili
            val currenciesDomain = getAvailableCurrenciesUseCase.invoke()
            val currencies = currenciesDomain.map { it.code to it.name }

            // Ottiene i tassi EUR->valuta, esclude EUR e tassi zero
            val rates = currencies
                .mapNotNull { (code, _) ->
                    if (code != "EUR") {
                        val rate = getRateUseCase.invoke("EUR", code).rate
                        if (rate != 0.0) code to rate else null
                    } else null
                }.toMap()

            // Verifica dati validi
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
