package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.moneyrate.ui.theme.MoneyRateTheme
import it.unibo.composeui.screens.MainScreen
import it.unibo.composeui.screens.SplashScreen
import it.unibo.data.di.RepositoryProviderImpl

class MainActivity : ComponentActivity() {

    private lateinit var repositoryProvider: RepositoryProviderImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repositoryProvider = RepositoryProviderImpl(this)

        setContent {
            MoneyRateTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    MainScreen(repositoryProvider)
                }
            }
        }
    }
}
