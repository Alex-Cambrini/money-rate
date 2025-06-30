package it.unibo.moneyrate.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.repository.CurrencyRateRepository

class CurrencyUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(
            "CurrencyUpdateWorker",
            "Worker started - attempting refresh of currency cache."
        ) //log for worker start
        val repository: CurrencyRateRepository =
            RepositoryProviderImpl(context).currencyRateRepository

        return try {
            val success = repository.refreshCache()
            if (success) {
                Log.d(
                    "CurrencyUpdateWorker",
                    "Refresh cache SUCCESS! Worker completed."
                ) // Log if refreshCache() returns true
                Result.success()
            } else {
                Log.e(
                    "CurrencyUpdateWorker",
                    "Refresh cache FAILED: repository.refreshCache() returned false. Retrying."
                ) // Log if refreshCache() returns false
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(
                "CurrencyUpdateWorker",
                "Refresh cache EXCEPTION: An error occurred during refresh. Retrying.",
                e
            ) // Log if an exception occurs
            Result.retry()
        }
    }
}