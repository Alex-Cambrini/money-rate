package it.unibo.composeui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import it.unibo.composeui.viewmodel.HomeViewModelFactory
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(repository: CurrencyRepository) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val rate by viewModel.rate.collectAsStateWithLifecycle(initialValue = null)
    val latestRates by viewModel.latestRates.collectAsStateWithLifecycle(emptyMap())
    val scrollState = rememberScrollState()

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

    LaunchedEffect(availableCurrencies) {
        if (availableCurrencies.isNotEmpty()) {
            viewModel.loadAllRatesAgainstEuro(
                availableCurrencies.map { it.first }.filter { it != "EUR" }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                    viewModel.loadRate(oldTarget, oldBase)
                },modifier = Modifier
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
        if (latestRates.isNotEmpty()) {
            Text(
                text = "Valute ordinate per forza (riferite a 1 EUR)",
                style = MaterialTheme.typography.titleMedium
            )
            val top10Rates = latestRates.entries
                .sortedBy { it.value }
                .take(10)
                .associate { it.key to it.value }

            CurrencyBarChart(top10Rates)
        }
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
    currencies: List<Pair<String, String>>,
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
            currencies.forEach { (code, name) ->
                DropdownMenuItem(text = { Text("$code - $name") }, onClick = {
                    onCurrencySelected(code)
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

@Composable
fun CurrencyBarChart(rates: Map<String, Double>) {
    if (rates.isEmpty()) return

    val sortedRates = rates.entries
        .sortedByDescending { it.value }
        .take(5)
    val maxRate = sortedRates.maxOf { it.value }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 8.dp)
    ) {
        val count = sortedRates.size
        val barWidth = size.width / (count * 1.5f)
        val spacing = (size.width - barWidth * count) / (count + 1)
        val maxHeight = size.height - 32.dp.toPx()

        sortedRates.forEachIndexed { i, (currency, rate) ->
            val normalized = (rate / maxRate).toFloat()
            val height = normalized * maxHeight
            val x = spacing + i * (barWidth + spacing)
            val y = maxHeight - height

            val color = if (rate == maxRate) Color.Red else Color(0xFF90CAF9)

            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, height)
            )

            val paintLabel = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 24f
            }

            val paintValue = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 22f
            }

            drawContext.canvas.nativeCanvas.drawText(
                currency,
                x + barWidth / 2,
                size.height,
                paintLabel
            )

            drawContext.canvas.nativeCanvas.drawText(
                "%.2f".format(rate),
                x + barWidth / 2,
                y - 6,
                paintValue
            )
        }
    }
}