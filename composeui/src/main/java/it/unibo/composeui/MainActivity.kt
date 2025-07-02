package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import it.unibo.composeui.screens.MainScreen
import it.unibo.composeui.theme.MoneyRateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoneyRateTheme {
                MainScreen()
            }
        }
    }
}
