package it.unibo.composeui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.unibo.composeui.R
import it.unibo.composeui.theme.Background
import it.unibo.composeui.theme.DarkBackground
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val amount by viewModel.amount.collectAsStateWithLifecycle(initialValue = "")
    val result by viewModel.result.collectAsStateWithLifecycle(initialValue = null)
    val top10Rates by viewModel.top10Rates.collectAsStateWithLifecycle(emptyMap())

    val scrollState = rememberScrollState()

    var baseCurrency by remember { mutableStateOf("EUR") }
    var targetCurrency by remember { mutableStateOf("USD") }

    val availableCurrencies by viewModel.currencies.collectAsStateWithLifecycle(emptyList())
    val focusManager = LocalFocusManager.current

    var baseExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrencies()
    }

    LaunchedEffect(availableCurrencies) {
        if (availableCurrencies.isNotEmpty()) {
            viewModel.loadAllRatesAgainstEuro(
                availableCurrencies.map { it.first }.filter { it != "EUR" }
            )
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimens.screenPadding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(Dimens.sectionSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
                ) {
                    CurrencyDropdown(
                        label = stringResource(R.string.base_currency),
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
                        label = stringResource(R.string.target_currency),
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

                Spacer(modifier = Modifier.width(Dimens.elementSpacing))

                Button(
                    onClick = {
                        val oldBase = baseCurrency
                        val oldTarget = targetCurrency
                        baseCurrency = oldTarget
                        targetCurrency = oldBase
                        viewModel.loadRate(oldTarget, oldBase)
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.SwapVert, contentDescription = stringResource(R.string.swap_currencies))
                }
            }

            AmountInput(
                amount = amount,
                onAmountChange = { newValue ->
                    if (newValue.all { it.isDigit() || it == '.' }) {
                        viewModel.updateAmount(newValue)
                    }
                }
            )

            ConvertButton(
                enabled = amount.toDoubleOrNull() != null,
                onClick = { viewModel.loadRate(baseCurrency, targetCurrency) }
            )

            ResultDisplay(result, amount, baseCurrency, targetCurrency)

            if (top10Rates.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.ordered_by_strength),
                    style = MaterialTheme.typography.titleMedium
                )
                CurrencyBarChart(top10Rates)
            }
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
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        TextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                focusedTrailingIconColor = textColor,
                unfocusedTrailingIconColor = textColor
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(backgroundColor)
        ) {
            currencies.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text("$code - $name", color = textColor) },
                    onClick = {
                        onCurrencySelected(code)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountInput(amount: String, onAmountChange: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    TextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text(stringResource(R.string.amount), color = textColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor,
            focusedTrailingIconColor = textColor,
            unfocusedTrailingIconColor = textColor
        )
    )
}

@Composable
fun ConvertButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.convert))
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

    val barColor = MaterialTheme.colorScheme.primary
    val highlightColor = MaterialTheme.colorScheme.error
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.barChartHeight)
            .padding(horizontal = Dimens.barChartHorizontalPadding)
    ) {
        val count = sortedRates.size
        val barWidth = size.width / (count * 1.5f)
        val spacing = (size.width - barWidth * count) / (count + 1)
        val maxHeight = size.height - Dimens.chartBottomPadding.toPx()

        sortedRates.forEachIndexed { i, (currency, rate) ->
            val normalized = (rate / maxRate).toFloat()
            val height = normalized * maxHeight
            val x = spacing + i * (barWidth + spacing)
            val y = maxHeight - height

            val color = if (rate == maxRate) highlightColor else barColor

            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, height)
            )

            val paintLabel = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = Dimens.BAR_LABEL_TEXT_SIZE
                isAntiAlias = true
            }
            paintLabel.color = textColor

            val paintValue = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = Dimens.BAR_VALUE_TEXT_SIZE
                isAntiAlias = true
            }
            paintValue.color = textColor

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