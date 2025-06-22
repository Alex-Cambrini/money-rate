package it.unibo.data.di

import android.content.Context
import it.unibo.data.local.db.AppDatabase
import it.unibo.domain.di.RepositoryProvider
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.data.remote.RetrofitClient
import it.unibo.data.repository.CurrencyRepositoryImpl

class RepositoryProviderImpl(private val context: Context) : RepositoryProvider {

    private val retrofitClient = RetrofitClient()
    private val database = AppDatabase.getInstance(context) // esempio database Room
    private val currencyRateDao = database.currencyRateDao()

    override var currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        dao = currencyRateDao,
        api = retrofitClient.currencyApi
    )
}
