package it.unibo.moneyrate

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.di.UseCaseProvider
import it.unibo.moneyrate.worker.CurrencyUpdateWorker
import java.util.concurrent.TimeUnit

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        UseCaseProvider.setup(
            repositoryProvider = RepositoryProviderImpl(context = this.applicationContext)
        )

        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(
            3, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
