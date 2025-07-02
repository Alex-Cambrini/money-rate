package it.unibo.composeui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.unibo.composeui.R
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.HomeViewModel
import it.unibo.composeui.viewmodel.HomeViewModelFactory
import it.unibo.composeui.viewmodel.MainViewModel
import it.unibo.composeui.viewmodel.MainViewModelFactory
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.composeui.viewmodel.WalletViewModelFactory
import it.unibo.data.NetworkCheckerImpl
import it.unibo.domain.di.UseCaseProvider

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            UseCaseProvider.getRateUseCase,
            UseCaseProvider.getAvailableCurrenciesUseCase
        )
    )

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            NetworkCheckerImpl(LocalContext.current)
        )
    )

    val isDataReady by homeViewModel.isDataReady.collectAsState()
    val isError by homeViewModel.isError.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.initializeIfNeeded()
    }

    if (!isDataReady) {
        if (isError) {
            SplashScreenWithError(
                onRetry = { homeViewModel.initializeIfNeeded() }
            )
        } else {
            SplashScreen()
        }
        return
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    HomeScreen(viewModel = homeViewModel)
                }
                composable("wallet") {
                    val walletViewModel: WalletViewModel = viewModel(
                        factory = WalletViewModelFactory(
                            UseCaseProvider.addEntryUseCase,
                            UseCaseProvider.updateEntryUseCase,
                            UseCaseProvider.removeEntryUseCase,
                            UseCaseProvider.getAllEntriesUseCase,
                            UseCaseProvider.getAvailableCurrenciesUseCase,
                            UseCaseProvider.refreshCacheUseCase,
                            UseCaseProvider.getCachedRatesUseCase
                        )
                    )
                    WalletScreen(viewModel = walletViewModel)
                }
            }

            ConnectionStatusBanner(
                viewModel = mainViewModel,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = stringResource(R.string.home_description)
                )
            },
            label = { Text(stringResource(R.string.home_label)) },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Wallet,
                    contentDescription = stringResource(R.string.wallet_description)
                )
            },
            label = { Text(stringResource(R.string.wallet_label)) },
            selected = currentRoute == "wallet",
            onClick = { navController.navigate("wallet") }
        )
    }
}

@Composable
fun ConnectionStatusBanner(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val isConnected by viewModel.isConnected.collectAsState()

    if (!isConnected) {
        Surface(
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
                .fillMaxWidth()
                .height(Dimens.connectionBannerHeight)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.no_connection),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
