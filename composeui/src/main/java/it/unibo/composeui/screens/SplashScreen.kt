package it.unibo.composeui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import it.unibo.composeui.R
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.SplashViewModel

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.splash_title),
                fontSize = Dimens.splashTitleSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(Dimens.splashSpacerHeight))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SplashScreenWithError(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.splash_title),
                fontSize = Dimens.splashTitleSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(Dimens.splashSpacerHeight))
            Text(
                text = stringResource(R.string.loading_failed),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(Dimens.splashSpacerHeight))
            androidx.compose.material3.Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun SplashScreenHost(
    splashViewModel: SplashViewModel,
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        splashViewModel.initialize()
    }

    val state by splashViewModel.state.collectAsState()

    when (state) {
        is SplashViewModel.State.Loading -> SplashScreen()
        is SplashViewModel.State.Error -> SplashScreenWithError(onRetry = { splashViewModel.initialize() })
        is SplashViewModel.State.Success -> {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }
    }
}

