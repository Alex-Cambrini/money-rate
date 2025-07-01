package it.unibo.moneyrate

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(15, TimeUnit.MINUTES).build()
        )
    }
}
