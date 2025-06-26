package it.unibo.moneyrate

import android.app.Application
import android.util.Log
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
        Log.d("CustomApplication", "Application started")

        UseCaseProvider.setup(
            repositoryProvider = RepositoryProviderImpl(context = this.applicationContext)
        )

        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.d("CustomApplication", "WorkManager enqueueUniquePeriodicWork called")
    }
}
