package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import it.unibo.composeui.screens.MainScreen
import it.unibo.composeui.screens.SplashScreenHost
import it.unibo.composeui.theme.MoneyRateTheme
import it.unibo.composeui.viewmodel.HomeViewModel
import it.unibo.composeui.viewmodel.HomeViewModelFactory
import it.unibo.composeui.viewmodel.MainViewModel
import it.unibo.composeui.viewmodel.MainViewModelFactory
import it.unibo.composeui.viewmodel.SplashViewModel
import it.unibo.composeui.viewmodel.SplashViewModelFactory
import it.unibo.data.NetworkCheckerImpl
import it.unibo.domain.di.UseCaseProvider
import it.unibo.domain.usecase.InitializeAppDataUseCase
import it.unibo.domain.usecase.home.CalculateConversionUseCase
import it.unibo.domain.usecase.home.CalculateTopRatesUseCase
import it.unibo.domain.usecase.home.GetSingleRateUseCase
import it.unibo.domain.usecase.home.LoadHomeDataUseCase

/**
 * Activity principale dell'app.
 * Avvia l'interfaccia utente impostando il tema e il punto d'ingresso dell'applicazione.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Imposta il contenuto della UI con il tema dell'app
        setContent {
            MoneyRateTheme {
                AppEntryPoint()
            }
        }
    }
}

/**
 * Punto d'ingresso composable dell'app.
 * Inizializza i ViewModel e decide se mostrare la schermata splash o quella principale.
 */
@Composable
fun AppEntryPoint() {
    val context = LocalContext.current

    val loadHomeDataUseCase = LoadHomeDataUseCase(
        UseCaseProvider.getAvailableCurrenciesUseCase,
        UseCaseProvider.getRateUseCase
    )
    val calculateTopRatesUseCase = CalculateTopRatesUseCase()
    val calculateConversionUseCase = CalculateConversionUseCase()
    val getSingleRateUseCase = GetSingleRateUseCase(UseCaseProvider.getRateUseCase)

    val initializeAppDataUseCase = InitializeAppDataUseCase(
        UseCaseProvider.getAvailableCurrenciesUseCase,
        UseCaseProvider.getRateUseCase
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            loadHomeDataUseCase,
            calculateTopRatesUseCase,
            calculateConversionUseCase,
            getSingleRateUseCase
        )
    )

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(NetworkCheckerImpl(context))
    )

    val splashViewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(
            initializeAppDataUseCase
        )
    )

    val showMainScreen = remember { mutableStateOf(false) }

    if (showMainScreen.value) {
        MainScreen(homeViewModel, mainViewModel)
    } else {
        SplashScreenHost(
            splashViewModel = splashViewModel,
            onNavigateToHome = { showMainScreen.value = true }
        )
    }
}
