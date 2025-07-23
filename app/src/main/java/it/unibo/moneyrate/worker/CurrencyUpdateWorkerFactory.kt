package it.unibo.moneyrate.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import it.unibo.domain.repository.CurrencyRateRepository

// Factory che crea il worker CurrencyUpdateWorker fornendogli il repository necessario.
class CurrencyUpdateWorkerFactory(
    private val repository: CurrencyRateRepository
) : WorkerFactory() {

    // Crea un'istanza del worker solo se il nome della classe richiesta è CurrencyUpdateWorker, altrimenti restituisce null.
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CurrencyUpdateWorker::class.java.name ->
                CurrencyUpdateWorker(appContext, workerParameters, repository)

            else -> null
        }
    }
}