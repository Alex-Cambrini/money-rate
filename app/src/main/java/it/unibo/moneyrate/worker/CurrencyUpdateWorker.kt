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
        ) // Log iniziale più specifico
        val repository: CurrencyRateRepository =
            RepositoryProviderImpl(context).currencyRateRepository

        return try {
            val success = repository.refreshCache()
            if (success) {
                Log.d(
                    "CurrencyUpdateWorker",
                    "Refresh cache SUCCESS! Worker completed."
                ) // Log in caso di successo
                Result.success()
            } else {
                Log.e(
                    "CurrencyUpdateWorker",
                    "Refresh cache FAILED: repository.refreshCache() returned false. Retrying."
                ) // Log se refreshCache() restituisce false
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(
                "CurrencyUpdateWorker",
                "Refresh cache EXCEPTION: An error occurred during refresh. Retrying.",
                e
            ) // Log in caso di eccezione, con stack trace
            Result.retry()
        }
    }
}