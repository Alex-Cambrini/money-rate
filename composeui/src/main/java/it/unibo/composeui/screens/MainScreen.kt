package it.unibo.composeui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import it.unibo.composeui.viewmodel.MainViewModel
import it.unibo.composeui.viewmodel.MainViewModelFactory
import it.unibo.data.di.RepositoryProviderImpl

@Composable
fun MainScreen(
    repositoryProvider: RepositoryProviderImpl
) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repositoryProvider.networkChecker))


    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ConnectionStatusBanner(viewModel)
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.weight(1f)
            ) {
                composable("home") {
                    HomeScreen(repositoryProvider.currencyRepository)
                }
                composable("wallet") {
                    WalletScreen(
                        currencyRepository = repositoryProvider.currencyRepository,
                        walletRepository = repositoryProvider.walletRepository
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Wallet, contentDescription = "Wallet") },
            label = { Text("Wallet") },
            selected = currentRoute == "wallet",
            onClick = { navController.navigate("wallet") }
        )
    }
}

@Composable
fun ConnectionStatusBanner(viewModel: MainViewModel) {
    val isConnected by viewModel.isConnected.collectAsState()

    if (!isConnected) {
        Surface(
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "No internet connection",
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}