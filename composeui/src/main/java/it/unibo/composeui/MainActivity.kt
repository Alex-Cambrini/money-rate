package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import it.unibo.composeui.screens.HomeScreen
import it.unibo.data.di.RepositoryProviderImpl

class MainActivity : ComponentActivity() {

    private lateinit var repositoryProvider: RepositoryProviderImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repositoryProvider = RepositoryProviderImpl(this)

        setContent {
            HomeScreen(repositoryProvider.currencyRepository)
        }
    }
}
