package it.unibo.moneyrate.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import it.unibo.domain.repository.CurrencyRateRepository

class CurrencyUpdateWorkerFactory(
    private val repository: CurrencyRateRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == CurrencyUpdateWorker::class.java.name) {
            CurrencyUpdateWorker(appContext, workerParameters, repository)
        } else null
    }
}
