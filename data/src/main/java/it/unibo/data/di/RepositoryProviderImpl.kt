package it.unibo.data.di

import android.content.Context
import it.unibo.data.NetworkCheckerImpl
import it.unibo.data.local.db.AppDatabase
import it.unibo.domain.di.RepositoryProvider
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.data.remote.RetrofitClient
import it.unibo.data.repository.CurrencyRepositoryImpl
import it.unibo.domain.repository.WalletRepository
import it.unibo.data.repository.WalletRepositoryImpl
import it.unibo.domain.NetworkChecker

class RepositoryProviderImpl(private val context: Context) : RepositoryProvider {

    private val retrofitClient = RetrofitClient()
    private val database = AppDatabase.getInstance(context)
    private val currencyRateDao = database.currencyRateDao()
    private val walletDao = database.walletDao()

    override var currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        dao = currencyRateDao,
        api = retrofitClient.currencyApi
    )

    override var walletRepository: WalletRepository = WalletRepositoryImpl(
        dao = walletDao
    )
    override val networkChecker: NetworkChecker = NetworkCheckerImpl(context)
}

