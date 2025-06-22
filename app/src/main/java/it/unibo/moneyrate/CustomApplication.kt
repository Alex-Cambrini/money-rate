package it.unibo.moneyrate

import android.app.Application
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.di.UseCaseProvider

class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        UseCaseProvider.setup(
            repositoryProvider = RepositoryProviderImpl(context = this.applicationContext)
        )
    }
}