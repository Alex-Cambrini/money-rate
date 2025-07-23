package it.unibo.moneyrate.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.unibo.domain.repository.CurrencyRateRepository

// Worker che aggiorna periodicamente la cache dei tassi di cambio tramite WorkManager.
class CurrencyUpdateWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: CurrencyRateRepository
) : CoroutineWorker(context, params) {

    // Funzione principale eseguita dal Worker provando ad aggiornare la cache dei tassi.
    override suspend fun doWork(): Result {
        Log.d("CurrencyUpdateWorker", "Worker started - attempting refresh of currency cache.")

        return try {
            val success = repository.refreshCache()
            if (success) {
                Log.d("CurrencyUpdateWorker", "Refresh cache SUCCESS! Worker completed.")
                Result.success()
            } else {
                Log.e("CurrencyUpdateWorker", "Refresh cache FAILED. Retrying.")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("CurrencyUpdateWorker", "Refresh cache EXCEPTION. Retrying.", e)
            Result.retry()
        }
    }
}
