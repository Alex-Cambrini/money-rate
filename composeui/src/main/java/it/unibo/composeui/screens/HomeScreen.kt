package it.unibo.composeui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.unibo.composeui.viewmodel.HomeViewModel
import it.unibo.domain.repository.CurrencyRepository
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import it.unibo.composeui.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(repository: CurrencyRepository) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val rate by viewModel.rate.collectAsStateWithLifecycle(initialValue = null)

    var baseCurrency by remember { mutableStateOf("EUR") }
    var targetCurrency by remember { mutableStateOf("USD") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Double?>(null) }

    val availableCurrencies by viewModel.currencies.collectAsStateWithLifecycle(emptyList())
    val focusManager = LocalFocusManager.current

    var baseExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.loadCurrencies()
    }

    LaunchedEffect(rate, amount) {
        val currentRate = rate
        val amountDouble = amount.toDoubleOrNull()
        result =
            if (amountDouble != null && currentRate != null) amountDouble * currentRate else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CurrencyDropdown(
                    label = "Base Currency",
                    selectedCurrency = baseCurrency,
                    expanded = baseExpanded,
                    onExpandedChange = { expanded ->
                        baseExpanded = expanded
                        if (expanded) focusManager.clearFocus(true)
                    },
                    onCurrencySelected = {
                        baseCurrency = it
                        baseExpanded = false
                    },
                    currencies = availableCurrencies,
                    modifier = Modifier.fillMaxWidth()
                )

                CurrencyDropdown(
                    label = "Target Currency",
                    selectedCurrency = targetCurrency,
                    expanded = targetExpanded,
                    onExpandedChange = { expanded ->
                        targetExpanded = expanded
                        if (expanded) focusManager.clearFocus(true)
                    },
                    onCurrencySelected = {
                        targetCurrency = it
                        targetExpanded = false
                    },
                    currencies = availableCurrencies,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val oldBase = baseCurrency
                    val oldTarget = targetCurrency
                    baseCurrency = oldTarget
                    targetCurrency = oldBase
                    viewModel.loadRate(oldBase, oldTarget)
                }, modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Filled.SwapVert, contentDescription = "Swap currencies")
            }
        }

        AmountInput(amount) { newValue ->
            if (newValue.all { it.isDigit() || it == '.' }) amount = newValue
        }

        ConvertButton(

            enabled = amount.toDoubleOrNull() != null,

            onClick = {

                viewModel.loadRate(baseCurrency, targetCurrency)

            }

        )

        ResultDisplay(result, amount, baseCurrency, targetCurrency)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    selectedCurrency: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCurrencySelected: (String) -> Unit,
    currencies: List<String>,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = onExpandedChange, modifier = modifier
    ) {
        TextField(value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            currencies.forEach { currency ->
                DropdownMenuItem(text = { Text(currency) }, onClick = {
                    onCurrencySelected(currency)
                    onExpandedChange(false)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountInput(amount: String, onAmountChange: (String) -> Unit) {
    TextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text("Amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ConvertButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Convert")
    }
}

@Composable
fun ResultDisplay(result: Double?, amount: String, baseCurrency: String, targetCurrency: String) {
    result?.let {
        Text(
            text = "$amount $baseCurrency = ${"%.4f".format(it)} $targetCurrency",
            style = MaterialTheme.typography.titleMedium
        )
    }
}