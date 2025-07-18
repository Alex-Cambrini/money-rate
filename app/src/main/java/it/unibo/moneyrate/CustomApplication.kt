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


class CustomApplication : Application(), Configuration.Provider {

    private val repositoryProvider by lazy { RepositoryProviderImpl(this) }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                CurrencyUpdateWorkerFactory(repositoryProvider.currencyRateRepository)
            )
            .build()


    override fun onCreate() {
        super.onCreate()
        UseCaseProvider.setup(repositoryProvider)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(15, TimeUnit.MINUTES)
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
