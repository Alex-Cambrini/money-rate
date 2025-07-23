package it.unibo.data.di

import android.content.Context
import it.unibo.data.NetworkCheckerImpl
import it.unibo.data.local.db.AppDatabase
import it.unibo.data.remote.RetrofitClient
import it.unibo.data.repository.CurrencyRateRepositoryImpl
import it.unibo.data.repository.CurrencyRepositoryImpl
import it.unibo.data.repository.WalletRepositoryImpl
import it.unibo.domain.NetworkChecker
import it.unibo.domain.di.RepositoryProvider
import it.unibo.domain.repository.CurrencyRateRepository
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository

/**
 * Implementazione di [RepositoryProvider] che fornisce repository e servizi
 * per l'accesso ai dati locali e remoti.
 *
 * @param context il contesto Android usato per creare il database e NetworkChecker
 */
class RepositoryProviderImpl(context: Context) : RepositoryProvider {

    private val database = AppDatabase.getInstance(context)
    private val currencyRateDao = database.currencyRateDao()
    private val currencyDao = database.currencyDao()
    private val walletDao = database.walletDao()

    /**
     * Repository per gestire le valute.
     */
    override var currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        dao = currencyDao,
        api = RetrofitClient.currencyApi
    )

    /**
     * Repository per gestire i tassi di cambio.
     */
    override val currencyRateRepository: CurrencyRateRepository = CurrencyRateRepositoryImpl(
        dao = currencyRateDao,
        api = RetrofitClient.currencyRateApi
    )

    /**
     * Repository per gestire il portafoglio.
     */
    override var walletRepository: WalletRepository = WalletRepositoryImpl(
        dao = walletDao
    )

    /**
     * Checker per verificare la disponibilità di rete.
     */
    override val networkChecker: NetworkChecker = NetworkCheckerImpl(context)
}
