package it.unibo.moneyrate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.data.repository.CurrencyRepositoryImpl
import it.unibo.moneyrate.network.RetrofitInstance
import it.unibo.data.local.db.AppDatabase

class CurrencyUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getInstance(context).currencyRateDao()
        val api = RetrofitInstance.api
        val repository: CurrencyRepository = CurrencyRepositoryImpl(dao, api)

        return try {
            repository.getAllRates("EUR")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}