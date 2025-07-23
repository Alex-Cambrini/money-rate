package it.unibo.moneyrate

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.di.UseCaseProvider
import it.unibo.moneyrate.worker.CurrencyUpdateWorker
import it.unibo.moneyrate.worker.CurrencyUpdateWorkerFactory
import java.util.concurrent.TimeUnit

/**
 * Application che inizializza le dipendenze e configura WorkManager.
 * Registra il worker periodico per aggiornare la cache dei tassi di cambio.
 */
class CustomApplication : Application(), Configuration.Provider {

    private val repositoryProvider by lazy { RepositoryProviderImpl(this) }

    companion object {
        private const val UPDATE_INTERVAL_MINUTES = 15L
    }

    /**
     * Configura WorkManager con una factory per fornire le dipendenze ai worker.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                CurrencyUpdateWorkerFactory(repositoryProvider.currencyRateRepository)
            )
            .build()

    /**
     * Inizializza i use case e avvia un worker periodico per aggiornare i tassi.
     * Il worker verrà eseguito ogni 15 minuti se c'è una connessione di rete attiva.
     */
    override fun onCreate() {
        super.onCreate()
        UseCaseProvider.setup(repositoryProvider)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(
            UPDATE_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
