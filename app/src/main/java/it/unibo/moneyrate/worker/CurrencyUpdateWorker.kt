package it.unibo.moneyrate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.repository.CurrencyRepository

class CurrencyUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository: CurrencyRepository =
            RepositoryProviderImpl(context).currencyRepository

        return try {
            repository.getAllRates("EUR")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}