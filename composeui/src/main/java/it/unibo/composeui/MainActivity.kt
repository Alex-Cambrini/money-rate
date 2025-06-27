package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import it.unibo.composeui.theme.MoneyRateTheme
import it.unibo.composeui.screens.MainScreen
import it.unibo.composeui.screens.SplashScreen
import it.unibo.data.di.RepositoryProviderImpl
import it.unibo.domain.di.UseCaseProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoneyRateTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    MainScreen()
                }
            }
        }
    }
}
