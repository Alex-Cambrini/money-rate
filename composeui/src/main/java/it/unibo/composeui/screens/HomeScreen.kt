package it.unibo.composeui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HelloScreen() {
    Text("Ciao, questa è la tua prima schermata Compose!")
}

@Preview(showBackground = true)
@Composable
fun HelloScreenPreview() {
    MaterialTheme {
        HelloScreen()
    }
}