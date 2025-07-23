package it.unibo.moneyrate.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import it.unibo.domain.repository.CurrencyRateRepository

/**
 * Factory per creare istanze del worker CurrencyUpdateWorker.
 * Fornisce al worker le dipendenze richieste, come il repository dei tassi di cambio.
 */
class CurrencyUpdateWorkerFactory(
    private val repository: CurrencyRateRepository
) : WorkerFactory() {

    /**
     * Crea e restituisce un'istanza di CurrencyUpdateWorker se richiesto,
     * altrimenti restituisce null per altri nomi di worker.
     */
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