package it.unibo.composeui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import it.unibo.composeui.resources.Strings
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.HomeViewModel
import it.unibo.composeui.viewmodel.HomeViewModelFactory
import it.unibo.composeui.viewmodel.MainViewModel
import it.unibo.composeui.viewmodel.MainViewModelFactory
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.composeui.viewmodel.WalletViewModelFactory
import it.unibo.data.NetworkCheckerImpl
import it.unibo.domain.di.UseCaseProvider
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen() {
    val navController = rememberNavController()

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
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(
                            UseCaseProvider.getRateUseCase,
                            UseCaseProvider.getAvailableCurrenciesUseCase
                        )
                    )
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
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    NetworkCheckerImpl(LocalContext.current)
                )
            )
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
            icon = { Icon(Icons.Filled.Home, contentDescription = Strings.HOME_DESCRIPTION) },
            label = { Text(Strings.HOME_LABEL) },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Wallet, contentDescription = Strings.WALLET_DESCRIPTION) },
            label = { Text(Strings.WALLET_LABEL) },
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
                    text = Strings.NO_CONNECTION,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
