package it.unibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.unibo.data.local.entity.CurrencyRateEntity

/**
 * DAO per gestire i tassi di cambio nel database locale.
 */
@Dao
interface CurrencyRateDao {

    /**
     * Inserisce o aggiorna la lista di tassi di cambio.
     * Sovrascrive le voci esistenti in caso di conflitto.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    /**
     * Recupera tutti i tassi con base specificata.
     *
     * @param base valuta di riferimento
     * @return lista di tassi relativi alla base
     */
    @Query("SELECT * FROM currency_rates WHERE base = :base")
    suspend fun getRatesByBase(base: String): List<CurrencyRateEntity>

    /**
     * Recupera il tasso di cambio specifico tra base e valuta target.
     *
     * @param base valuta di partenza
     * @param toCurrency valuta di destinazione
     * @return tasso di cambio o null se non trovato
     */
    @Query("SELECT rate FROM currency_rates WHERE base = :base AND currencyCode = :toCurrency LIMIT 1")
    suspend fun getRate(base: String, toCurrency: String): Double?

    /**
     * Elimina tutti i tassi associati a una data valuta base.
     * @param base valuta di riferimento
     */
    @Query("DELETE FROM currency_rates WHERE base = :base")
    suspend fun clearRatesByBase(base: String)
}
