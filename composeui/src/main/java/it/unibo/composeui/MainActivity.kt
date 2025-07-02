package it.unibo.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import it.unibo.composeui.screens.SplashScreenHost
import it.unibo.composeui.screens.MainScreen
import it.unibo.composeui.theme.MoneyRateTheme
import it.unibo.composeui.viewmodel.HomeViewModel
import it.unibo.composeui.viewmodel.MainViewModel
import it.unibo.composeui.viewmodel.MainViewModelFactory
import it.unibo.composeui.viewmodel.SplashViewModel
import it.unibo.data.NetworkCheckerImpl
import it.unibo.domain.di.UseCaseProvider
import it.unibo.composeui.viewmodel.HomeViewModelFactory
import it.unibo.composeui.viewmodel.SplashViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoneyRateTheme {
                AppEntryPoint()
            }
        }
    }
}

@Composable
fun AppEntryPoint() {
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            UseCaseProvider.getRateUseCase,
            UseCaseProvider.getAvailableCurrenciesUseCase
        )
    )

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(NetworkCheckerImpl(context))
    )

    val splashViewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(
            UseCaseProvider.getRateUseCase,
            UseCaseProvider.getAvailableCurrenciesUseCase
        )
    )

    val showMainScreen = remember { mutableStateOf(false) }

    if (showMainScreen.value) {
        MainScreen(homeViewModel, mainViewModel)
    } else {
        SplashScreenHost(
            splashViewModel = splashViewModel,
            homeViewModel = homeViewModel,
            onNavigateToHome = {
                homeViewModel.setInitialData(
                    splashViewModel.currencies.value,
                    splashViewModel.latestRates.value
                )
                showMainScreen.value = true
            }
        )
    }
}