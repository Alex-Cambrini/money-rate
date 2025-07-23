package it.unibo.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modello della risposta dell'API Frankfurter per i tassi di cambio.
 *
 * @property amount valore su cui sono calcolati i tassi (di solito 1.0)
 * @property base valuta di riferimento
 * @property date data della rilevazione
 * @property rates mappa dei tassi di cambio (valuta -> tasso)
 */
@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse(
    @Json(name = "amount") val amount: Double,
    @Json(name = "base") val base: String,
    @Json(name = "date") val date: String,
    @Json(name = "rates") val rates: Map<String, Double>
)
