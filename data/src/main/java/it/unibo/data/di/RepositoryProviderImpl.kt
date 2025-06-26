package it.unibo.data.di

import android.content.Context
import it.unibo.data.NetworkCheckerImpl
import it.unibo.data.local.db.AppDatabase
import it.unibo.domain.di.RepositoryProvider
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.data.remote.RetrofitClient
import it.unibo.data.repository.CurrencyRateRepositoryImpl
import it.unibo.data.repository.CurrencyRepositoryImpl
import it.unibo.domain.repository.WalletRepository
import it.unibo.data.repository.WalletRepositoryImpl
import it.unibo.domain.NetworkChecker
import it.unibo.domain.repository.CurrencyRateRepository

class RepositoryProviderImpl(private val context: Context) : RepositoryProvider {

    private val retrofitClient = RetrofitClient()
    private val database = AppDatabase.getInstance(context)
    private val currencyRateDao = database.currencyRateDao()
    private val currencyDao = database.currencyDao()
    private val walletDao = database.walletDao()

    override var currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        dao = currencyDao,
        api = retrofitClient.currencyApi
    )
    override val currencyRateRepository: CurrencyRateRepository = CurrencyRateRepositoryImpl(
        dao = currencyRateDao,
        api = retrofitClient.currencyRateApi
    )

    override var walletRepository: WalletRepository = WalletRepositoryImpl(
        dao = walletDao
    )
    override val networkChecker: NetworkChecker = NetworkCheckerImpl(context)
}

