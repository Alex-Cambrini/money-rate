package it.unibo.composeui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.unibo.composeui.R
import it.unibo.composeui.components.CurrencyInputRow
import it.unibo.composeui.theme.DarkSurface
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.HomeViewModel
import java.util.Locale

/**
 * Schermata principale per la conversione di valute.
 * Permette di inserire un importo, selezionare due valute e visualizzare il risultato della conversione.
 * Mostra anche un grafico con le 5 valute che hanno il tasso di cambio più alto rispetto alla valuta base,
 * ovvero quelle con il valore unitario più basso.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val amount by viewModel.amount.collectAsStateWithLifecycle("")
    val result by viewModel.result.collectAsStateWithLifecycle(null)
    val top5Rates by viewModel.top5Rates.collectAsStateWithLifecycle(emptyMap())
    val availableCurrencies by viewModel.currencies.collectAsStateWithLifecycle(emptyList())
    val isLoadingRate by viewModel.isLoadingRate.collectAsStateWithLifecycle(false)

    var baseCurrency by remember { mutableStateOf("EUR") }
    var targetCurrency by remember { mutableStateOf("USD") }
    var baseAmount by remember { mutableStateOf(TextFieldValue(amount)) }
    var targetAmount by remember { mutableStateOf(TextFieldValue("")) }
    var lastInputSource by remember { mutableStateOf("base") }
    val cardBackground =
        if (isSystemInDarkTheme()) DarkSurface else MaterialTheme.colorScheme.surface

    val scrollState = rememberScrollState()

    var rotated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (rotated) 180f else 0f)

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
        viewModel.resetConversion()
        baseAmount = TextFieldValue("")
        targetAmount = TextFieldValue("")
    }

    LaunchedEffect(result) {
        if (result != null) {
            val formatter = java.text.NumberFormat.getNumberInstance(Locale.ITALY)
            formatter.maximumFractionDigits = 4
            if (lastInputSource == "base") {
                targetAmount = TextFieldValue(formatter.format(result))
            } else {
                baseAmount = TextFieldValue(formatter.format(result))
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(Dimens.sectionSpacing)
        ) {
            Text(
                text = stringResource(R.string.title_currency_converter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            // Card principale con input e bottone di conversione
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Input valuta di partenza
                            CurrencyInputRow(
                                currency = baseCurrency,
                                amount = baseAmount,
                                onCurrencyChange = { baseCurrency = it },
                                onAmountChange = {
                                    baseAmount = it
                                    lastInputSource = "base"
                                    viewModel.updateAmount(it.text)
                                },
                                currencies = availableCurrencies
                            )
                            // Input valuta di destinazione
                            CurrencyInputRow(
                                currency = targetCurrency,
                                amount = targetAmount,
                                onCurrencyChange = { targetCurrency = it },
                                onAmountChange = {
                                    targetAmount = it
                                    lastInputSource = "target"
                                    viewModel.updateAmount(it.text)
                                },
                                currencies = availableCurrencies
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Bottone per invertire le valute e aggiornare gli importi
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Button(onClick = {
                                rotated = !rotated
                                baseCurrency = targetCurrency.also { targetCurrency = baseCurrency }

                                val tempAmount = baseAmount
                                baseAmount = TextFieldValue(
                                    targetAmount.text,
                                    TextRange(targetAmount.text.length)
                                )
                                targetAmount = TextFieldValue(
                                    tempAmount.text,
                                    TextRange(tempAmount.text.length)
                                )

                                viewModel.resetResult()
                                viewModel.updateAmount(baseAmount.text)
                                viewModel.loadRate(baseCurrency, targetCurrency)
                            }) {
                                Icon(
                                    Icons.Filled.SwapVert,
                                    contentDescription = stringResource(R.string.swap_currencies),
                                    modifier = Modifier.rotate(rotation)
                                )
                            }
                        }
                    }

                    val inputText =
                        if (lastInputSource == "base") baseAmount.text else targetAmount.text
                    ConvertButton(
                        enabled = !isLoadingRate && inputText.replace(',', '.')
                            .toDoubleOrNull() != null
                    ) {
                        if (lastInputSource == "base") {
                            viewModel.loadRate(baseCurrency, targetCurrency)
                        } else {
                            viewModel.loadRate(targetCurrency, baseCurrency)
                        }
                    }
                }
            }

            if (top5Rates.isNotEmpty()) {
                Text(
                    stringResource(R.string.ordered_by_strength),
                    style = MaterialTheme.typography.titleMedium
                )
                CurrencyBarChart(top5Rates)
            }
        }
    }
}

/**
 * Bottone per eseguire la conversione. Disabilitato se l'input non è valido o se è in caricamento.
 */
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

/**
 * Disegna un grafico a barre con le 5 valute con il tasso di cambio più alto rispetto all'euro.
 * Queste valute hanno il valore unitario più basso (cioè servono più unità per fare 1 euro).
 */
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